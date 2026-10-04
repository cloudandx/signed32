package org.cloudandx.signed32.mixin.fix;

import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

/**
 * 刷怪取點與粗略群系查詢的極端 Y 座標適配。
 */
@Mixin(NaturalSpawner.class)
public class NaturalSpawnerMixin {

    @Unique
    private static final int VERTICAL_SIMULATION_DISTANCE = 8; // 垂直模擬半徑（區段數），取代原 Config 設定

    @Overwrite
    private static BlockPos getRandomPosWithin(Level level, LevelChunk chunk) {
        ChunkPos cpos = chunk.getPos();
        int x = cpos.getMinBlockX() + level.getRandom().nextInt(16);
        int z = cpos.getMinBlockZ() + level.getRandom().nextInt(16);
        int topEmptyY = chunk.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) + 1;

        int lo = level.getMinY();
        long maxPossibleHi = (long) lo + Integer.MAX_VALUE - 1L;
        int hi = (int) Math.min((long) topEmptyY, maxPossibleHi);
        if (hi < lo) {
            hi = lo;
        }

        // 窗口帶：取離地表最近的玩家窗口，把取點區間夾進去。
        // section 座標左移 4 位在極值會越過 int 下界，全程用 long 計算後再落回 int。
        long bandLo = 0L;
        long bandHi = 0L;
        long bestDist = Long.MAX_VALUE;
        if (level instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : serverLevel.players()) {
                int center = Mth.floorDiv(player.getBlockY(), 16);
                long rLo = ((long) center - VERTICAL_SIMULATION_DISTANCE) << 4;
                long rHi = (((long) center + VERTICAL_SIMULATION_DISTANCE) << 4) + 15L;
                long d = hi < rLo ? rLo - hi : (hi > rHi ? hi - rHi : 0L);
                if (d < bestDist) {
                    bestDist = d;
                    bandLo = rLo;
                    bandHi = rHi;
                }
            }
        }
        if (bestDist != Long.MAX_VALUE) {
            long wLo = Math.max((long) lo, bandLo);
            long wHi = Math.min((long) hi, bandHi);
            if (wLo <= wHi) {
                lo = (int) wLo;
                hi = (int) wHi;
            }
        }

        int y = Mth.randomBetweenInclusive(level.getRandom(), lo, hi);
        return new BlockPos(x, y, z);
    }
}