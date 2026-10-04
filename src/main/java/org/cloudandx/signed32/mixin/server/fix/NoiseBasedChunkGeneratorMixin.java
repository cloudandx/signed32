package org.cloudandx.signed32.mixin.fix;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.cloudandx.signed32.util.world.WorldBounds;

import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.Set;
import java.util.function.Function;

@Mixin(NoiseBasedChunkGenerator.class)
public abstract class NoiseBasedChunkGeneratorMixin {

    @Inject(method = "buildSurface", at = @At("HEAD"), cancellable = true)
    private void skipBuildSurface(ChunkAccess protoChunk, NoiseChunk noiseChunk, RandomState randomState, BiomeManager biomeManager, Set<Holder<Biome>> possibleBiomes, MaterialRule materialRule, CallbackInfo ci) {
        int cx = protoChunk.getPos().x();
        int cz = protoChunk.getPos().z();
        if (!WorldBounds.inChunkRange(cx, cz)) {
            ci.cancel();
        }
    }

    @Inject(method = "applyCarvingMask", at = @At("HEAD"), cancellable = true)
    private void skipApplyCarvers(ChunkAccess chunk, CarvingMask mask, RandomState randomState, MaterialRule materialRule, WorldGenerationContext context, NoiseChunk noiseChunk, Function<BlockPos, Holder<Biome>> biomeGetter, CarvingMask.@Nullable Filter filter, CallbackInfo ci) {
        int cx = chunk.getPos().x();
        int cz = chunk.getPos().z();
        if (!WorldBounds.inChunkRange(cx, cz)) {
            ci.cancel();
        }
    }

    @Inject(method = "spawnOriginalMobs", at = @At("HEAD"), cancellable = true)
    private void skipSpawnOriginalMobs(WorldGenRegion level, CallbackInfo ci) {
        int cx = level.getCenter().x();
        int cz = level.getCenter().z();
        if (!WorldBounds.inChunkRange(cx, cz)) {
            ci.cancel();
        }
    }
}