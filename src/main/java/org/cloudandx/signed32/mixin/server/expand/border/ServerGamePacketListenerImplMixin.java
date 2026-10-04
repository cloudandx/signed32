package org.cloudandx.signed32.mixin.server.expand.border;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.cloudandx.signed32.config.Signed32Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Inject(method = "clampHorizontal", at = @At("HEAD"), cancellable = true)
    private static void clampHorizontal(double value, CallbackInfoReturnable<Double> cir) {
        if (Signed32Config.INSTANCE.expandWorldBorder) {
            cir.setReturnValue(value);
        }
    }

    @ModifyConstant(method = "clampVertical", constant = @Constant(doubleValue = 2.0E7D))
    private static double maxVerticalClamp(double original) {
        return Signed32Config.INSTANCE.expandWorldBorder ? (double) Integer.MAX_VALUE : original;
    }

    @ModifyConstant(method = "clampVertical", constant = @Constant(doubleValue = -2.0E7D))
    private static double minVerticalClamp(double original) {
        return Signed32Config.INSTANCE.expandWorldBorder ? (double) -Integer.MAX_VALUE : original;
    }
}