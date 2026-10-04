package org.cloudandx.signed32.mixin.expand.border;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public class EntityMixin {

    @Redirect(method = "absSnapTo(DDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(DDD)D"))
    private static double absSnapTo(double value, double min, double max) {
        return value;
    }

    /**
     * 實體位置判定放行。取消原有 ±30,000,000 的 Mth.clamp 限制。
     */
    @Redirect(method = "load(Lnet/minecraft/world/level/storage/ValueInput;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(DDD)D"))
    private static double load(double value, double min, double max) {
        return value;
    }
}