package org.cloudandx.signed32.util.maps;

import org.cloudandx.signed32.util.map.Long2ObjectStripedMap;
import org.cloudandx.signed32.util.pos.IntSectionPos;

public final class SectionUtil {
    private static final Long2ObjectStripedMap<IntSectionPos> lookup = new Long2ObjectStripedMap<>(1 << 16);
    // 記憶體中是否有超過 3355 萬的資料旗標（0 鎖極速判斷）
    private static volatile boolean hasEntries = false;

    private SectionUtil() {}

    // 解決 SectionPosMixin 紅字：直接回傳是否為空，完全不碰 256 個分段鎖
    public static boolean isEmpty() {
        return !hasEntries;
    }

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
        hasEntries = true; // 有寫入極限座標時標記為 true
    }

    public static IntSectionPos get(long key) {
        if (!hasEntries) return null; // 常規座標直接返回 null，不進入 Map 查表
        return lookup.get(key);
    }

    public static int size() {
        return lookup.size();
    }

    public static void clearAll() {
        lookup.clear();
        hasEntries = false;
    }

    public static void trim(long currentTick) {
        long cutoff = currentTick - 600;
        lookup.removeIf(p -> p.lastAccess < cutoff);
        hasEntries = !lookup.isEmpty();
    }
}