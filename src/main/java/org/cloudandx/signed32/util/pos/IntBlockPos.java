package org.cloudandx.signed32.util.pos;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.cloudandx.signed32.util.maps.BlockUtil;

public class IntBlockPos {
    public final int x, y, z;

    public IntBlockPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public IntBlockPos(BlockPos pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public IntBlockPos offset(Direction d) {
        return new IntBlockPos(x + d.getStepX(), y + d.getStepY(), z + d.getStepZ());
    }

    public IntBlockPos offset(int dx, int dy, int dz) {
        return new IntBlockPos(x + dx, y + dy, z + dz);
    }

    public static IntBlockPos getBlockPos(long key) {
        IntBlockPos bp = BlockUtil.get(key);
        if (bp != null) {
            return bp;
        }
        return new IntBlockPos(BlockPos.getX(key), BlockPos.getY(key), BlockPos.getZ(key));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntBlockPos that)) return false;
        return x == that.x && y == that.y && z == that.z;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    @Override
    public String toString() {
        return "IntBlockPos[" + x + ", " + y + ", " + z + "]";
    }
}