package org.cloudandx.signed32.client.mixin.client.misc;

import net.minecraft.client.Camera;
import org.cloudandx.signed32.config.Signed32Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Camera.class)
public class CameraMixin {

    @ModifyVariable(method = "setPosition(DDD)V", at = @At("HEAD"), argsOnly = true, name = "x")
    private double degradePrecisionX(double x) {
        if (!Signed32Config.INSTANCE.cameraJitter || Math.abs(x) < Signed32Config.INSTANCE.jitterThreshold) {
            return x;
        }
        return (double) ((float) x);
    }

    @ModifyVariable(method = "setPosition(DDD)V", at = @At("HEAD"), argsOnly = true, name = "y")
    private double degradePrecisionY(double y) {
        if (!Signed32Config.INSTANCE.cameraJitter || Math.abs(y) < Signed32Config.INSTANCE.jitterThreshold) {
            return y;
        }
        return (double) ((float) y);
    }

    @ModifyVariable(method = "setPosition(DDD)V", at = @At("HEAD"), argsOnly = true, name = "z")
    private double degradePrecisionZ(double z) {
        if (!Signed32Config.INSTANCE.cameraJitter || Math.abs(z) < Signed32Config.INSTANCE.jitterThreshold) {
            return z;
        }
        return (double) ((float) z);
    }
}
