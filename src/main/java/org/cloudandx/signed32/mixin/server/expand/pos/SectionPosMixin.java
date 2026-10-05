package org.cloudandx.signed32.mixin.server.expand.pos;

import it.unimi.dsi.fastutil.longs.LongConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import org.cloudandx.signed32.config.Signed32Config;
import org.cloudandx.signed32.util.hash.HashMath;
import org.cloudandx.signed32.util.maps.Common;
import org.cloudandx.signed32.util.maps.SectionUtil;
import org.cloudandx.signed32.util.pos.IntSectionPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SectionPos.class)
public abstract class SectionPosMixin {

    @Shadow public abstract int x();
    @Shadow public abstract int y();
    @Shadow public abstract int z();
    @Shadow public static int sectionRelativeX(short packed) { return 0; }
    @Shadow public static int sectionRelativeY(short packed) { return 0; }
    @Shadow public static int sectionRelativeZ(short packed) { return 0; }

    @Unique
    private static boolean signed32$isVanillaSection(int x, int y, int z) {
        return x >= -2097152 && x <= 2097151
                && z >= -2097152 && z <= 2097151
                && y >= -524288 && y <= 524287;
    }

    @Unique
    private static long signed32$packVanilla(int x, int y, int z) {
        long l = 0L;
        l |= ((long) x & 4194303L) << 42;
        l |= ((long) y & 1048575L);
        return l | ((long) z & 4194303L) << 20;
    }

    @Overwrite
    public long asLong() {
        int x = this.x();
        int y = this.y();
        int z = this.z();
        if (Signed32Config.INSTANCE.expandSectionPos) {
            if (!signed32$isVanillaSection(x, y, z)) {
                long key = HashMath.hash(x, y, z);
                SectionUtil.put(key, x, y, z);
                return key;
            }
        }
        return signed32$packVanilla(x, y, z);
    }

    @Overwrite
    public static long asLong(int x, int y, int z) {
        if (Signed32Config.INSTANCE.expandSectionPos) {
            if (!signed32$isVanillaSection(x, y, z)) {
                long key = HashMath.hash(x, y, z);
                SectionUtil.put(key, x, y, z);
                return key;
            }
        }
        return signed32$packVanilla(x, y, z);
    }

    @Overwrite
    public static int x(long packed) {
        if (Signed32Config.INSTANCE.expandSectionPos && !SectionUtil.isEmpty()) {
            IntSectionPos p = SectionUtil.get(packed);
            if (p != null) {
                p.lastAccess = Common.getTick();
                return p.x;
            }
        }
        return (int) (packed >> 42);
    }

    @Overwrite
    public static int y(long packed) {
        if (Signed32Config.INSTANCE.expandSectionPos && !SectionUtil.isEmpty()) {
            IntSectionPos p = SectionUtil.get(packed);
            if (p != null) {
                p.lastAccess = Common.getTick();
                return p.y;
            }
        }
        return (int) (packed << 44 >> 44);
    }

    @Overwrite
    public static int z(long packed) {
        if (Signed32Config.INSTANCE.expandSectionPos && !SectionUtil.isEmpty()) {
            IntSectionPos p = SectionUtil.get(packed);
            if (p != null) {
                p.lastAccess = Common.getTick();
                return p.z;
            }
        }
        return (int) (packed << 22 >> 42);
    }

    @Overwrite
    public static SectionPos of(long packed) {
        if (Signed32Config.INSTANCE.expandSectionPos && !SectionUtil.isEmpty()) {
            IntSectionPos p = SectionUtil.get(packed);
            if (p != null) {
                return SectionPos.of(p.x, p.y, p.z);
            }
        }
        return SectionPos.of(x(packed), y(packed), z(packed));
    }

    @Overwrite
    public static long offset(long packed, Direction direction) {
        return offset(packed, direction.getStepX(), direction.getStepY(), direction.getStepZ());
    }

    @Overwrite
    public static long offset(long packed, int dx, int dy, int dz) {
        if (dx == 0 && dy == 0 && dz == 0) return packed;

        if (Signed32Config.INSTANCE.expandSectionPos) {
            if (!SectionUtil.isEmpty()) {
                IntSectionPos p = SectionUtil.get(packed);
                if (p != null) {
                    int nx = p.x + dx, ny = p.y + dy, nz = p.z + dz;
                    if (signed32$isVanillaSection(nx, ny, nz)) {
                        return signed32$packVanilla(nx, ny, nz);
                    }
                    long key = HashMath.hash(nx, ny, nz);
                    SectionUtil.put(key, nx, ny, nz);
                    return key;
                }
            }

            int nx = x(packed) + dx;
            int ny = y(packed) + dy;
            int nz = z(packed) + dz;
            if (signed32$isVanillaSection(nx, ny, nz)) {
                return signed32$packVanilla(nx, ny, nz);
            }
            long key = HashMath.hash(nx, ny, nz);
            SectionUtil.put(key, nx, ny, nz);
            return key;
        }

        return signed32$packVanilla(x(packed) + dx, y(packed) + dy, z(packed) + dz);
    }

    @Overwrite
    public static long blockToSection(long levelPos) {
        int bx = BlockPos.getX(levelPos);
        int by = BlockPos.getY(levelPos);
        int bz = BlockPos.getZ(levelPos);
        int sx = SectionPos.blockToSectionCoord(bx);
        int sy = SectionPos.blockToSectionCoord(by);
        int sz = SectionPos.blockToSectionCoord(bz);
        return asLong(sx, sy, sz);
    }

    @Overwrite
    public static long getZeroNode(long packed) {
        if (Signed32Config.INSTANCE.expandSectionPos && !SectionUtil.isEmpty()) {
            IntSectionPos p = SectionUtil.get(packed);
            if (p != null) {
                return asLong(p.x, 0, p.z);
            }
        }
        return packed & -1048576L;
    }

    @Overwrite
    public static long getZeroNode(int x, int z) {
        return asLong(x, 0, z);
    }

    @Overwrite
    public int relativeToBlockX(short local) {
        return (int) (((long) x() << 4) + (long) sectionRelativeX(local));
    }

    @Overwrite
    public int relativeToBlockY(short local) {
        return (int) (((long) y() << 4) + (long) sectionRelativeY(local));
    }

    @Overwrite
    public int relativeToBlockZ(short local) {
        return (int) (((long) z() << 4) + (long) sectionRelativeZ(local));
    }

    @Overwrite
    public BlockPos relativeToBlockPos(short local) {
        int bx = (int) (((long) x() << 4) + (long) sectionRelativeX(local));
        int by = (int) (((long) y() << 4) + (long) sectionRelativeY(local));
        int bz = (int) (((long) z() << 4) + (long) sectionRelativeZ(local));
        return new BlockPos(bx, by, bz);
    }

    @Overwrite
    public static void aroundAndAtBlockPos(long pos, LongConsumer consumer) {
        int bx = BlockPos.getX(pos);
        int by = BlockPos.getY(pos);
        int bz = BlockPos.getZ(pos);
        SectionPos.aroundAndAtBlockPos(new BlockPos(bx, by, bz), consumer);
    }
}