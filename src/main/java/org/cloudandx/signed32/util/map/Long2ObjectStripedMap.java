package org.cloudandx.signed32.util.map;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.concurrent.locks.StampedLock;
import java.util.function.LongFunction;
import java.util.function.Predicate;

/**
 * 無裝箱分段 map：fastutil Long2ObjectOpenHashMap × 256 段 + StampedLock。
 */
public final class Long2ObjectStripedMap<V> {

    private static final int SEG_BITS = 8;
    private static final int SEG_COUNT = 1 << SEG_BITS;
    private static final int SEG_MASK = SEG_COUNT - 1;

    private final Long2ObjectOpenHashMap<V>[] segments;
    private final StampedLock[] locks;

    @SuppressWarnings("unchecked")
    public Long2ObjectStripedMap(int expectedCapacity) {
        int perSeg = Math.max(16, (expectedCapacity >> SEG_BITS) + 1);
        segments = new Long2ObjectOpenHashMap[SEG_COUNT];
        locks = new StampedLock[SEG_COUNT];
        for (int i = 0; i < SEG_COUNT; i++) {
            segments[i] = new Long2ObjectOpenHashMap<>(perSeg);
            locks[i] = new StampedLock();
        }
    }

    private int seg(long key) {
        return (int) ((key ^ (key >>> 32)) & SEG_MASK);
    }

    public V get(long key) {
        int s = seg(key);
        StampedLock l = locks[s];
        long stamp = l.tryOptimisticRead();
        V v;
        try {
            v = segments[s].get(key);
        } catch (ArrayIndexOutOfBoundsException e) {
            stamp = l.readLock();
            try {
                v = segments[s].get(key);
            } finally {
                l.unlockRead(stamp);
            }
            return v;
        }
        if (!l.validate(stamp)) {
            stamp = l.readLock();
            try {
                v = segments[s].get(key);
            } finally {
                l.unlockRead(stamp);
            }
        }
        return v;
    }

    public V put(long key, V val) {
        int s = seg(key);
        StampedLock l = locks[s];
        long stamp = l.writeLock();
        try {
            return segments[s].put(key, val);
        } finally {
            l.unlockWrite(stamp);
        }
    }

    public V remove(long key) {
        int s = seg(key);
        StampedLock l = locks[s];
        long stamp = l.writeLock();
        try {
            return segments[s].remove(key);
        } finally {
            l.unlockWrite(stamp);
        }
    }

    public V putIfAbsent(long key, V val) {
        int s = seg(key);
        StampedLock l = locks[s];
        long stamp = l.writeLock();
        try {
            return segments[s].putIfAbsent(key, val);
        } finally {
            l.unlockWrite(stamp);
        }
    }

    public V computeIfAbsent(long key, LongFunction<V> mapping) {
        int s = seg(key);
        StampedLock l = locks[s];
        long stamp = l.writeLock();
        try {
            Long2ObjectOpenHashMap<V> m = segments[s];
            V v = m.get(key);
            if (v == null) {
                v = mapping.apply(key);
                m.put(key, v);
            }
            return v;
        } finally {
            l.unlockWrite(stamp);
        }
    }

    public boolean containsKey(long key) {
        int s = seg(key);
        StampedLock l = locks[s];
        long stamp = l.readLock();
        try {
            return segments[s].containsKey(key);
        } finally {
            l.unlockRead(stamp);
        }
    }

    public void removeIf(Predicate<V> predicate) {
        for (int i = 0; i < SEG_COUNT; i++) {
            StampedLock l = locks[i];
            long stamp = l.writeLock();
            try {
                segments[i].values().removeIf(predicate);
            } finally {
                l.unlockWrite(stamp);
            }
        }
    }

    public void clear() {
        for (int i = 0; i < SEG_COUNT; i++) {
            StampedLock l = locks[i];
            long stamp = l.writeLock();
            try {
                segments[i].clear();
            } finally {
                l.unlockWrite(stamp);
            }
        }
    }

    public int size() {
        int n = 0;
        for (int i = 0; i < SEG_COUNT; i++) {
            StampedLock l = locks[i];
            long stamp = l.readLock();
            try {
                n += segments[i].size();
            } finally {
                l.unlockRead(stamp);
            }
        }
        return n;
    }

    public boolean isEmpty() {
        for (int i = 0; i < SEG_COUNT; i++) {
            StampedLock l = locks[i];
            long stamp = l.readLock();
            try {
                if (!segments[i].isEmpty()) {
                    return false;
                }
            } finally {
                l.unlockRead(stamp);
            }
        }
        return true;
    }
}