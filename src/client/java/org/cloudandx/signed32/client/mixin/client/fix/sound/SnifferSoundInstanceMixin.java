package org.cloudandx.signed32.client.mixin.client.fix.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SnifferSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.sniffer.Sniffer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnifferSoundInstance.class)
public abstract class SnifferSoundInstanceMixin extends AbstractTickableSoundInstance {

    @Shadow
    @Final
    private Sniffer sniffer;

    @SuppressWarnings("DataFlowIssue")
    protected SnifferSoundInstanceMixin() {
        super((SoundEvent) null, (SoundSource) null, SoundInstance.createUnseededRandom());
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void signed32$preciseCoordsOnTick(CallbackInfo ci) {
        this.x = this.sniffer.getX();
        this.y = this.sniffer.getY();
        this.z = this.sniffer.getZ();
    }
}