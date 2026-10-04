package org.cloudandx.signed32.mixin.server.fix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Overwrite;

import java.lang.reflect.Field;

import org.cloudandx.signed32.util.world.WorldBounds;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;

import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WalkNodeEvaluator.class)
public abstract class WalkNodeEvaluatorMixin {

    @Unique
    private static final int MAX_CAP_ITER = 256;

    @Unique
    private static final Field F_MOB;

    @Unique
    private static final Field F_CURRENT_CONTEXT;

    static {
        try {
            F_MOB = NodeEvaluator.class.getDeclaredField("mob");
            F_MOB.setAccessible(true);
            F_CURRENT_CONTEXT = NodeEvaluator.class.getDeclaredField("currentContext");
            F_CURRENT_CONTEXT.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Unique
    private Mob refMob() {
        try {
            return (Mob) F_MOB.get(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Unique
    private PathfindingContext refContext() {
        try {
            return (PathfindingContext) F_CURRENT_CONTEXT.get(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Shadow
    protected abstract Node getStartNode(BlockPos pos);

    @Shadow
    protected abstract boolean canStartAt(BlockPos pos);

    @Redirect(method = "tryFindFirstNonWaterBelow", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getMinY()I"))
    private int nonWaterFloorLimit(Level level) {
        return (int) Math.max((long) WorldBounds.DEFAULT_MIN_Y,
                (long) refMob().getY() - MAX_CAP_ITER);
    }

    @Overwrite
    public Node getStart() {
        BlockPos.MutableBlockPos reusablePos = new BlockPos.MutableBlockPos();
        Mob mob = refMob();
        PathfindingContext ctx = refContext();
        int startY = mob.getBlockY();
        BlockState blockState = ctx.getBlockState(reusablePos.set(mob.getX(), startY, mob.getZ()));
        if (!mob.canStandOnFluid(blockState.getFluidState())) {
            if (((NodeEvaluator) (Object) this).canFloat() && mob.isInWater()) {
                while (true) {
                    if (!blockState.is(Blocks.WATER) && blockState.getFluidState() != Fluids.WATER.getSource(false)) {
                        startY--;
                        break;
                    }
                    blockState = ctx.getBlockState(reusablePos.set(mob.getX(), ++startY, mob.getZ()));
                }
            } else if (mob.onGround()) {
                startY = Mth.floor(mob.getY() + 0.5);
            } else {
                reusablePos.set(mob.getX(), mob.getY() + 1.0, mob.getZ());
                int floorLimit = (int) Math.max((long) WorldBounds.DEFAULT_MIN_Y,
                        (long) mob.getBlockY() - MAX_CAP_ITER);
                boolean foundGround = false;

                while (reusablePos.getY() > floorLimit) {
                    startY = reusablePos.getY();
                    reusablePos.setY(reusablePos.getY() - 1);
                    BlockState belowBlockState = ctx.getBlockState(reusablePos);
                    if (!belowBlockState.isAir() && !belowBlockState.isPathfindable(PathComputationType.LAND)) {
                        foundGround = true;
                        break;
                    }
                }

                if (!foundGround) {
                    startY = mob.getBlockY();
                }
            }
        } else {
            while (mob.canStandOnFluid(blockState.getFluidState())) {
                blockState = ctx.getBlockState(reusablePos.set(mob.getX(), ++startY, mob.getZ()));
            }
            startY--;
        }

        BlockPos startPos = mob.blockPosition();
        if (!this.canStartAt(reusablePos.set(startPos.getX(), startY, startPos.getZ()))) {
            AABB mobBB = mob.getBoundingBox();
            if (this.canStartAt(reusablePos.set(mobBB.minX, startY, mobBB.minZ))
                    || this.canStartAt(reusablePos.set(mobBB.minX, startY, mobBB.maxZ))
                    || this.canStartAt(reusablePos.set(mobBB.maxX, startY, mobBB.minZ))
                    || this.canStartAt(reusablePos.set(mobBB.maxX, startY, mobBB.maxZ))) {
                return this.getStartNode(reusablePos);
            }
        }

        return this.getStartNode(new BlockPos(startPos.getX(), startY, startPos.getZ()));
    }
}