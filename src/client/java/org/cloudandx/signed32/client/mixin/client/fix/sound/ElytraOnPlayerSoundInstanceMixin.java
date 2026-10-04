package org.cloudandx.signed32.client.mixin.client.fix.sound;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.ElytraOnPlayerSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ElytraOnPlayerSoundInstance.class)
public abstract class ElytraOnPlayerSoundInstanceMixin extends AbstractTickableSoundInstance {

    @Shadow
    @Final
    private LocalPlayer player;

    @SuppressWarnings("DataFlowIssue")
    protected ElytraOnPlayerSoundInstanceMixin() {
        super((SoundEvent) null, (SoundSource) null, SoundInstance.createUnseededRandom());
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void signed32$preciseCoordsOnTick(CallbackInfo ci) {
        this.x = this.player.getX();
        this.y = this.player.getY();
        this.z = this.player.getZ();
    }
}