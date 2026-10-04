package org.cloudandx.signed32.client.mixin.client.fix.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import org.cloudandx.signed32.util.world.WorldBounds;

import net.minecraft.client.renderer.entity.EntityRenderer;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    @Redirect(method = "extractShadow", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;floor(D)I"))
    private static int floorSafe(double v) {
        int r = (int) Math.floor(v);
        if (r > WorldBounds.MAX_PLAYABLE_BLOCK - 1) {
            return WorldBounds.MAX_PLAYABLE_BLOCK - 1;
        }
        if (r < -WorldBounds.MAX_PLAYABLE_BLOCK) {
            return -WorldBounds.MAX_PLAYABLE_BLOCK;
        }
        return r;
    }
}