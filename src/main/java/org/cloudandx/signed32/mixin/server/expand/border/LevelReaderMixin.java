package org.cloudandx.signed32.mixin.server.expand.border;

import net.minecraft.world.level.LevelReader;
import org.cloudandx.signed32.config.Signed32Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LevelReader.class)
public interface LevelReaderMixin {
    @ModifyConstant(method = "getMaxLocalRawBrightness(Lnet/minecraft/core/BlockPos;I)I", constant = @Constant(intValue = 30000000))
    private int maxBlock(int max) {
        return Signed32Config.INSTANCE.expandWorldBorder ? Integer.MAX_VALUE : max;
    }

    @ModifyConstant(method = "getMaxLocalRawBrightness(Lnet/minecraft/core/BlockPos;I)I", constant = @Constant(intValue = -30000000))
    private int minBlock(int min) {
        return Signed32Config.INSTANCE.expandWorldBorder ? -Integer.MAX_VALUE : min;
    }
}