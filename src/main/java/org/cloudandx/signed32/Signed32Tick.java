package org.cloudandx.signed32;

import java.util.concurrent.atomic.AtomicLong;

public final class Signed32Tick {
    private static final AtomicLong CURRENT_TICK = new AtomicLong(0);

    private Signed32Tick() {}

    public static void update(long tick) {
        CURRENT_TICK.set(tick);
    }

    public static long getNow() {
        return CURRENT_TICK.get();
    }
}