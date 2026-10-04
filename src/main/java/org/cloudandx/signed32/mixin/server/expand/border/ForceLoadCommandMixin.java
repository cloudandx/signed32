package org.cloudandx.signed32.mixin.expand.border;

import net.minecraft.server.commands.ForceLoadCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ForceLoadCommand.class)
public class ForceLoadCommandMixin {
    @ModifyConstant(method = "changeForceLoad", constant = @Constant(intValue = 30000000))
    private static int maxBlock(int max) {
        return Integer.MAX_VALUE;
    }

    @ModifyConstant(method = "changeForceLoad", constant = @Constant(intValue = -30000000))
    private static int minBlock(int min) {
        return -Integer.MAX_VALUE;
    }
}