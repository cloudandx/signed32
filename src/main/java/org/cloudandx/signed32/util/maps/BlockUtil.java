package org.cloudandx.signed32.util.maps;

import org.cloudandx.signed32.util.map.Long2ObjectStripedMap;
import org.cloudandx.signed32.util.pos.IntBlockPos;

public final class BlockUtil {
    private static final int INITIAL_CAPACITY = 1 << 16;
    private static volatile Long2ObjectStripedMap<IntBlockPos> lookup = new Long2ObjectStripedMap<>(INITIAL_CAPACITY);
    private static volatile Long2ObjectStripedMap<IntBlockPos> oldLookup;
    private static volatile boolean hasEntries = false;

    private BlockUtil() {}

    // 提供給 BlockPosMixin 呼叫
    public static boolean isEmpty() {
        return !hasEntries;
    }

    public static void put(long key, int x, int y, int z) {
        IntBlockPos prev = lookup.get(key);
        if (prev != null) {
            return;
        }
        IntBlockPos np = new IntBlockPos(x, y, z);
        lookup.putIfAbsent(key, np);
        hasEntries = true;
    }

    public static IntBlockPos get(long key) {
        if (!hasEntries) return null;
        IntBlockPos bp = lookup.get(key);
        if (bp != null) return bp;
        Long2ObjectStripedMap<IntBlockPos> old = oldLookup;
        return old != null ? old.get(key) : null;
    }

    public static int size() {
        return lookup.size();
    }

    public static void clearAll() {
        lookup.clear();
        Long2ObjectStripedMap<IntBlockPos> old = oldLookup;
        if (old != null) {
            old.clear();
        }
        hasEntries = false;
    }

    public static void swap() {
        oldLookup = lookup;
        lookup = new Long2ObjectStripedMap<>(INITIAL_CAPACITY);
        hasEntries = (oldLookup != null && !oldLookup.isEmpty());
    }
}