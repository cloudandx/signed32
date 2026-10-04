package org.cloudandx.signed32.client.mixin.client.misc;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.cloudandx.signed32.config.Signed32Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Shadow
    public Camera camera;

    @ModifyVariable(method = "submit", at = @At("HEAD"), argsOnly = true, name = "x")
    private double degradeEntityX(double x) {
        if (!Signed32Config.INSTANCE.entityJitter || this.camera == null) {
            return x;
        }
        double camX = this.camera.position().x;
        if (Math.abs(camX) < Signed32Config.INSTANCE.jitterThreshold) {
            return x;
        }
        double absoluteX = x + camX;
        return (double) ((float) absoluteX) - camX;
    }

    @ModifyVariable(method = "submit", at = @At("HEAD"), argsOnly = true, name = "y")
    private double degradeEntityY(double y) {
        if (!Signed32Config.INSTANCE.entityJitter || this.camera == null) {
            return y;
        }
        double camY = this.camera.position().y;
        if (Math.abs(camY) < Signed32Config.INSTANCE.jitterThreshold) {
            return y;
        }
        double absoluteY = y + camY;
        return (double) ((float) absoluteY) - camY;
    }

    @ModifyVariable(method = "submit", at = @At("HEAD"), argsOnly = true, name = "z")
    private double degradeEntityZ(double z) {
        if (!Signed32Config.INSTANCE.entityJitter || this.camera == null) {
            return z;
        }
        double camZ = this.camera.position().z;
        if (Math.abs(camZ) < Signed32Config.INSTANCE.jitterThreshold) {
            return z;
        }
        double absoluteZ = z + camZ;
        return (double) ((float) absoluteZ) - camZ;
    }
}
