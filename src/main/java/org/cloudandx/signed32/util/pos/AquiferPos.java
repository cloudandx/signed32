package org.cloudandx.signed32.util.pos;

/**
 * Aquifer 專用位置載體，含 lastAccess TTL 保活。
 */
public class AquiferPos {
    public final int x, y, z;
    public volatile long lastAccess;

    public AquiferPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}