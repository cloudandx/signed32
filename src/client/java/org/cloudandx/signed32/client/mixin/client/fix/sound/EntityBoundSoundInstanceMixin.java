package org.cloudandx.signed32.client.mixin.client.fix.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityBoundSoundInstance.class)
public abstract class EntityBoundSoundInstanceMixin extends AbstractTickableSoundInstance {

    @Shadow
    @Final
    private Entity entity;

    @SuppressWarnings("DataFlowIssue")
    protected EntityBoundSoundInstanceMixin() {
        super((SoundEvent) null, (SoundSource) null, RandomSource.create());
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void signed32$entityCoords(SoundEvent event, SoundSource source, float volume, float pitch,
            Entity entity, long seed, CallbackInfo ci) {
        this.x = this.entity.getX();
        this.y = this.entity.getY();
        this.z = this.entity.getZ();
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void signed32$entityCoordsTick(CallbackInfo ci) {
        Entity bound = this.entity;
        if (bound != null && !bound.isRemoved()) {
            this.x = bound.getX();
            this.y = bound.getY();
            this.z = bound.getZ();
        }
    }
}