package org.cloudandx.signed32.util.pos;

import net.minecraft.core.SectionPos;
import org.cloudandx.signed32.util.maps.Common;
import org.cloudandx.signed32.util.maps.SectionUtil;

public class IntSectionPos implements Comparable<IntSectionPos> {
    public final int x, y, z;
    public volatile long lastAccess;

    public IntSectionPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static int blockToSectionCoord(int blockCoord) {
        return blockCoord >> 4;
    }

    public static int sectionRelative(int blockCoord) {
        return blockCoord & 15;
    }

    public int minBlockX() {
        return x << 4;
    }

    public int minBlockY() {
        return y << 4;
    }

    public int minBlockZ() {
        return z << 4;
    }

    @Override
    public int compareTo(IntSectionPos o) {
        int c = Integer.compare(this.x, o.x);
        if (c != 0) return c;
        c = Integer.compare(this.y, o.y);
        if (c != 0) return c;
        return Integer.compare(this.z, o.z);
    }

    public IntSectionPos offset(int dx, int dy, int dz) {
        return new IntSectionPos(x + dx, y + dy, z + dz);
    }

    public static IntSectionPos getSectionPos(long key) {
        IntSectionPos sp = SectionUtil.get(key);
        if (sp != null) {
            sp.lastAccess = Common.getTick();
            return sp;
        }
        return new IntSectionPos(SectionPos.x(key), SectionPos.y(key), SectionPos.z(key));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntSectionPos that)) return false;
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
        return "IntSectionPos[" + x + ", " + y + ", " + z + "]";
    }
}