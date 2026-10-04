package org.cloudandx.signed32.util.worldgen;

import net.minecraft.world.level.levelgen.densityfunction.DensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import org.cloudandx.signed32.config.Signed32Config;
import org.jspecify.annotations.NonNull;

public class FarLandsDensitySampler implements DensitySampler {
    private final DensitySampler delegate;
    private final BetaFarLandsNoise farLandsNoise;

    public FarLandsDensitySampler(DensitySampler delegate, BetaFarLandsNoise farLandsNoise) {
        this.delegate = delegate;
        this.farLandsNoise = farLandsNoise;
    }

    private double getGridDelta(int gridX, int gridY, int gridZ, int threshold) {
        int bx = gridX * 4;
        int bz = gridZ * 4;
        if (Math.abs(bx) < threshold && Math.abs(bz) < threshold) {
            return 0.0;
        }
        return this.farLandsNoise.computeDeltaDensity(bx, gridY * 8, bz, threshold);
    }

    private static double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    @Override
    public float sampleValue(@NonNull SamplerContext context, int blockX, int blockY, int blockZ) {
        float original = this.delegate.sampleValue(context, blockX, blockY, blockZ);
        if (!Signed32Config.INSTANCE.farLandsNoise) return original;

        int threshold = Signed32Config.INSTANCE.farLandsThreshold;
        if (Math.abs(blockX) < threshold && Math.abs(blockZ) < threshold) {
            return original;
        }

        int gridX = Math.floorDiv(blockX, 4);
        int gridY = Math.floorDiv(blockY, 8);
        int gridZ = Math.floorDiv(blockZ, 4);

        double xFraction = (blockX & 3) / 4.0;
        double yFraction = (blockY & 7) / 8.0;
        double zFraction = (blockZ & 3) / 4.0;

        double v000 = getGridDelta(gridX, gridY, gridZ, threshold);
        double v100 = getGridDelta(gridX + 1, gridY, gridZ, threshold);
        double v010 = getGridDelta(gridX, gridY + 1, gridZ, threshold);
        double v110 = getGridDelta(gridX + 1, gridY + 1, gridZ, threshold);
        double v001 = getGridDelta(gridX, gridY, gridZ + 1, threshold);
        double v101 = getGridDelta(gridX + 1, gridY, gridZ + 1, threshold);
        double v011 = getGridDelta(gridX, gridY + 1, gridZ + 1, threshold);
        double v111 = getGridDelta(gridX + 1, gridY + 1, gridZ + 1, threshold);

        double i1 = lerp(xFraction, v000, v100);
        double i2 = lerp(xFraction, v001, v101);
        double i3 = lerp(xFraction, v010, v110);
        double i4 = lerp(xFraction, v011, v111);

        double j1 = lerp(zFraction, i1, i2);
        double j2 = lerp(zFraction, i3, i4);

        double delta = lerp(yFraction, j1, j2);
        return original + (float) delta;
    }

    @Override
    public void sampleVolume(@NonNull SamplerContext context, @NonNull DensityBuffer outputBuffer, @NonNull DensityVolume volume) {
        this.delegate.sampleVolume(context, outputBuffer, volume);

        if (!Signed32Config.INSTANCE.farLandsNoise) return;

        int sizeX = volume.sizeX();
        int sizeZ = volume.sizeZ();
        if (sizeX <= 0 || sizeZ <= 0) return;

        int minX = volume.blockX(0);
        int maxX = volume.blockX(sizeX - 1);
        int minZ = volume.blockZ(0);
        int maxZ = volume.blockZ(sizeZ - 1);

        int threshold = Signed32Config.INSTANCE.farLandsThreshold;

        if (Math.abs(minX) < threshold && Math.abs(maxX) < threshold
                && Math.abs(minZ) < threshold && Math.abs(maxZ) < threshold) {
            return;
        }

        int sizeY = volume.sizeY();
        int index = 0;

        for (int z = 0; z < sizeZ; z++) {
            int blockZ = volume.blockZ(z);
            int gridZ = Math.floorDiv(blockZ, 4);
            double zFraction = (blockZ & 3) / 4.0;

            for (int x = 0; x < sizeX; x++) {
                int blockX = volume.blockX(x);
                int gridX = Math.floorDiv(blockX, 4);
                double xFraction = (blockX & 3) / 4.0;

                int lastGridY = Integer.MAX_VALUE;
                double j1 = 0, j2 = 0;

                for (int y = 0; y < sizeY; y++) {
                    int blockY = volume.blockY(y);
                    int gridY = Math.floorDiv(blockY, 8);
                    double yFraction = (blockY & 7) / 8.0;

                    if (gridY != lastGridY) {
                        double v000 = getGridDelta(gridX, gridY, gridZ, threshold);
                        double v100 = getGridDelta(gridX + 1, gridY, gridZ, threshold);
                        double v010 = getGridDelta(gridX, gridY + 1, gridZ, threshold);
                        double v110 = getGridDelta(gridX + 1, gridY + 1, gridZ, threshold);
                        double v001 = getGridDelta(gridX, gridY, gridZ + 1, threshold);
                        double v101 = getGridDelta(gridX + 1, gridY, gridZ + 1, threshold);
                        double v011 = getGridDelta(gridX, gridY + 1, gridZ + 1, threshold);
                        double v111 = getGridDelta(gridX + 1, gridY + 1, gridZ + 1, threshold);

                        double i1 = lerp(xFraction, v000, v100);
                        double i2 = lerp(xFraction, v001, v101);
                        double i3 = lerp(xFraction, v010, v110);
                        double i4 = lerp(xFraction, v011, v111);

                        j1 = lerp(zFraction, i1, i2);
                        j2 = lerp(zFraction, i3, i4);

                        lastGridY = gridY;
                    }

                    double delta = lerp(yFraction, j1, j2);
                    if (delta != 0.0) {
                        outputBuffer.addTo(index, (float) delta);
                    }
                    index++;
                }
            }
        }
    }
}
