package org.cloudandx.signed32.client.mixin.client.fix.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.cloudandx.signed32.util.world.WorldBounds;

import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;

@Mixin(LevelExtractor.class)
public abstract class LevelExtractorMixin {

    @Shadow
    public abstract void setSectionDirty(int sectionX, int sectionY, int sectionZ);

    @Shadow
    protected abstract void setSectionDirty(int sectionX, int sectionY, int sectionZ, boolean playerChanged);

    @Unique
    private static boolean isDangerous(int v) {
        return v >= WorldBounds.MAX_PLAYABLE_BLOCK - 1 || v <= -WorldBounds.MAX_PLAYABLE_BLOCK;
    }

    @Inject(method = "setBlocksDirty(IIIIII)V", at = @At("HEAD"), cancellable = true)
    private void safeSetBlocksDirty(int x0, int y0, int z0, int x1, int y1, int z1, CallbackInfo ci) {
        if (!isDangerous(x0) && !isDangerous(y0) && !isDangerous(z0)
                && !isDangerous(x1) && !isDangerous(y1) && !isDangerous(z1)) {
            return;
        }
        for (long i = (long) z0 - 1; i <= (long) z1 + 1; i++) {
            for (long j = (long) x0 - 1; j <= (long) x1 + 1; j++) {
                for (long k = (long) y0 - 1; k <= (long) y1 + 1; k++) {
                    this.setSectionDirty(
                            SectionPos.blockToSectionCoord((int) j),
                            SectionPos.blockToSectionCoord((int) k),
                            SectionPos.blockToSectionCoord((int) i));
                }
            }
        }
        ci.cancel();
    }

    @Inject(method = "setBlockDirty(Lnet/minecraft/core/BlockPos;Z)V", at = @At("HEAD"), cancellable = true)
    private void safeSetBlockDirty(BlockPos pos, boolean playerChanged, CallbackInfo ci) {
        if (!isDangerous(pos.getX()) && !isDangerous(pos.getY()) && !isDangerous(pos.getZ())) {
            return;
        }
        for (long i = (long) pos.getZ() - 1; i <= (long) pos.getZ() + 1; i++) {
            for (long j = (long) pos.getX() - 1; j <= (long) pos.getX() + 1; j++) {
                for (long k = (long) pos.getY() - 1; k <= (long) pos.getY() + 1; k++) {
                    this.setSectionDirty(
                            SectionPos.blockToSectionCoord((int) j),
                            SectionPos.blockToSectionCoord((int) k),
                            SectionPos.blockToSectionCoord((int) i),
                            playerChanged);
                }
            }
        }
        ci.cancel();
    }
}