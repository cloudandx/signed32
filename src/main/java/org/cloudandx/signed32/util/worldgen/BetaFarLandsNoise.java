package org.cloudandx.signed32.util.worldgen;

import org.cloudandx.signed32.config.Signed32Config;
import java.util.Random;

public class BetaFarLandsNoise {
    private final PerlinOctave minLimit;
    private final PerlinOctave maxLimit;
    private final PerlinOctave mainNoise;

    public BetaFarLandsNoise(long seed) {
        Random rand = new Random(seed);
        this.minLimit = new PerlinOctave(rand, 16);
        this.maxLimit = new PerlinOctave(rand, 16);
        this.mainNoise = new PerlinOctave(rand, 8);
    }

    public double computeDeltaDensity(int blockX, int blockY, int blockZ, int threshold) {
        int absX = Math.abs(blockX);
        int absZ = Math.abs(blockZ);
        int maxCoord = Math.max(absX, absZ);

        int t1 = threshold;
        int t2 = Signed32Config.INSTANCE.fartherLandsThreshold;

        // 未達門檻：完全原版地形
        if (maxCoord < t1) {
            return 0.0;
        }

        // =========================================================================
        // 1. 邊境之地基礎映射 (對齊自然溢位點 12550825)
        // =========================================================================
        int effLX = (absX >= t1) ? (absX - t1 + 12550825) : absX;
        int effLZ = (absZ >= t1) ? (absZ - t1 + 12550825) : absZ;
        effLX = (blockX >= 0) ? effLX : -effLX;
        effLZ = (blockZ >= 0) ? effLZ : -effLZ;

        double sampleLimitX = effLX / 4.0;
        double sampleLimitY = (double) blockY / 8.0;
        double sampleLimitZ = effLZ / 4.0;

        double scaleXZ = 684.412;
        double scaleY = 684.412;

        // 計算 minLimit 與 maxLimit 的原生溢位差額 (還原立體起司穿孔結構)
        double deltaMin = this.minLimit.sampleDelta(sampleLimitX, sampleLimitY, sampleLimitZ, scaleXZ, scaleY, scaleXZ);
        double deltaMax = this.maxLimit.sampleDelta(sampleLimitX, sampleLimitY, sampleLimitZ, scaleXZ, scaleY, scaleXZ);

        // =========================================================================
        // 2. 選擇器映射 (邊境之地 -> 遙遠之地)
        // =========================================================================
        double effSX, effSZ;
        boolean capMain;

        if (maxCoord < t2) {
            // 【邊境之地】：保持原生頻率，平滑插值雕刻 3D 穿孔起司洞穴
            effSX = sampleLimitX;
            effSZ = sampleLimitZ;
            capMain = false;
        } else {
            // 【遙遠之地】：選擇器在溢位點失控，沿主軸凍結，拉伸為無限延伸的平行實心條帶
            int offset = maxCoord - t2 + 1004065925;
            effSX = (blockX >= 0) ? (offset / 4.0) : (-offset / 4.0);
            effSZ = (blockZ >= 0) ? (offset / 4.0) : (-offset / 4.0);
            capMain = true;
        }

        double rawMain = this.mainNoise.sample(effSX, sampleLimitY, effSZ, scaleXZ / 80.0, scaleY / 160.0, scaleXZ / 80.0, capMain);
        double selector = (rawMain / 10.0) + 0.5;

        double delta;
        if (selector < 0.0) {
            delta = deltaMin;
        } else if (selector > 1.0) {
            delta = deltaMax;
        } else {
            delta = deltaMin + (deltaMax - deltaMin) * selector;
        }

        // 最終確保密度收斂在區塊生成的合法邊界內
        if (delta > 64.0) return 64.0;
        if (delta < -64.0) return -64.0;
        return delta;
    }

    private static class PerlinOctave {
        private final PerlinSampler[] samplers;

        public PerlinOctave(Random rand, int count) {
            this.samplers = new PerlinSampler[count];
            for (int i = 0; i < count; i++) {
                this.samplers[i] = new PerlinSampler(rand);
            }
        }

        public double sampleDelta(double x, double y, double z, double sx, double sy, double sz) {
            double total = 0.0;
            double freq = 1.0;
            for (PerlinSampler s : this.samplers) {
                double c = s.sample(x * sx * freq, y * sy * freq, z * sz * freq, true);
                double u = s.sample(x * sx * freq, y * sy * freq, z * sz * freq, false);
                total += (c - u) / freq;
                freq /= 2.0;
            }
            return total;
        }

        public double sample(double x, double y, double z, double sx, double sy, double sz, boolean cap32Bit) {
            double total = 0.0;
            double freq = 1.0;
            for (PerlinSampler s : this.samplers) {
                total += s.sample(x * sx * freq, y * sy * freq, z * sz * freq, cap32Bit) / freq;
                freq /= 2.0;
            }
            return total;
        }
    }

    private static class PerlinSampler {
        private final int[] p = new int[512];

        public PerlinSampler(Random rand) {
            int[] perm = new int[256];
            for (int i = 0; i < 256; i++) perm[i] = i;
            for (int i = 0; i < 256; i++) {
                int j = rand.nextInt(256 - i) + i;
                int tmp = perm[i];
                perm[i] = perm[j];
                perm[j] = tmp;
            }
            for (int i = 0; i < 256; i++) {
                p[i] = perm[i];
                p[i + 256] = perm[i];
            }
        }

        public double sample(double x, double y, double z, boolean cap32Bit) {
            long lX, lY, lZ;
            if (cap32Bit) {
                lX = (x > 2147483647.0) ? 2147483647L : ((x < -2147483648.0) ? -2147483648L : (long) Math.floor(x));
                lY = (y > 2147483647.0) ? 2147483647L : ((y < -2147483648.0) ? -2147483648L : (long) Math.floor(y));
                lZ = (z > 2147483647.0) ? 2147483647L : ((z < -2147483648.0) ? -2147483648L : (long) Math.floor(z));
            } else {
                lX = (long) Math.floor(x);
                lY = (long) Math.floor(y);
                lZ = (long) Math.floor(z);
            }

            int X = (int) (lX & 255);
            int Y = (int) (lY & 255);
            int Z = (int) (lZ & 255);

            double dx = x - (double) lX;
            double dy = y - (double) lY;
            double dz = z - (double) lZ;

            double u = dx * dx * dx * (dx * (dx * 6.0 - 15.0) + 10.0);
            double v = dy * dy * dy * (dy * (dy * 6.0 - 15.0) + 10.0);
            double w = dz * dz * dz * (dz * (dz * 6.0 - 15.0) + 10.0);

            int a = p[X] + Y;
            int aa = p[a] + Z;
            int ab = p[a + 1] + Z;
            int b = p[X + 1] + Y;
            int ba = p[b] + Z;
            int bb = p[b + 1] + Z;

            double l1 = lerp(u, grad(p[aa], dx, dy, dz), grad(p[ba], dx - 1, dy, dz));
            double l2 = lerp(u, grad(p[ab], dx, dy - 1, dz), grad(p[bb], dx - 1, dy - 1, dz));
            double l3 = lerp(u, grad(p[aa + 1], dx, dy, dz - 1), grad(p[ba + 1], dx - 1, dy, dz - 1));
            double l4 = lerp(u, grad(p[ab + 1], dx, dy - 1, dz - 1), grad(p[bb + 1], dx - 1, dy - 1, dz - 1));

            return lerp(w, lerp(v, l1, l2), lerp(v, l3, l4));
        }

        private static double lerp(double t, double a, double b) {
            return a + t * (b - a);
        }

        private static double grad(int hash, double x, double y, double z) {
            int h = hash & 15;
            double u = h < 8 ? x : y;
            double v = h < 4 ? y : (h == 12 || h == 14 ? x : z);
            return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
        }
    }
}
