package org.cloudandx.signed32.mixin.server.worldgen;

import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DensitySamplerSet;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import org.cloudandx.signed32.util.worldgen.BetaFarLandsNoise;
import org.cloudandx.signed32.util.worldgen.FarLandsDensitySampler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoiseChunk.class)
public abstract class NoiseChunkMixin {

    @Shadow
    @Final
    @Mutable
    private DensitySamplerSet cachingSamplers;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void wrapFinalDensitySampler(
            RandomState randomState,
            Beardifier beardifier,
            NoiseGeneratorSettings settings,
            Aquifer.FluidPicker globalFluidPicker,
            Blender blender,
            DensityVolume volume,
            CallbackInfo ci
    ) {
        DensityFunction finalDensityFunction = settings.noiseRouter().finalDensity();
        DensitySamplerSet original = this.cachingSamplers;
        BetaFarLandsNoise farLandsNoise = new BetaFarLandsNoise(0L);

        // 包裝 cachingSamplers，當索取 finalDensity 時替換為遠方之地採樣器
        this.cachingSamplers = function -> {
            DensitySampler.Bound bound = original.get(function);
            if (function == finalDensityFunction || function.equals(finalDensityFunction)) {
                return new DensitySampler.Bound(
                        new FarLandsDensitySampler(bound.sampler(), farLandsNoise),
                        bound.context()
                );
            }
            return bound;
        };
    }
}