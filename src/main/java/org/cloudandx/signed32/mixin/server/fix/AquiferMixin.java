package org.cloudandx.signed32.mixin.server.fix;

import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.cloudandx.signed32.util.world.WorldBounds;

@Mixin(Aquifer.NoiseBasedAquifer.class)
public abstract class AquiferMixin {

    /**
     * 防禦 fromGridX 位移溢出為負數：
     * 當 gridCoord << 4 超出 Integer.MAX_VALUE 時，飽和截斷在 MAX_PLAYABLE_BLOCK，杜絕座標翻轉。
     */
    @Inject(method = "fromGridX", at = @At("HEAD"), cancellable = true)
    private static void signed32$safeFromGridX(int gridCoord, int blockOffset, CallbackInfoReturnable<Integer> cir) {
        long val = ((long) gridCoord << 4) + (long) blockOffset;
        if (val > WorldBounds.MAX_PLAYABLE_BLOCK) {
            cir.setReturnValue(WorldBounds.MAX_PLAYABLE_BLOCK);
        } else if (val < WorldBounds.MIN_PLAYABLE_BLOCK) {
            cir.setReturnValue(WorldBounds.MIN_PLAYABLE_BLOCK);
        } else {
            cir.setReturnValue((int) val);
        }
    }

    /**
     * 防禦 fromGridZ 位移溢出為負數。
     */
    @Inject(method = "fromGridZ", at = @At("HEAD"), cancellable = true)
    private static void signed32$safeFromGridZ(int gridCoord, int blockOffset, CallbackInfoReturnable<Integer> cir) {
        long val = ((long) gridCoord << 4) + (long) blockOffset;
        if (val > WorldBounds.MAX_PLAYABLE_BLOCK) {
            cir.setReturnValue(WorldBounds.MAX_PLAYABLE_BLOCK);
        } else if (val < WorldBounds.MIN_PLAYABLE_BLOCK) {
            cir.setReturnValue(WorldBounds.MIN_PLAYABLE_BLOCK);
        } else {
            cir.setReturnValue((int) val);
        }
    }

    /**
     * 熔斷保險：攔截 maxSurfaceLevel 中的 new DensityVolume，
     * 強制將計算出的跨度夾在 [1, 64] 之間，徹底消滅 Size must be positive 崩潰。
     */
    @Redirect(
            method = "maxSurfaceLevel",
            at = @At(
                    value = "NEW",
                    target = "(IIIIIIIII)Lnet/minecraft/world/level/levelgen/densityfunction/DensityVolume;"
            )
    )
    private DensityVolume signed32$safeCreateDensityVolume(
            int sizeX, int sizeY, int sizeZ,
            int minBlockX, int minBlockY, int minBlockZ,
            int stepBlockX, int stepBlockY, int stepBlockZ
    ) {
        int safeSizeX = Math.max(1, Math.min(64, sizeX));
        int safeSizeZ = Math.max(1, Math.min(64, sizeZ));
        return new DensityVolume(safeSizeX, sizeY, safeSizeZ, minBlockX, minBlockY, minBlockZ, stepBlockX, stepBlockY, stepBlockZ);
    }
}