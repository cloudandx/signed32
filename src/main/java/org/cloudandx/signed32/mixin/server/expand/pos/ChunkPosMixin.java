package org.cloudandx.signed32.mixin.server.expand.pos;

import net.minecraft.world.level.ChunkPos;
import org.cloudandx.signed32.config.Signed32Config;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkPos.class)
public abstract class ChunkPosMixin {

    @Final
    @Shadow
    public int x;

    @Final
    @Shadow
    public int z;

    @Unique
    private int signed32$shiftToBlockCoord(int coord) {
        long val = (long) coord << 4;
        if (val > Integer.MAX_VALUE - 15L) {
            return Integer.MAX_VALUE - 15;
        }
        if (val < Integer.MIN_VALUE + 15L) {
            return Integer.MIN_VALUE + 15;
        }
        return (int) val;
    }

    @Inject(method = "getMinBlockX", at = @At("HEAD"), cancellable = true)
    private void onGetMinBlockX(CallbackInfoReturnable<Integer> cir) {
        if (Signed32Config.INSTANCE.fixChunkOverflow) {
            cir.setReturnValue(signed32$shiftToBlockCoord(this.x));
        }
    }

    @Inject(method = "getMinBlockZ", at = @At("HEAD"), cancellable = true)
    private void onGetMinBlockZ(CallbackInfoReturnable<Integer> cir) {
        if (Signed32Config.INSTANCE.fixChunkOverflow) {
            cir.setReturnValue(signed32$shiftToBlockCoord(this.z));
        }
    }

    /**
     * 攔截無參數的 ChunkPos#isValid() 檢查
     */
    @Inject(method = "isValid()Z", at = @At("HEAD"), cancellable = true)
    private void onIsValid(CallbackInfoReturnable<Boolean> cir) {
        if (Signed32Config.INSTANCE.fixChunkOverflow) {
            cir.setReturnValue(true);
        }
    }

    /**
     * 兼顧靜態 isValid(int, int)（若底層存在則攔截，若不存在則略過不報錯）
     */
    @Inject(method = "isValid(II)Z", at = @At("HEAD"), cancellable = true, require = 0)
    private static void onIsValidStatic(int x, int z, CallbackInfoReturnable<Boolean> cir) {
        if (Signed32Config.INSTANCE.fixChunkOverflow) {
            cir.setReturnValue(true);
        }
    }
}