package org.cloudandx.signed32.util.maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Common {
    private static final Logger LOGGER = LoggerFactory.getLogger("Signed32");
    private static volatile long lastConflictInfo = 0;
    private static volatile long currentTick = 0;

    private Common() {}

    /** 獲取全域 Tick，支援 TTL 判定 */
    public static long getTick() {
        return currentTick;
    }

    /** 伺服器/客戶端 Tick 事件驅動更新 */
    public static void updateTick(long tick) {
        currentTick = tick;
    }

    public static void conflict(String kind, long key, int ox, int oy, int oz, int nx, int ny, int nz) {
        long now = System.currentTimeMillis();
        if (now - lastConflictInfo < 1000) {
            return;
        }
        lastConflictInfo = now;
        LOGGER.warn("Hash Conflicted! [{}] key=0x{} old={},{},{} new={},{},{}",
                kind, Long.toHexString(key), ox, oy, oz, nx, ny, nz);
    }
}