package org.cloudandx.signed32.mixin.server.expand.border;

import net.minecraft.world.level.Level;
import org.cloudandx.signed32.config.Signed32Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Level.class)
public class LevelMixin {
    @ModifyConstant(method = "isInWorldBoundsHorizontal", constant = @Constant(intValue = 30000000))
    private static int maxBlockA(int max) {
        return Signed32Config.INSTANCE.expandWorldBorder ? Integer.MAX_VALUE : max;
    }

    @ModifyConstant(method = "isInWorldBoundsHorizontal", constant = @Constant(intValue = -30000000))
    private static int minBlockA(int min) {
        return Signed32Config.INSTANCE.expandWorldBorder ? -Integer.MAX_VALUE : min;
    }

    @ModifyConstant(method = "getHeight", constant = @Constant(intValue = 30000000))
    private static int maxBlockB(int max) {
        return Signed32Config.INSTANCE.expandWorldBorder ? Integer.MAX_VALUE : max;
    }

    @ModifyConstant(method = "getHeight", constant = @Constant(intValue = -30000000))
    private static int minBlockB(int min) {
        return Signed32Config.INSTANCE.expandWorldBorder ? -Integer.MAX_VALUE : min;
    }
}