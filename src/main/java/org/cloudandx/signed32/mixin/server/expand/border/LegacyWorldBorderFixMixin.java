package org.cloudandx.signed32.mixin.server.expand.border;

import net.minecraft.util.datafix.fixes.LegacyWorldBorderFix;
import org.cloudandx.signed32.config.Signed32Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LegacyWorldBorderFix.class)
public class LegacyWorldBorderFixMixin {
    @ModifyConstant(method = "lambda$makeRule$1", constant = @Constant(doubleValue = 5.9999968E7D))
    private static double maxSize(double value) {
        return Signed32Config.INSTANCE.expandWorldBorder ? (double) (Integer.MAX_VALUE - 16) * 2.0 : value;
    }
}