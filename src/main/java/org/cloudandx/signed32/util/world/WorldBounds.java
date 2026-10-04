package org.cloudandx.signed32.util.world;

/**
 * Signed32 邊界與空間常數統一規範。
 */
public final class WorldBounds {
    private WorldBounds() {}

    /** 正方向最後一個可玩方塊 = 2,147,483,631 = 2^31 - 17 */
    public static final int MAX_PLAYABLE_BLOCK = 2_147_483_631;
    /** 負方向最後一個可玩方塊 = -2,147,483,632 */
    public static final int MIN_PLAYABLE_BLOCK = ~MAX_PLAYABLE_BLOCK;

    /** 正方向最後一個可表示區段 = 134,217,727 = 2^27 - 1 */
    public static final int MAX_SECTION = (1 << 27) - 1;
    /** 負方向最後一個可表示區段 = -134,217,728 = -2^27 */
    public static final int MIN_SECTION = -(1 << 27);

    /** 正方向最後一個可玩區段 = 134,217,726 = 2^27 - 2 */
    public static final int MAX_PLAYABLE_SECTION = (1 << 27) - 2;
    /** 負方向最後一個可玩區段 = -134,217,727 */
    public static final int MIN_PLAYABLE_SECTION = ~MAX_PLAYABLE_SECTION;

    /** 主世界預設建築高度範圍 */
    public static final int DEFAULT_MIN_Y = -64;
    public static final int DEFAULT_MAX_Y = 320;

    public static boolean inBlock(int v) {
        return v >= MIN_PLAYABLE_BLOCK && v <= MAX_PLAYABLE_BLOCK;
    }

    public static boolean inBlock(long v) {
        return v >= MIN_PLAYABLE_BLOCK && v <= MAX_PLAYABLE_BLOCK;
    }

    public static boolean inBlockXZ(int x, int z) {
        return inBlock(x) && inBlock(z);
    }

    public static boolean inBlockXZ(long x, long z) {
        return inBlock(x) && inBlock(z);
    }

    public static boolean inSection(int sy) {
        return sy >= MIN_PLAYABLE_SECTION && sy <= MAX_PLAYABLE_SECTION;
    }

    public static boolean inSectionAbsolute(int sy) {
        return sy >= MIN_SECTION && sy <= MAX_SECTION;
    }

    public static boolean inChunk(int v) {
        return v >= MIN_PLAYABLE_SECTION && v <= MAX_PLAYABLE_SECTION;
    }

    public static boolean inChunkRange(int cx, int cz) {
        return inChunk(cx) && inChunk(cz);
    }

    public static boolean inBuildHeight(int y) {
        return y >= DEFAULT_MIN_Y && y < DEFAULT_MAX_Y;
    }

    public static boolean inBuildHeightRange(int minY, int maxY) {
        return minY >= DEFAULT_MIN_Y && maxY < DEFAULT_MAX_Y;
    }

    public static int clampBlockCoord(int v) {
        return v > MAX_PLAYABLE_BLOCK ? MAX_PLAYABLE_BLOCK
                : v < MIN_PLAYABLE_BLOCK ? MIN_PLAYABLE_BLOCK : v;
    }

    public static int clampBlockCoord(long v) {
        return v > MAX_PLAYABLE_BLOCK ? MAX_PLAYABLE_BLOCK
                : v < MIN_PLAYABLE_BLOCK ? MIN_PLAYABLE_BLOCK : (int) v;
    }

    public static int clampChunk(int v) {
        return v > MAX_PLAYABLE_SECTION ? MAX_PLAYABLE_SECTION
                : v < MIN_PLAYABLE_SECTION ? MIN_PLAYABLE_SECTION : v;
    }
}