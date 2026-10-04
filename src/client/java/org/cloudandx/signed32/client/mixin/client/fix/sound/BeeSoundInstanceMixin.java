package org.cloudandx.signed32.client.mixin.client.fix.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.BeeSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.bee.Bee;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeeSoundInstance.class)
public abstract class BeeSoundInstanceMixin extends AbstractTickableSoundInstance {

    @Shadow
    @Final
    protected Bee bee;

    @SuppressWarnings("DataFlowIssue")
    protected BeeSoundInstanceMixin() {
        super((SoundEvent) null, (SoundSource) null, SoundInstance.createUnseededRandom());
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void signed32$preciseCoordsOnCreate(CallbackInfo ci) {
        this.x = this.bee.getX();
        this.y = this.bee.getY();
        this.z = this.bee.getZ();
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void signed32$preciseCoordsOnTick(CallbackInfo ci) {
        this.x = this.bee.getX();
        this.y = this.bee.getY();
        this.z = this.bee.getZ();
    }
}