package org.cloudandx.signed32.mixin.server.expand.border;

import net.minecraft.server.commands.WorldBorderCommand;
import org.cloudandx.signed32.config.Signed32Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(WorldBorderCommand.class)
public class WorldBorderCommandMixin {
    @ModifyConstant(method = "<clinit>", constant = @Constant(doubleValue = 5.9999968E7D))
    private static double onClassInitA(double value) {
        return Signed32Config.INSTANCE.expandWorldBorder ? (double) (Integer.MAX_VALUE - 16) * 2.0 : value;
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(doubleValue = 2.9999984E7D))
    private static double onClassInitB(double value) {
        return Signed32Config.INSTANCE.expandWorldBorder ? (double) Integer.MAX_VALUE - 16.0 : value;
    }

    @ModifyConstant(method = "register", constant = @Constant(doubleValue = 5.9999968E7D))
    private static double maxBlock(double value) {
        return Signed32Config.INSTANCE.expandWorldBorder ? (double) (Integer.MAX_VALUE - 16) * 2.0 : value;
    }

    @ModifyConstant(method = "register", constant = @Constant(doubleValue = -5.9999968E7D))
    private static double minBlock(double value) {
        return Signed32Config.INSTANCE.expandWorldBorder ? (double) (-Integer.MAX_VALUE + 16) * 2.0 : value;
    }

    @ModifyConstant(method = "setSize", constant = @Constant(doubleValue = 5.9999968E7D))
    private static double maxSize(double value) {
        return Signed32Config.INSTANCE.expandWorldBorder ? (double) (Integer.MAX_VALUE - 16) * 2.0 : value;
    }

    @ModifyConstant(method = "setCenter", constant = @Constant(doubleValue = 2.9999984E7D))
    private static double maxBlockA(double value) {
        return Signed32Config.INSTANCE.expandWorldBorder ? (double) Integer.MAX_VALUE - 16.0 : value;
    }
}