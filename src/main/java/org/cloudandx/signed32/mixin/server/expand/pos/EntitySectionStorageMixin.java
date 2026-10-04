package org.cloudandx.signed32.mixin.server.expand.pos;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import net.minecraft.core.SectionPos;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.phys.AABB;
import org.cloudandx.signed32.config.Signed32Config;
import org.cloudandx.signed32.util.pos.IntSectionPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntitySectionStorage.class)
public abstract class EntitySectionStorageMixin<T extends EntityAccess> {

    @Unique
    private final Long2IntMap sectionXByKey = new Long2IntOpenHashMap();
    @Unique
    private final Int2ObjectMap<LongSet> sectionsByX = new Int2ObjectOpenHashMap<>();

    @Final
    @Shadow
    private Long2ObjectMap<EntitySection<T>> sections;

    @Final
    @Shadow
    private LongSortedSet sectionIds;

    // === 原版二進制位元輔助運算（避免受 SectionPos 雜湊開關污染） ===
    @Unique
    private static long signed32$vanillaSectionPosAsLong(int x, int y, int z) {
        long l = 0L;
        l |= ((long) x & 4194303L) << 42;
        l |= ((long) y & 1048575L);
        return l | ((long) z & 4194303L) << 20;
    }

    @Unique
    private static int signed32$vanillaSectionPosX(long packed) {
        return (int) (packed >> 42);
    }

    @Unique
    private static int signed32$vanillaSectionPosY(long packed) {
        return (int) (packed << 44 >> 44);
    }

    @Unique
    private static int signed32$vanillaSectionPosZ(long packed) {
        return (int) (packed << 22 >> 42);
    }

    @Inject(method = "createSection", at = @At("TAIL"))
    private void onCreateSection(long sectionPos, CallbackInfoReturnable<EntitySection<T>> cir) {
        int sx = Signed32Config.INSTANCE.expandEntitySections
                ? IntSectionPos.getSectionPos(sectionPos).x
                : signed32$vanillaSectionPosX(sectionPos);
        sectionXByKey.put(sectionPos, sx);
        sectionsByX.computeIfAbsent(sx, k -> new LongOpenHashSet()).add(sectionPos);
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void onRemove(long sectionId, CallbackInfo ci) {
        int sx = sectionXByKey.remove(sectionId);
        int defaultX = sectionXByKey.defaultReturnValue();

        if (sx == defaultX) {
            return;
        }

        LongSet set = sectionsByX.get(sx);
        if (set == null) {
            return;
        }

        set.remove(sectionId);
        if (set.isEmpty()) {
            sectionsByX.remove(sx);
        }
    }

    @Overwrite
    public void forEachAccessibleNonEmptySection(AABB bounds, AbortableIterationConsumer<EntitySection<T>> consumer) {
        int minSecX = SectionPos.posToSectionCoord(bounds.minX - 2.0);
        int maxSecX = SectionPos.posToSectionCoord(bounds.maxX + 2.0);
        int minSecY = SectionPos.posToSectionCoord(bounds.minY - 2.0);
        int maxSecY = SectionPos.posToSectionCoord(bounds.maxY + 2.0);
        int minSecZ = SectionPos.posToSectionCoord(bounds.minZ - 2.0);
        int maxSecZ = SectionPos.posToSectionCoord(bounds.maxZ + 2.0);

        if (Signed32Config.INSTANCE.expandEntitySections) {
            // 【Signed32 模式】使用 X 軸分組字典，不受 22 位元與 subSet 排序限制
            for (int sx = minSecX; sx <= maxSecX; sx++) {
                LongSet keys = sectionsByX.get(sx);
                if (keys == null) {
                    continue;
                }

                for (long key : keys) {
                    IntSectionPos pos = IntSectionPos.getSectionPos(key);
                    if (pos.y < minSecY || pos.y > maxSecY || pos.z < minSecZ || pos.z > maxSecZ) {
                        continue;
                    }

                    EntitySection<T> section = this.sections.get(key);
                    if (section != null &&
                            !section.isEmpty() &&
                            section.getStatus().isAccessible() &&
                            consumer.accept(section).shouldAbort()) {
                        return;
                    }
                }
            }
        } else {
            // 【原版相容模式】正確的原版 subSet 算法：(0, 0) 為下界，(-1, -1) 為上界
            for (int sx = minSecX; sx <= maxSecX; sx++) {
                long minKey = signed32$vanillaSectionPosAsLong(sx, 0, 0);
                long maxKey = signed32$vanillaSectionPosAsLong(sx, -1, -1);

                for (long key : this.sectionIds.subSet(minKey, maxKey + 1L)) {
                    EntitySection<T> section = this.sections.get(key);
                    if (section == null || section.isEmpty() || !section.getStatus().isAccessible()) {
                        continue;
                    }

                    int sy = signed32$vanillaSectionPosY(key);
                    int sz = signed32$vanillaSectionPosZ(key);
                    if (sy < minSecY || sy > maxSecY || sz < minSecZ || sz > maxSecZ) {
                        continue;
                    }

                    if (consumer.accept(section).shouldAbort()) {
                        return;
                    }
                }
            }
        }
    }

    @Overwrite
    private LongSortedSet getChunkSections(int cx, int cz) {
        LongAVLTreeSet result = new LongAVLTreeSet();
        LongIterator it = this.sectionIds.iterator();
        while (it.hasNext()) {
            long key = it.nextLong();
            if (Signed32Config.INSTANCE.expandEntitySections) {
                IntSectionPos sp = IntSectionPos.getSectionPos(key);
                if (sp.x == cx && sp.z == cz) {
                    result.add(key);
                }
            } else {
                if (signed32$vanillaSectionPosX(key) == cx && signed32$vanillaSectionPosZ(key) == cz) {
                    result.add(key);
                }
            }
        }
        return result;
    }

    @Overwrite
    private static long getChunkKeyFromSectionKey(long pos) {
        if (Signed32Config.INSTANCE.expandEntitySections) {
            IntSectionPos sp = IntSectionPos.getSectionPos(pos);
            return ChunkPos.pack(sp.x, sp.z);
        }
        return ChunkPos.pack(signed32$vanillaSectionPosX(pos), signed32$vanillaSectionPosZ(pos));
    }
}