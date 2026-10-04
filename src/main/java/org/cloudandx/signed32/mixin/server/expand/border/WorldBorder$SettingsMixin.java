package org.cloudandx.signed32.mixin.expand.border;

import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(WorldBorder.Settings.class)
public class WorldBorder$SettingsMixin {
    @ModifyConstant(method = "<clinit>", constant = @Constant(doubleValue = 5.9999968E7D))
    private static double onClassInitA(double value) {
        return (double) (Integer.MAX_VALUE - 16) * 2.0;
    }

    @ModifyConstant(method = "lambda$static$0", constant = @Constant(doubleValue = 2.9999984E7D))
    private static double onClassInitB(double value) {
        return (double) Integer.MAX_VALUE - 16.0;
    }

    @ModifyConstant(method = "lambda$static$0", constant = @Constant(doubleValue = -2.9999984E7D))
    private static double onClassInitC(double value) {
        return (double) -Integer.MAX_VALUE + 16.0;
    }
}