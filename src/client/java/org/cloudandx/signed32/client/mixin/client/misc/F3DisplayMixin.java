package org.cloudandx.signed32.client.mixin.client.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import org.cloudandx.signed32.config.Signed32Config;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Locale;

@Mixin(DebugScreenOverlay.class)
public abstract class F3DisplayMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private @Nullable ServerLevel getServerLevel() {
        return null;
    }

    @Unique
    private Climate.@Nullable Sampler signed32$cachedClimateSampler;
    @Unique
    private @Nullable RandomState signed32$cachedRandomState;

    @Inject(method = "extractLines", at = @At("HEAD"))
    private void onExtractLines(GuiGraphicsExtractor graphics, List<String> lines, boolean alignLeft, int scaledScreenWidth, CallbackInfo ci) {
        // 核心防護 1：未開 F3 時 lines 為空，直接返回，絕不閃爍
        if (!alignLeft || lines == null || lines.isEmpty()) {
            return;
        }

        // 核心防護 2：防影分身機制！
        // 若 Sodium / ImmediatelyFast 在同一個畫格內多次抽取，發現已存在則直接退出
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line != null && line.startsWith("Current precision:")) {
                return;
            }
        }

        Entity camera = this.minecraft.getCameraEntity();
        if (camera == null) {
            return;
        }

        // =========================================================================
        // 1. Current precision: 顯示浮點數精度
        // =========================================================================
        double maxCoord = Math.max(Math.abs(camera.getX()), Math.abs(camera.getZ()));
        double doublePrec = Math.ulp(maxCoord);
        float floatPrec = Math.ulp((float) maxCoord);

        boolean isSinglePrecisionActive = Signed32Config.INSTANCE.cameraJitter
                && maxCoord >= Signed32Config.INSTANCE.jitterThreshold;

        String floatColor = signed32$getFloatPrecisionColor(floatPrec);
        String doubleColor = "§a";

        String precisionLine;
        if (isSinglePrecisionActive) {
            precisionLine = "Current precision: " + floatColor + floatPrec + "§r §e[32-bit Float]§r (native double: " + doubleColor + doublePrec + "§r)";
        } else {
            precisionLine = "Current precision: " + doubleColor + doublePrec + "§r (float: " + floatColor + floatPrec + "§r)";
        }

        // 尋找插入錨點
        int facingIndex = -1;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line != null && line.startsWith("Facing:")) {
                facingIndex = i;
                break;
            }
        }

        int precisionInsertIndex;
        if (facingIndex != -1) {
            precisionInsertIndex = facingIndex + 1;
            lines.add(precisionInsertIndex, precisionLine);
        } else {
            // 降級位置：插在第 2 行（FPS 下方）
            precisionInsertIndex = Math.min(2, lines.size());
            lines.add(precisionInsertIndex, precisionLine);
        }

        // =========================================================================
        // 2. NoiseRouter: 提取氣候採樣器
        // =========================================================================
        String noiseRouterLine = signed32$formatNoiseRouterLine(camera);
        if (noiseRouterLine != null) {
            int difficultyIndex = -1;
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line != null && line.startsWith("Local Difficulty:")) {
                    difficultyIndex = i;
                    break;
                }
            }

            if (difficultyIndex != -1) {
                lines.add(difficultyIndex + 1, noiseRouterLine);
            } else {
                lines.add(precisionInsertIndex + 1, noiseRouterLine);
            }
        }
    }

    @Unique
    private @Nullable String signed32$formatNoiseRouterLine(Entity camera) {
        ServerLevel serverLevel = this.getServerLevel();

        if (serverLevel != null && serverLevel.getChunkSource().getGenerator() instanceof NoiseBasedChunkGenerator) {
            try {
                RandomState randomState = serverLevel.getChunkSource().randomState();

                if (this.signed32$cachedRandomState != randomState || this.signed32$cachedClimateSampler == null) {
                    this.signed32$cachedRandomState = randomState;
                    this.signed32$cachedClimateSampler = randomState.createClimateSampler(SamplerContext.EMPTY_UNCACHED);
                }

                Climate.Sampler sampler = this.signed32$cachedClimateSampler;
                if (sampler == null) return null;

                BlockPos pos = camera.blockPosition();
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();

                float t = sampler.temperature().sampleValue(x, y, z);
                float v = sampler.humidity().sampleValue(x, y, z);
                float c = sampler.continentalness().sampleValue(x, y, z);
                float e = sampler.erosion().sampleValue(x, y, z);
                float d = sampler.depth().sampleValue(x, y, z);
                float w = sampler.weirdness().sampleValue(x, y, z);

                float pv = -(Math.abs(Math.abs(w) - 0.6666667F) - 0.33333334F) * 3.0F;

                return String.format(
                        Locale.ROOT,
                        "NoiseRouter T: %.3f V: %.3f C: %.3f E: %.3f D: %.3f W: %.3f PV: %.3f",
                        t, v, c, e, d, w, pv
                );
            } catch (Throwable ignored) {
                return null;
            }
        } else if (serverLevel == null) {
            return "NoiseRouter: §7(Multiplayer: Managed by server)§r";
        }
        return null;
    }

    @Unique
    private static String signed32$getFloatPrecisionColor(float floatPrec) {
        if (floatPrec < 0.001F) {
            return "§a";
        } else if (floatPrec < 0.05F) {
            return "§e";
        } else if (floatPrec < 0.5F) {
            return "§6";
        } else if (floatPrec < 1.0F) {
            return "§c";
        } else {
            return "§c";
        }
    }
}