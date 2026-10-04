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

    // 緩存氣候採樣器，避免每幀重複建立造成 GC 停頓
    @Unique
    private Climate.@Nullable Sampler signed32$cachedClimateSampler;
    @Unique
    private @Nullable RandomState signed32$cachedRandomState;

    @Inject(method = "extractLines", at = @At("HEAD"))
    private void onExtractLines(GuiGraphicsExtractor graphics, List<String> lines, boolean alignLeft, int scaledScreenWidth, CallbackInfo ci) {
        if (!alignLeft || lines == null) {
            return;
        }

        Entity camera = this.minecraft.getCameraEntity();
        if (camera == null) {
            return;
        }

        // =========================================================================
        // 1. Current precision: 顯示浮點數精度 (支援單精度接管模式與動態顏色)
        // =========================================================================
        double maxCoord = Math.max(Math.abs(camera.getX()), Math.abs(camera.getZ()));
        double doublePrec = Math.ulp(maxCoord);
        float floatPrec = Math.ulp((float) maxCoord);

        boolean isSinglePrecisionActive = Signed32Config.INSTANCE.cameraJitter
                && maxCoord >= Signed32Config.INSTANCE.jitterThreshold;

        String floatColor = signed32$getFloatPrecisionColor(floatPrec);
        String doubleColor = "§a"; // double 在 21 億內始終保持在 10^-9 級別，保持綠色

        String precisionLine;
        if (isSinglePrecisionActive) {
            // 單精度生效中：float 成為主角，並顯示 [32-bit Float] 標籤
            precisionLine = "Current precision: " + floatColor + floatPrec + "§r §e[32-bit Float]\u00a7r (native double: " + doubleColor + doublePrec + "\u00a7r)";
        } else {
            // 正常雙精度模式
            precisionLine = "Current precision: " + doubleColor + doublePrec + "§r (float: " + floatColor + floatPrec + "\u00a7r)";
        }

        // 插入在 "Facing:" 這一行的正下方
        int facingIndex = -1;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line != null && line.startsWith("Facing:")) {
                facingIndex = i;
                break;
            }
        }
        if (facingIndex != -1) {
            lines.add(facingIndex + 1, precisionLine);
        } else {
            lines.add(precisionLine);
        }

        // =========================================================================
        // 2. NoiseRouter: 提取氣候採樣器 (T, V, C, E, D, W, PV)
        // =========================================================================
        String noiseRouterLine = signed32$formatNoiseRouterLine(camera);
        if (noiseRouterLine != null) {
            // 插入在 "Local Difficulty:" 這一行的正下方
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
                lines.add(noiseRouterLine);
            }
        }
    }

    @Unique
    private @Nullable String signed32$formatNoiseRouterLine(Entity camera) {
        ServerLevel serverLevel = this.getServerLevel();

        if (serverLevel != null && serverLevel.getChunkSource().getGenerator() instanceof NoiseBasedChunkGenerator) {
            try {
                RandomState randomState = serverLevel.getChunkSource().randomState();
                
                // 若 RandomState 改變則更新採樣器，否則重用緩存
                if (this.signed32$cachedRandomState != randomState || this.signed32$cachedClimateSampler == null) {
                    this.signed32$cachedRandomState = randomState;
                    this.signed32$cachedClimateSampler = randomState.createClimateSampler(SamplerContext.EMPTY_UNCACHED);
                }

                Climate.Sampler sampler = this.signed32$cachedClimateSampler;
                BlockPos pos = camera.blockPosition();
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();

                // 26.3 正確 API：直接從 Bound 取出原生的 float 數值
                float t = sampler.temperature().sampleValue(x, y, z);
                float v = sampler.humidity().sampleValue(x, y, z);       // V: Vegetation (對應 Climate 中的 humidity)
                float c = sampler.continentalness().sampleValue(x, y, z); // C: Continents (對應 Climate 中的 continentalness)
                float e = sampler.erosion().sampleValue(x, y, z);        // E: Erosion
                float d = sampler.depth().sampleValue(x, y, z);          // D: Depth
                float w = sampler.weirdness().sampleValue(x, y, z);      // W: Weirdness / Ridges

                // 計算山脊折疊峰谷 (Peaks & Valleys)
                float pv = -(Math.abs(Math.abs(w) - 0.6666667F) - 0.33333334F) * 3.0F;

                return String.format(
                        Locale.ROOT,
                        "NoiseRouter T: %.3f V: %.3f C: %.3f E: %.3f D: %.3f W: %.3f PV: %.3f",
                        t, v, c, e, d, w, pv
                );
            } catch (Throwable ignored) {
            }
        } else if (serverLevel == null) {
            return "NoiseRouter: §7(Multiplayer: Managed by server)§r";
        }
        return null;
    }

    @Unique
    private static String signed32$getFloatPrecisionColor(float floatPrec) {
        if (floatPrec < 0.001F) {
            return "§a"; // 綠色：微米級誤差，完全平滑
        } else if (floatPrec < 0.05F) {
            return "§e"; // 黃色：像素級微幅抖動
        } else if (floatPrec < 0.5F) {
            return "§6"; // 橘色：顯著階梯式抖動
        } else if (floatPrec < 1.0F) {
            return "§c"; // 紅色：模型嚴重撕裂
        } else {
            return "§c"; // 暗紅粗體：精度徹底崩潰 (>= 1.0 方塊跳步)
        }
    }
}
