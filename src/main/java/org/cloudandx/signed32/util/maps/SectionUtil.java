package org.cloudandx.signed32.util.maps;

import org.cloudandx.signed32.util.map.Long2ObjectStripedMap;
import org.cloudandx.signed32.util.pos.IntSectionPos;

public final class SectionUtil {
    private static final Long2ObjectStripedMap<IntSectionPos> lookup = new Long2ObjectStripedMap<>(1 << 20);

    private SectionUtil() {}

    public static void put(long key, int x, int y, int z) {
        IntSectionPos prev = lookup.get(key);
        if (prev != null) {
            if (prev.x != x || prev.y != y || prev.z != z) {
                Common.conflict("section", key, prev.x, prev.y, prev.z, x, y, z);
            }
            return;
        }
        IntSectionPos np = new IntSectionPos(x, y, z);
        prev = lookup.putIfAbsent(key, np);
        if (prev != null) {
            if (prev.x != x || prev.y != y || prev.z != z) {
                Common.conflict("section", key, prev.x, prev.y, prev.z, x, y, z);
            }
        }
    }

    public static IntSectionPos get(long key) {
        return lookup.get(key);
    }

    public static int size() {
        return lookup.size();
    }

    public static void clearAll() {
        lookup.clear();
    }

    public static void trim(long currentTick) {
        long cutoff = currentTick - 600;
        lookup.removeIf(p -> p.lastAccess < cutoff);
    }
}