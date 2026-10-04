package org.cloudandx.signed32.client.mixin.client.fix.sound;

import org.cloudandx.signed32.client.audio.AudioOrigin;
import net.minecraft.client.Camera;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin {

    @Unique
    private static volatile Vec3 signed32$anchor;

    @Unique
    private static final ThreadLocal<SoundInstance> signed32$playing = new ThreadLocal<>();

    @Shadow
    @Final
    private Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel;

    @Inject(method = "updateSource", at = @At("HEAD"))
    private void signed32$refreshAnchor(Camera camera, CallbackInfo ci) {
        if (!camera.isInitialized()) {
            return;
        }
        Vec3 anchor = AudioOrigin.quantize(camera.position());
        Vec3 previous = signed32$anchor;
        if (previous != null && AudioOrigin.sameAnchor(previous, anchor)) {
            return;
        }
        signed32$anchor = anchor;
        signed32$rewriteSources(anchor);
    }

    @Unique
    private void signed32$rewriteSources(Vec3 anchor) {
        for (Map.Entry<SoundInstance, ChannelAccess.ChannelHandle> entry : this.instanceToChannel.entrySet()) {
            SoundInstance instance = entry.getKey();
            if (instance.isRelative()) {
                continue;
            }
            Vec3 shifted = new Vec3(
                    instance.getX() - anchor.x,
                    instance.getY() - anchor.y,
                    instance.getZ() - anchor.z);
            entry.getValue().execute(channel -> channel.setSelfPosition(shifted));
        }
    }

    @Inject(method = "play", at = @At("HEAD"))
    private void signed32$markPlaying(SoundInstance instance,
            CallbackInfoReturnable<SoundEngine.PlayResult> cir) {
        signed32$playing.set(instance);
    }

    @Inject(method = "play", at = @At("RETURN"))
    private void signed32$clearPlaying(SoundInstance instance,
            CallbackInfoReturnable<SoundEngine.PlayResult> cir) {
        signed32$playing.remove();
    }

    @ModifyVariable(method = "play", at = @At("STORE"), name = "position")
    private Vec3 signed32$relativeForPlay(Vec3 position) {
        SoundInstance instance = signed32$playing.get();
        if (instance != null && instance.isRelative()) {
            return position;
        }
        return signed32$toAnchorRelative(position);
    }

    @Redirect(method = "tickInGameSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/sounds/TickableSoundInstance;getX()D"))
    private double signed32$relativeTickX(TickableSoundInstance instance) {
        Vec3 anchor = signed32$anchor;
        return instance.isRelative() || anchor == null ? instance.getX() : instance.getX() - anchor.x;
    }

    @Redirect(method = "tickInGameSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/sounds/TickableSoundInstance;getY()D"))
    private double signed32$relativeTickY(TickableSoundInstance instance) {
        Vec3 anchor = signed32$anchor;
        return instance.isRelative() || anchor == null ? instance.getY() : instance.getY() - anchor.y;
    }

    @Redirect(method = "tickInGameSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/sounds/TickableSoundInstance;getZ()D"))
    private double signed32$relativeTickZ(TickableSoundInstance instance) {
        Vec3 anchor = signed32$anchor;
        return instance.isRelative() || anchor == null ? instance.getZ() : instance.getZ() - anchor.z;
    }

    @Unique
    private static Vec3 signed32$toAnchorRelative(Vec3 position) {
        Vec3 anchor = signed32$anchor;
        if (anchor == null) {
            return position;
        }
        return position.subtract(anchor);
    }
}