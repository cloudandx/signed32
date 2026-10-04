package org.cloudandx.signed32.mixin.expand.border;

import net.minecraft.server.dedicated.DedicatedServerProperties;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DedicatedServerProperties.class)
public class DedicatedServerPropertiesMixin {

    @Shadow
    @Final
    @Mutable
    private int maxWorldSize;

    @Unique
    private void setMaxWorldSize(int value) {
        this.maxWorldSize = value;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void OnInit(CallbackInfo ci) {
        this.setMaxWorldSize(Integer.MAX_VALUE - 16);
    }
}