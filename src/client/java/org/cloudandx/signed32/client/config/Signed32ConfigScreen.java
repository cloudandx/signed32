package org.cloudandx.signed32.client.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.cloudandx.signed32.config.Signed32Config;

public class Signed32ConfigScreen {

    public static Screen create(Screen parent) {
        Signed32Config config = Signed32Config.INSTANCE;

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("text.config.signed32.title"));

        builder.setSavingRunnable(Signed32Config::save);
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // -------------------------------------------------------------
        // 分類 1：世界與邊界生成 (World & Generation)
        // -------------------------------------------------------------
        ConfigCategory worldCategory = builder.getOrCreateCategory(
                Component.translatable("text.config.signed32.category.border"));

        worldCategory.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("text.config.signed32.option.expandWorldBorder"),
                        config.expandWorldBorder)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("text.config.signed32.option.expandWorldBorder.tooltip"))
                .setSaveConsumer(val -> config.expandWorldBorder = val)
                .build());

        worldCategory.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("text.config.signed32.option.fixChunkOverflow"),
                        config.fixChunkOverflow)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("text.config.signed32.option.fixChunkOverflow.tooltip"))
                .setSaveConsumer(val -> config.fixChunkOverflow = val)
                .build());

        worldCategory.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("text.config.signed32.option.farLandsNoise"),
                        config.farLandsNoise)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("text.config.signed32.option.farLandsNoise.tooltip"))
                .setSaveConsumer(val -> config.farLandsNoise = val)
                .build());

        // 1. 邊境之地生成距離
        worldCategory.addEntry(entryBuilder.startIntField(
                        Component.translatable("text.config.signed32.option.farLandsThreshold"),
                        config.farLandsThreshold)
                .setDefaultValue(12550821)
                .setMin(0)
                .setMax(Integer.MAX_VALUE)
                .setTooltip(Component.translatable("text.config.signed32.option.farLandsThreshold.tooltip"))
                .setSaveConsumer(val -> config.farLandsThreshold = val)
                .build());

        // 2. 遙遠之地生成距離
        worldCategory.addEntry(entryBuilder.startIntField(
                        Component.translatable("text.config.signed32.option.fartherLandsThreshold"),
                        config.fartherLandsThreshold)
                .setDefaultValue(1004065920)
                .setMin(0)
                .setMax(Integer.MAX_VALUE)
                .setTooltip(Component.translatable("text.config.signed32.option.fartherLandsThreshold.tooltip"))
                .setSaveConsumer(val -> config.fartherLandsThreshold = val)
                .build());

        // 下界邊境之地開關
        worldCategory.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("text.config.signed32.option.netherFarLands"),
                        config.netherFarLands)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("text.config.signed32.option.netherFarLands.tooltip"))
                .setSaveConsumer(val -> config.netherFarLands = val)
                .build());

        // 下界座標除以 8 換算開關
        worldCategory.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("text.config.signed32.option.netherCoordinateScaling"),
                        config.netherCoordinateScaling)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("text.config.signed32.option.netherCoordinateScaling.tooltip"))
                .setSaveConsumer(val -> config.netherCoordinateScaling = val)
                .build());

        // 終界邊境之地開關
        worldCategory.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("text.config.signed32.option.endFarLands"),
                        config.endFarLands)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("text.config.signed32.option.endFarLands.tooltip"))
                .setSaveConsumer(val -> config.endFarLands = val)
                .build());

        // 角部地貌增強開關
        worldCategory.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("text.config.signed32.option.cornerLandsEnhanced"),
                        config.cornerLandsEnhanced)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("text.config.signed32.option.cornerLandsEnhanced.tooltip"))
                .setSaveConsumer(val -> config.cornerLandsEnhanced = val)
                .build());


        // -------------------------------------------------------------
        // 分類 2：極限座標與儲存 (Coordinates & Storage)
        // -------------------------------------------------------------
        ConfigCategory posCategory = builder.getOrCreateCategory(
                Component.translatable("text.config.signed32.category.pos"));

        posCategory.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("text.config.signed32.option.expandBlockPos"),
                        config.expandBlockPos)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("text.config.signed32.option.expandBlockPos.tooltip"))
                .setSaveConsumer(val -> config.expandBlockPos = val)
                .build());

        posCategory.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("text.config.signed32.option.expandSectionPos"),
                        config.expandSectionPos)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("text.config.signed32.option.expandSectionPos.tooltip"))
                .setSaveConsumer(val -> config.expandSectionPos = val)
                .build());

        posCategory.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("text.config.signed32.option.expandEntitySections"),
                        config.expandEntitySections)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("text.config.signed32.option.expandEntitySections.tooltip"))
                .setSaveConsumer(val -> config.expandEntitySections = val)
                .build());

        // -------------------------------------------------------------
        // 分類 3：網路協定與相容性 (Network & Compatibility)
        // -------------------------------------------------------------
        ConfigCategory networkCategory = builder.getOrCreateCategory(
                Component.translatable("text.config.signed32.category.network"));

        networkCategory.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("text.config.signed32.option.extendedBlockPosProtocol"),
                        config.extendedBlockPosProtocol)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("text.config.signed32.option.extendedBlockPosProtocol.tooltip"))
                .setSaveConsumer(val -> config.extendedBlockPosProtocol = val)
                .build());

        // -------------------------------------------------------------
        // 分類 4：遠方之地視覺效果 (Visual Artifacts)
        // -------------------------------------------------------------
        ConfigCategory visualCategory = builder.getOrCreateCategory(
                Component.translatable("text.config.signed32.category.visual"));

        visualCategory.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("text.config.signed32.option.cameraJitter"),
                        config.cameraJitter)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("text.config.signed32.option.cameraJitter.tooltip"))
                .setSaveConsumer(val -> config.cameraJitter = val)
                .build());

        visualCategory.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("text.config.signed32.option.entityJitter"),
                        config.entityJitter)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("text.config.signed32.option.entityJitter.tooltip"))
                .setSaveConsumer(val -> config.entityJitter = val)
                .build());

        visualCategory.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("text.config.signed32.option.blockTearing"),
                        config.blockTearing)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("text.config.signed32.option.blockTearing.tooltip"))
                .setSaveConsumer(val -> config.blockTearing = val)
                .build());

        visualCategory.addEntry(entryBuilder.startIntField(
                        Component.translatable("text.config.signed32.option.jitterThreshold"),
                        config.jitterThreshold)
                .setDefaultValue(0)
                .setMin(0)
                .setMax(Integer.MAX_VALUE)
                .setTooltip(Component.translatable("text.config.signed32.option.jitterThreshold.tooltip"))
                .setSaveConsumer(val -> config.jitterThreshold = val)
                .build());

        return builder.build();
    }
}
