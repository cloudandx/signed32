package org.cloudandx.signed32.mixin.expand.pos;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import org.cloudandx.signed32.util.hash.HashMath;
import org.cloudandx.signed32.util.maps.BlockUtil;
import org.cloudandx.signed32.util.pos.IntBlockPos;

import net.minecraft.core.BlockPos;

@Mixin(BlockPos.class)
public abstract class BlockPosMixin {
    @Final
    @Shadow
    private static int PACKED_HORIZONTAL_LENGTH;
    @Final
    @Shadow
    private static int PACKED_Y_LENGTH;
    @Final
    @Shadow
    private static int Z_OFFSET;
    @Final
    @Shadow
    private static int X_OFFSET;

    @Overwrite
    public static int getX(long packedPos) {
        IntBlockPos pos = BlockUtil.get(packedPos);
        if (pos != null) {
            return pos.x;
        }
        return (int) (packedPos << 64 - X_OFFSET - PACKED_HORIZONTAL_LENGTH >> 64 - PACKED_HORIZONTAL_LENGTH);
    }

    @Overwrite
    public static int getY(long packedPos) {
        IntBlockPos pos = BlockUtil.get(packedPos);
        if (pos != null) {
            return pos.y;
        }
        return (int) (packedPos << 64 - PACKED_Y_LENGTH >> 64 - PACKED_Y_LENGTH);
    }

    @Overwrite
    public static int getZ(long packedPos) {
        IntBlockPos pos = BlockUtil.get(packedPos);
        if (pos != null) {
            return pos.z;
        }
        return (int) (packedPos << 64 - Z_OFFSET - PACKED_HORIZONTAL_LENGTH >> 64 - PACKED_HORIZONTAL_LENGTH);
    }

    @Overwrite
    public static long offset(long packedPos, int dx, int dy, int dz) {
        IntBlockPos pos = IntBlockPos.getBlockPos(packedPos);
        int nx = pos.x + dx;
        int ny = pos.y + dy;
        int nz = pos.z + dz;
        long key = HashMath.hash(nx, ny, nz);
        BlockUtil.put(key, nx, ny, nz);
        return key;
    }

    @Overwrite
    public static BlockPos of(long packedPos) {
        IntBlockPos pos = IntBlockPos.getBlockPos(packedPos);
        return new BlockPos(pos.x, pos.y, pos.z);
    }

    @Overwrite
    public static long getFlatIndex(long packedPos) {
        IntBlockPos pos = IntBlockPos.getBlockPos(packedPos);
        return HashMath.hash(pos.x, 0, pos.z);
    }

    @Overwrite
    public long asLong() {
        BlockPos self = (BlockPos) (Object) this;
        int x = self.getX();
        int y = self.getY();
        int z = self.getZ();
        long key = HashMath.hash((long) x, (long) y, (long) z);
        BlockUtil.put(key, x, y, z);
        return key;
    }

    @Overwrite
    public static long asLong(int x, int y, int z) {
        long key = HashMath.hash((long) x, (long) y, (long) z);
        BlockUtil.put(key, x, y, z);
        return key;
    }
}