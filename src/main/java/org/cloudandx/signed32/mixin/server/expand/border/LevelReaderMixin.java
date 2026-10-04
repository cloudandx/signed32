package org.cloudandx.signed32.mixin.expand.border;

import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LevelReader.class)
public interface LevelReaderMixin {
    @ModifyConstant(method = "getMaxLocalRawBrightness(Lnet/minecraft/core/BlockPos;I)I", constant = @Constant(intValue = 30000000))
    private int maxBlock(int max) {
        return Integer.MAX_VALUE;
    }

    @ModifyConstant(method = "getMaxLocalRawBrightness(Lnet/minecraft/core/BlockPos;I)I", constant = @Constant(intValue = -30000000))
    private int minBlock(int min) {
        return -Integer.MAX_VALUE;
    }
}