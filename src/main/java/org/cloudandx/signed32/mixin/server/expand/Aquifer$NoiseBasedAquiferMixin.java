package org.cloudandx.signed32.mixin.server.expand;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.level.levelgen.Aquifer;

import org.cloudandx.signed32.util.maps.AquiferUtil;
import org.cloudandx.signed32.util.pos.AquiferPos;
import org.cloudandx.signed32.util.pos.IntBlockPos;

@Mixin(Aquifer.NoiseBasedAquifer.class)
public abstract class Aquifer$NoiseBasedAquiferMixin {

    @Final
    @Shadow
    private Aquifer.FluidStatus[] aquiferCache;

    @Final
    @Shadow
    private long[] aquiferLocationCache;

    @Shadow
    protected abstract Aquifer.FluidStatus computeFluid(int x, int y, int z);

    // computeSubstance 循環的 3 處座標解包 -> 側信道反查
    // 跨 tick 保活

    @Redirect(method = "computeSubstance", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;getX(J)I"))
    private static int signed32$getX(long blockNode) {
        return resolveX(blockNode);
    }

    @Redirect(method = "computeSubstance", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;getY(J)I"))
    private static int signed32$getY(long blockNode) {
        return resolveY(blockNode);
    }

    @Redirect(method = "computeSubstance", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;getZ(J)I"))
    private static int signed32$getZ(long blockNode) {
        return resolveZ(blockNode);
    }

    @Unique
    private static int resolveX(long packedPos) {
        AquiferPos ap = AquiferUtil.get(packedPos);
        if (ap != null) {
            return ap.x;
        }
        IntBlockPos bp = IntBlockPos.getBlockPos(packedPos);
        AquiferUtil.put(packedPos, bp.x, bp.y, bp.z); // 補註冊：此後跨 tick 保活
        return bp.x;
    }

    @Unique
    private static int resolveY(long packedPos) {
        AquiferPos ap = AquiferUtil.get(packedPos);
        if (ap != null) {
            return ap.y;
        }
        IntBlockPos bp = IntBlockPos.getBlockPos(packedPos);
        AquiferUtil.put(packedPos, bp.x, bp.y, bp.z);
        return bp.y;
    }

    @Unique
    private static int resolveZ(long packedPos) {
        AquiferPos ap = AquiferUtil.get(packedPos);
        if (ap != null) {
            return ap.z;
        }
        IntBlockPos bp = IntBlockPos.getBlockPos(packedPos);
        AquiferUtil.put(packedPos, bp.x, bp.y, bp.z);
        return bp.z;
    }

    @Overwrite
    private Aquifer.FluidStatus getAquiferStatus(int index) {
        Aquifer.FluidStatus s = aquiferCache[index];
        if (s != null) {
            return s;
        }
        long packedPos = aquiferLocationCache[index];
        AquiferPos ap = AquiferUtil.get(packedPos);
        int bx, by, bz;
        if (ap != null) {
            bx = ap.x;
            by = ap.y;
            bz = ap.z;
        } else {
            IntBlockPos bp = IntBlockPos.getBlockPos(packedPos);
            bx = bp.x;
            by = bp.y;
            bz = bp.z;
            AquiferUtil.put(packedPos, bx, by, bz); // 補註冊：此後跨 tick 保活
        }
        Aquifer.FluidStatus c = computeFluid(bx, by, bz);
        aquiferCache[index] = c;
        return c;
    }
}