package org.cloudandx.signed32.mixin.fix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.lang.reflect.Field;

import org.cloudandx.signed32.util.world.WorldBounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

@Mixin(GroundPathNavigation.class)
public abstract class GroundPathNavigationMixin {

    @Unique
    private static final int MAX_CAP_ITER = 256; // 替換原先由 Config 提供的尋路掃描深度

    @Unique
    private static final Field F_LEVEL;

    static {
        try {
            F_LEVEL = PathNavigation.class.getDeclaredField("level");
            F_LEVEL.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Unique
    private Level refLevel() {
        try {
            return (Level) F_LEVEL.get(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Unique
    private int scanFloor(int targetY) {
        return (int) Math.max((long) WorldBounds.DEFAULT_MIN_Y,
                (long) targetY - MAX_CAP_ITER);
    }

    @Unique
    private int scanCeil(int targetY) {
        return (int) Math.min((long) WorldBounds.DEFAULT_MAX_Y,
                (long) targetY + MAX_CAP_ITER);
    }

    @SuppressWarnings("deprecation")
    @Overwrite
    BlockPos findSurfacePosition(final LevelChunk chunk, BlockPos pos, final int reachRange) {
        if (chunk.getBlockState(pos).isAir()) {
            BlockPos.MutableBlockPos columnPos = pos.mutable().move(Direction.DOWN);
            int floorLimit = scanFloor(pos.getY());

            while (columnPos.getY() >= floorLimit && chunk.getBlockState(columnPos).isAir()) {
                columnPos.move(Direction.DOWN);
            }

            if (columnPos.getY() >= floorLimit) {
                return columnPos.above();
            }

            columnPos.setY(pos.getY() + 1);
            int ceilLimit = scanCeil(pos.getY());

            while (columnPos.getY() <= ceilLimit && chunk.getBlockState(columnPos).isAir()) {
                columnPos.move(Direction.UP);
            }

            pos = columnPos;
        }

        if (!chunk.getBlockState(pos).isSolid()) {
            return pos;
        }

        BlockPos.MutableBlockPos columnPos = pos.mutable().move(Direction.UP);
        int ceilLimit = scanCeil(pos.getY());

        while (columnPos.getY() <= ceilLimit && chunk.getBlockState(columnPos).isSolid()) {
            columnPos.move(Direction.UP);
        }

        return columnPos.immutable();
    }
}