package org.cloudandx.signed32.mixin.server.expand.border;

import net.minecraft.server.commands.ForceLoadCommand;
import org.cloudandx.signed32.config.Signed32Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ForceLoadCommand.class)
public class ForceLoadCommandMixin {
    @ModifyConstant(method = "changeForceLoad", constant = @Constant(intValue = 30000000))
    private static int maxBlock(int max) {
        return Signed32Config.INSTANCE.expandWorldBorder ? Integer.MAX_VALUE : max;
    }

    @ModifyConstant(method = "changeForceLoad", constant = @Constant(intValue = -30000000))
    private static int minBlock(int min) {
        return Signed32Config.INSTANCE.expandWorldBorder ? -Integer.MAX_VALUE : min;
    }
}