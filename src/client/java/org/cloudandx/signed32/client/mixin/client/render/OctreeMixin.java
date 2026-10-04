package org.cloudandx.signed32.client.mixin.client.render;

import net.minecraft.client.renderer.Octree;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Octree.class)
public class OctreeMixin {

    @Unique
    private static final int VERTICAL_SIMULATION_DISTANCE = 8;

    @Final
    @Shadow
    private BlockPos cameraSectionCenter;

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;<init>(IIIIII)V"), index = 1)
    private int signed32$cameraCenteredMinY(int minY) {
        return signed32$boxMin(this.cameraSectionCenter.getY());
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;<init>(IIIIII)V"), index = 4)
    private int signed32$cameraCenteredMaxY(int maxY) {
        return signed32$boxMax(this.cameraSectionCenter.getY());
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;<init>(IIIIII)V"), index = 0)
    private int signed32$cameraCenteredMinX(int minX) {
        return signed32$boxMin(this.cameraSectionCenter.getX());
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;<init>(IIIIII)V"), index = 3)
    private int signed32$cameraCenteredMaxX(int maxX) {
        return signed32$boxMax(this.cameraSectionCenter.getX());
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;<init>(IIIIII)V"), index = 2)
    private int signed32$cameraCenteredMinZ(int minZ) {
        return signed32$boxMin(this.cameraSectionCenter.getZ());
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;<init>(IIIIII)V"), index = 5)
    private int signed32$cameraCenteredMaxZ(int maxZ) {
        return signed32$boxMax(this.cameraSectionCenter.getZ());
    }

    @Unique
    private static int signed32$halfSpanBlocks() {
        int gridSections = VERTICAL_SIMULATION_DISTANCE * 2 + 1;
        return Mth.smallestEncompassingPowerOfTwo(gridSections) * 16 / 2;
    }

    @Unique
    private static long signed32$shiftedCenter(long center) {
        long half = signed32$halfSpanBlocks();
        long lowest = Integer.MIN_VALUE + half;
        long highest = (long) Integer.MAX_VALUE - half;
        return Mth.clamp(center, lowest, highest);
    }

    @Unique
    private static int signed32$boxMin(int center) {
        return (int) (signed32$shiftedCenter(center) - signed32$halfSpanBlocks());
    }

    @Unique
    private static int signed32$boxMax(int center) {
        return (int) (signed32$shiftedCenter(center) + signed32$halfSpanBlocks() - 1L);
    }
}