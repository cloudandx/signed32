package org.cloudandx.signed32.client.mixin.client.fix.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.GuardianAttackSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.monster.Guardian;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuardianAttackSoundInstance.class)
public abstract class GuardianAttackSoundInstanceMixin extends AbstractTickableSoundInstance {

    @Shadow
    @Final
    private Guardian guardian;

    @SuppressWarnings("DataFlowIssue")
    protected GuardianAttackSoundInstanceMixin() {
        super((SoundEvent) null, (SoundSource) null, SoundInstance.createUnseededRandom());
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void signed32$preciseCoordsOnTick(CallbackInfo ci) {
        this.x = this.guardian.getX();
        this.y = this.guardian.getY();
        this.z = this.guardian.getZ();
    }
}