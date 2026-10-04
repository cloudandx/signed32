package org.cloudandx.signed32.mixin.expand.border;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Player.class)
public class PlayerMixin {
    @ModifyConstant(method = "tick", constant = @Constant(doubleValue = 2.9999999E7D))
    private static double maxPos(double value) {
        return (double) Integer.MAX_VALUE - 1.0;
    }

    @ModifyConstant(method = "tick", constant = @Constant(doubleValue = -2.9999999E7D))
    private static double minPos(double value) {
        return (double) -Integer.MAX_VALUE + 1.0;
    }
}