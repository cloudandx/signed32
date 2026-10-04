package org.cloudandx.signed32.mixin.server.expand.border;

import net.minecraft.world.entity.player.Player;
import org.cloudandx.signed32.config.Signed32Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Player.class)
public class PlayerMixin {
    @ModifyConstant(method = "tick", constant = @Constant(doubleValue = 2.9999999E7D))
    private static double maxPos(double value) {
        return Signed32Config.INSTANCE.expandWorldBorder ? (double) Integer.MAX_VALUE - 1.0 : value;
    }

    @ModifyConstant(method = "tick", constant = @Constant(doubleValue = -2.9999999E7D))
    private static double minPos(double value) {
        return Signed32Config.INSTANCE.expandWorldBorder ? (double) -Integer.MAX_VALUE + 1.0 : value;
    }
}