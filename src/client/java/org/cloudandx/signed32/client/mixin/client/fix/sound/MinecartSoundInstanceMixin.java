package org.cloudandx.signed32.client.mixin.client.fix.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.MinecartSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecartSoundInstance.class)
public abstract class MinecartSoundInstanceMixin extends AbstractTickableSoundInstance {

    @Shadow
    @Final
    private AbstractMinecart minecart;

    @SuppressWarnings("DataFlowIssue")
    protected MinecartSoundInstanceMixin() {
        super((SoundEvent) null, (SoundSource) null, SoundInstance.createUnseededRandom());
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void signed32$preciseCoordsOnCreate(CallbackInfo ci) {
        this.x = this.minecart.getX();
        this.y = this.minecart.getY();
        this.z = this.minecart.getZ();
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void signed32$preciseCoordsOnTick(CallbackInfo ci) {
        this.x = this.minecart.getX();
        this.y = this.minecart.getY();
        this.z = this.minecart.getZ();
    }
}