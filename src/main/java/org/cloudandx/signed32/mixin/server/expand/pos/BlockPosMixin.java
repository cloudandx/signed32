package org.cloudandx.signed32.mixin.server.expand.pos;

import net.minecraft.core.BlockPos;
import org.cloudandx.signed32.config.Signed32Config;
import org.cloudandx.signed32.util.hash.HashMath;
import org.cloudandx.signed32.util.maps.BlockUtil;
import org.cloudandx.signed32.util.pos.IntBlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

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
        if (Signed32Config.INSTANCE.expandBlockPos) {
            IntBlockPos pos = BlockUtil.get(packedPos);
            if (pos != null) {
                return pos.x;
            }
        }
        return (int) (packedPos << 64 - X_OFFSET - PACKED_HORIZONTAL_LENGTH >> 64 - PACKED_HORIZONTAL_LENGTH);
    }

    @Overwrite
    public static int getY(long packedPos) {
        if (Signed32Config.INSTANCE.expandBlockPos) {
            IntBlockPos pos = BlockUtil.get(packedPos);
            if (pos != null) {
                return pos.y;
            }
        }
        return (int) (packedPos << 64 - PACKED_Y_LENGTH >> 64 - PACKED_Y_LENGTH);
    }

    @Overwrite
    public static int getZ(long packedPos) {
        if (Signed32Config.INSTANCE.expandBlockPos) {
            IntBlockPos pos = BlockUtil.get(packedPos);
            if (pos != null) {
                return pos.z;
            }
        }
        return (int) (packedPos << 64 - Z_OFFSET - PACKED_HORIZONTAL_LENGTH >> 64 - PACKED_HORIZONTAL_LENGTH);
    }

    @Overwrite
    public static long offset(long packedPos, int dx, int dy, int dz) {
        if (Signed32Config.INSTANCE.expandBlockPos) {
            IntBlockPos pos = IntBlockPos.getBlockPos(packedPos);
            int nx = pos.x + dx;
            int ny = pos.y + dy;
            int nz = pos.z + dz;
            long key = HashMath.hash(nx, ny, nz);
            BlockUtil.put(key, nx, ny, nz);
            return key;
        }
        return dx == 0 && dy == 0 && dz == 0 ? packedPos : asLong(getX(packedPos) + dx, getY(packedPos) + dy, getZ(packedPos) + dz);
    }

    @Overwrite
    public static BlockPos of(long packedPos) {
        if (Signed32Config.INSTANCE.expandBlockPos) {
            IntBlockPos pos = IntBlockPos.getBlockPos(packedPos);
            return new BlockPos(pos.x, pos.y, pos.z);
        }
        return new BlockPos(getX(packedPos), getY(packedPos), getZ(packedPos));
    }

    @Overwrite
    public static long getFlatIndex(long packedPos) {
        if (Signed32Config.INSTANCE.expandBlockPos) {
            IntBlockPos pos = IntBlockPos.getBlockPos(packedPos);
            return HashMath.hash(pos.x, 0, pos.z);
        }
        return packedPos & -16L;
    }

    @Overwrite
    public long asLong() {
        BlockPos self = (BlockPos) (Object) this;
        int x = self.getX();
        int y = self.getY();
        int z = self.getZ();

        if (Signed32Config.INSTANCE.expandBlockPos) {
            long key = HashMath.hash((long) x, (long) y, (long) z);
            BlockUtil.put(key, x, y, z);
            return key;
        }
        return asLong(x, y, z);
    }

    @Overwrite
    public static long asLong(int x, int y, int z) {
        if (Signed32Config.INSTANCE.expandBlockPos) {
            long key = HashMath.hash((long) x, (long) y, (long) z);
            BlockUtil.put(key, x, y, z);
            return key;
        }
        long l = 0L;
        l |= ((long) x & (1L << PACKED_HORIZONTAL_LENGTH) - 1L) << X_OFFSET;
        l |= ((long) y & (1L << PACKED_Y_LENGTH) - 1L);
        return l | ((long) z & (1L << PACKED_HORIZONTAL_LENGTH) - 1L) << Z_OFFSET;
    }
}