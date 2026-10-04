package org.cloudandx.signed32.mixin.fix;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.cloudandx.signed32.util.world.WorldBounds;

import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.MineshaftStructure;

@Mixin(MineshaftStructure.class)
public class MineshaftStructureMixin {

    @Inject(method = "findGenerationPoint", at = @At("HEAD"), cancellable = true)
    private void skipOutOfBounds(Structure.GenerationContext context,
            CallbackInfoReturnable<Optional<Structure.GenerationStub>> cir) {
        int cx = context.chunkPos().x();
        int cz = context.chunkPos().z();
        if (!WorldBounds.inChunkRange(cx, cz)) {
            cir.setReturnValue(Optional.empty());
        }
    }
}