package org.cloudandx.signed32.mixin.server.fix;

import org.cloudandx.signed32.util.world.WorldBounds;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlacedFeature.class)
public class PlacedFeatureMixin {

    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void signed32$skipExtremeCoords(WorldGenLevel level, ChunkGenerator generator, RandomSource random,
            BlockPos origin, CallbackInfoReturnable<Boolean> cir) {
        if (!WorldBounds.inBlockXZ(origin.getX(), origin.getZ())) {
            cir.setReturnValue(false);
        }
    }
}