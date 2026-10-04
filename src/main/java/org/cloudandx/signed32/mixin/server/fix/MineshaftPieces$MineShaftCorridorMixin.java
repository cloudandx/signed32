package org.cloudandx.signed32.mixin.server.fix;

import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.cloudandx.signed32.util.world.WorldBounds;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;

@Mixin(targets = "net.minecraft.world.level.levelgen.structure.structures.MineshaftPieces$MineShaftCorridor")
public abstract class MineshaftPieces$MineShaftCorridorMixin {

    @Inject(method = "addChildren", at = @At("HEAD"), cancellable = true)
    private void skipOverflowingBox(StructurePiece startPiece, StructurePiecesBuilder builder, RandomSource random, CallbackInfo ci) {
        BoundingBox bb = ((StructurePiece) (Object) this).getBoundingBox();
        if (bb.minX() > WorldBounds.MAX_PLAYABLE_BLOCK - 100 || bb.maxX() > WorldBounds.MAX_PLAYABLE_BLOCK - 100
                || bb.minZ() > WorldBounds.MAX_PLAYABLE_BLOCK - 100 || bb.maxZ() > WorldBounds.MAX_PLAYABLE_BLOCK - 100) {
            ci.cancel();
        }
    }
}