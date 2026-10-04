package org.cloudandx.signed32.mixin.fix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.cloudandx.signed32.util.world.WorldBounds;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

@Mixin(BoundingBox.class)
public class BoundingBoxMixin {

    @Shadow private int minX, minY, minZ, maxX, maxY, maxZ;

    @Inject(method = "moved", at = @At("HEAD"), cancellable = true)
    private void safeMoved(int x, int y, int z, CallbackInfoReturnable<BoundingBox> cir) {
        long newMinX = (long) this.minX + x;
        long newMaxX = (long) this.maxX + x;
        long newMinZ = (long) this.minZ + z;
        long newMaxZ = (long) this.maxZ + z;
        if (!WorldBounds.inBlock(newMinX) || !WorldBounds.inBlock(newMaxX)
                || !WorldBounds.inBlock(newMinZ) || !WorldBounds.inBlock(newMaxZ)) {
            cir.setReturnValue((BoundingBox) (Object) this);
            return;
        }
        if (newMinX > newMaxX || newMinZ > newMaxZ) {
            cir.setReturnValue((BoundingBox) (Object) this);
            return;
        }
    }

    @Inject(method = "fromCorners", at = @At("RETURN"), cancellable = true)
    private static void clampFromCorners(Vec3i first, Vec3i second, CallbackInfoReturnable<BoundingBox> cir) {
        BoundingBox box = cir.getReturnValue();
        int newMaxX = Math.min(box.maxX(), WorldBounds.MAX_PLAYABLE_BLOCK - 2);
        int newMaxY = Math.min(box.maxY(), WorldBounds.MAX_PLAYABLE_BLOCK - 2);
        int newMaxZ = Math.min(box.maxZ(), WorldBounds.MAX_PLAYABLE_BLOCK - 2);
        if (newMaxX == box.maxX() && newMaxY == box.maxY() && newMaxZ == box.maxZ()) {
            return;
        }
        cir.setReturnValue(new BoundingBox(
                Math.min(box.minX(), newMaxX),
                Math.min(box.minY(), newMaxY),
                Math.min(box.minZ(), newMaxZ),
                newMaxX,
                newMaxY,
                newMaxZ));
    }
}