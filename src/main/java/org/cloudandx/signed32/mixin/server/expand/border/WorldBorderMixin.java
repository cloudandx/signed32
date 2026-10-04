package org.cloudandx.signed32.mixin.server.expand.border;

import net.minecraft.world.level.border.WorldBorder;
import org.cloudandx.signed32.config.Signed32Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldBorder.class)
public class WorldBorderMixin {
    @Shadow private int absoluteMaxSize;

    @Unique
    private void setAbsoluteMaxSize(int value) {
        absoluteMaxSize = value;
    }

    @Inject(method = "<init>*", at = @At("RETURN"))
    private void OnInitA(CallbackInfo ci) {
        if (Signed32Config.INSTANCE.expandWorldBorder) {
            setAbsoluteMaxSize(Integer.MAX_VALUE);
        }
    }

    @ModifyConstant(method = "<init>(Lnet/minecraft/world/level/border/WorldBorder$Settings;)V", constant = @Constant(doubleValue = 5.9999968E7D))
    private double OnInitB(double value) {
        return Signed32Config.INSTANCE.expandWorldBorder ? (double) (Integer.MAX_VALUE - 16) * 2.0 : value;
    }
}