package org.cloudandx.signed32.client.mixin.client.fix.sound;

import org.cloudandx.signed32.client.audio.AudioOrigin;
import com.mojang.blaze3d.audio.Listener;
import com.mojang.blaze3d.audio.ListenerTransform;

import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Listener.class)
public abstract class ListenerMixin {

    @Redirect(method = "setTransform", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/audio/ListenerTransform;position()Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 signed32$anchoredListenerPosition(ListenerTransform transform) {
        return AudioOrigin.shift(transform.position());
    }
}