package org.cloudandx.signed32.util.maps;

import org.cloudandx.signed32.util.map.Long2ObjectStripedMap;
import org.cloudandx.signed32.util.pos.AquiferPos;

public final class AquiferUtil {
    private static final Long2ObjectStripedMap<AquiferPos> lookup = new Long2ObjectStripedMap<>(1 << 20);

    private AquiferUtil() {}

    public static AquiferPos get(long key) {
        AquiferPos bp = lookup.get(key);
        if (bp != null) {
            bp.lastAccess = Common.getTick();
        }
        return bp;
    }

    public static void put(long key, int x, int y, int z) {
        AquiferPos prev = lookup.get(key);
        if (prev != null) {
            prev.lastAccess = Common.getTick();
            return;
        }
        AquiferPos np = new AquiferPos(x, y, z);
        np.lastAccess = Common.getTick();
        AquiferPos race = lookup.putIfAbsent(key, np);
        if (race != null) {
            race.lastAccess = Common.getTick();
        }
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