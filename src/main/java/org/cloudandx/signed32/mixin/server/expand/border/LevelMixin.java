package org.cloudandx.signed32.mixin.expand.border;

import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Level.class)
public class LevelMixin {
    @ModifyConstant(method = "isInWorldBoundsHorizontal", constant = @Constant(intValue = 30000000))
    private static int maxBlockA(int max) {
        return Integer.MAX_VALUE;
    }

    @ModifyConstant(method = "isInWorldBoundsHorizontal", constant = @Constant(intValue = -30000000))
    private static int minBlockA(int min) {
        return -Integer.MAX_VALUE;
    }

    @ModifyConstant(method = "getHeight", constant = @Constant(intValue = 30000000))
    private static int maxBlockB(int max) {
        return Integer.MAX_VALUE;
    }

    @ModifyConstant(method = "getHeight", constant = @Constant(intValue = -30000000))
    private static int minBlockB(int min) {
        return -Integer.MAX_VALUE;
    }
}