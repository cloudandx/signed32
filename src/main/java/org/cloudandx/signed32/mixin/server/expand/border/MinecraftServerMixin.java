package org.cloudandx.signed32.mixin.server.expand.border;

import net.minecraft.server.MinecraftServer;
import org.cloudandx.signed32.config.Signed32Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @ModifyConstant(method = "getAbsoluteMaxWorldSize", constant = @Constant(intValue = 29999984))
    private int absoluteMaxWorldSize(int value) {
        return Signed32Config.INSTANCE.expandWorldBorder ? Integer.MAX_VALUE : value;
    }
}