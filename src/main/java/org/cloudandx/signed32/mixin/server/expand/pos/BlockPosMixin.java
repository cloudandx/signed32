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
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockPos.class)
public abstract class BlockPosMixin {

    @Final @Shadow private static int PACKED_HORIZONTAL_LENGTH;
    @Final @Shadow private static int PACKED_Y_LENGTH;
    @Final @Shadow private static int Z_OFFSET;
    @Final @Shadow private static int X_OFFSET;

    @Unique
    private static boolean signed32$isVanillaBlock(int x, int y, int z) {
        return x >= -33554432 && x <= 33554431
                && z >= -33554432 && z <= 33554431
                && y >= -2048 && y <= 2047;
    }

    @Unique
    private static long signed32$packVanilla(int x, int y, int z) {
        long l = 0L;
        l |= ((long) x & (1L << PACKED_HORIZONTAL_LENGTH) - 1L) << X_OFFSET;
        l |= ((long) y & (1L << PACKED_Y_LENGTH) - 1L);
        return l | ((long) z & (1L << PACKED_HORIZONTAL_LENGTH) - 1L) << Z_OFFSET;
    }

    @Overwrite
    public static int getX(long packedPos) {
        if (Signed32Config.INSTANCE.expandBlockPos && !BlockUtil.isEmpty()) {
            IntBlockPos pos = BlockUtil.get(packedPos);
            if (pos != null) {
                return pos.x;
            }
        }
        return (int) (packedPos << 64 - X_OFFSET - PACKED_HORIZONTAL_LENGTH >> 64 - PACKED_HORIZONTAL_LENGTH);
    }

    @Overwrite
    public static int getY(long packedPos) {
        if (Signed32Config.INSTANCE.expandBlockPos && !BlockUtil.isEmpty()) {
            IntBlockPos pos = BlockUtil.get(packedPos);
            if (pos != null) {
                return pos.y;
            }
        }
        return (int) (packedPos << 64 - PACKED_Y_LENGTH >> 64 - PACKED_Y_LENGTH);
    }

    @Overwrite
    public static int getZ(long packedPos) {
        if (Signed32Config.INSTANCE.expandBlockPos && !BlockUtil.isEmpty()) {
            IntBlockPos pos = BlockUtil.get(packedPos);
            if (pos != null) {
                return pos.z;
            }
        }
        return (int) (packedPos << 64 - Z_OFFSET - PACKED_HORIZONTAL_LENGTH >> 64 - PACKED_HORIZONTAL_LENGTH);
    }

    @Overwrite
    public static long offset(long packedPos, int dx, int dy, int dz) {
        if (dx == 0 && dy == 0 && dz == 0) return packedPos;

        if (Signed32Config.INSTANCE.expandBlockPos) {
            if (!BlockUtil.isEmpty()) {
                IntBlockPos pos = BlockUtil.get(packedPos);
                if (pos != null) {
                    int nx = pos.x + dx, ny = pos.y + dy, nz = pos.z + dz;
                    if (signed32$isVanillaBlock(nx, ny, nz)) {
                        return signed32$packVanilla(nx, ny, nz);
                    }
                    long key = HashMath.hash(nx, ny, nz);
                    BlockUtil.put(key, nx, ny, nz);
                    return key;
                }
            }

            int nx = getX(packedPos) + dx;
            int ny = getY(packedPos) + dy;
            int nz = getZ(packedPos) + dz;
            if (signed32$isVanillaBlock(nx, ny, nz)) {
                return signed32$packVanilla(nx, ny, nz);
            }
            long key = HashMath.hash(nx, ny, nz);
            BlockUtil.put(key, nx, ny, nz);
            return key;
        }

        return signed32$packVanilla(getX(packedPos) + dx, getY(packedPos) + dy, getZ(packedPos) + dz);
    }

    @Overwrite
    public static BlockPos of(long packedPos) {
        if (Signed32Config.INSTANCE.expandBlockPos && !BlockUtil.isEmpty()) {
            IntBlockPos pos = BlockUtil.get(packedPos);
            if (pos != null) {
                return new BlockPos(pos.x, pos.y, pos.z);
            }
        }
        return new BlockPos(getX(packedPos), getY(packedPos), getZ(packedPos));
    }

    @Overwrite
    public static long getFlatIndex(long packedPos) {
        if (Signed32Config.INSTANCE.expandBlockPos && !BlockUtil.isEmpty()) {
            IntBlockPos pos = BlockUtil.get(packedPos);
            if (pos != null) {
                return HashMath.hash(pos.x, 0, pos.z);
            }
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
            if (!signed32$isVanillaBlock(x, y, z)) {
                long key = HashMath.hash((long) x, (long) y, (long) z);
                BlockUtil.put(key, x, y, z);
                return key;
            }
        }
        return signed32$packVanilla(x, y, z);
    }

    @Overwrite
    public static long asLong(int x, int y, int z) {
        if (Signed32Config.INSTANCE.expandBlockPos) {
            if (!signed32$isVanillaBlock(x, y, z)) {
                long key = HashMath.hash((long) x, (long) y, (long) z);
                BlockUtil.put(key, x, y, z);
                return key;
            }
        }
        return signed32$packVanilla(x, y, z);
    }
}