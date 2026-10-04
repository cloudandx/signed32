package org.cloudandx.signed32.mixin.fix;

import org.cloudandx.signed32.util.world.WorldBounds;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PathNavigation.class)
public class PathNavigationMixin {

    @Inject(method = "createPath(Lnet/minecraft/core/BlockPos;I)Lnet/minecraft/world/level/pathfinder/Path;", at = @At("HEAD"), cancellable = true)
    private void signed32$skipOutOfBounds(BlockPos pos, int reachRange, CallbackInfoReturnable<Path> cir) {
        if (!WorldBounds.inBlockXZ(pos.getX(), pos.getZ())) {
            cir.setReturnValue(null);
        }
    }

    @Redirect(method = "createPath(Ljava/util/Set;IZIF)Lnet/minecraft/world/level/pathfinder/Path;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getMinY()I"))
    private int signed32$worldGenMinY(Level level) {
        return WorldBounds.DEFAULT_MIN_Y;
    }

    @Redirect(method = "createPath(Ljava/util/Set;IZIF)Lnet/minecraft/world/level/pathfinder/Path;", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"))
    private BlockPos signed32$clampOffset(BlockPos self, int dx, int dy, int dz) {
        long nx = (long) self.getX() + dx;
        long nz = (long) self.getZ() + dz;
        long ny = (long) self.getY() + dy;
        return new BlockPos(WorldBounds.clampBlockCoord(nx), (int) ny, WorldBounds.clampBlockCoord(nz));
    }
}