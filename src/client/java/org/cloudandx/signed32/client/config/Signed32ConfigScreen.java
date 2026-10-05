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

        // =============================================================
        // 分類 1：世界 (World)
        // =============================================================
        ConfigCategory worldCategory = builder.getOrCreateCategory(
                Component.translatable("text.config.signed32.category.world"));

        worldCategory.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("text.config.signed32.option.expandWorldBorder"),
                        config.expandWorldBorder)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("text.config.signed32.option.expandWorldBorder.tooltip"))
                .setSaveConsumer(val -> config.expandWorldBorder = val)
                .build());

        worldCategory.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("text.config.signed32.option.farLandsNoise"),
                        config.farLandsNoise)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("text.config.signed32.option.farLandsNoise.tooltip"))
                .setSaveConsumer(val -> config.farLandsNoise = val)
                .build());

        worldCategory.addEntry(entryBuilder.startIntField(
                        Component.translatable("text.config.signed32.option.farLandsThreshold"),
                        config.farLandsThreshold)
                .setDefaultValue(12550821)
                .setMin(0)
                .setMax(Integer.MAX_VALUE)
                .setTooltip(Component.translatable("text.config.signed32.option.farLandsThreshold.tooltip"))
                .setSaveConsumer(val -> config.farLandsThreshold = val)
                .build());

        worldCategory.addEntry(entryBuilder.startIntField(
                        Component.translatable("text.config.signed32.option.fartherLandsThreshold"),
                        config.fartherLandsThreshold)
                .setDefaultValue(1004065920)
                .setMin(0)
                .setMax(Integer.MAX_VALUE)
                .setTooltip(Component.translatable("text.config.signed32.option.fartherLandsThreshold.tooltip"))
                .setSaveConsumer(val -> config.fartherLandsThreshold = val)
                .build());


        // =============================================================
        // 分類 2：修復 (Fixes)
        // =============================================================
        ConfigCategory fixesCategory = builder.getOrCreateCategory(
                Component.translatable("text.config.signed32.category.fixes"));

        fixesCategory.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("text.config.signed32.option.fixChunkOverflow"),
                        config.fixChunkOverflow)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("text.config.signed32.option.fixChunkOverflow.tooltip"))
                .setSaveConsumer(val -> config.fixChunkOverflow = val)
                .build());

        fixesCategory.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("text.config.signed32.option.expandBlockPos"),
                        config.expandBlockPos)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("text.config.signed32.option.expandBlockPos.tooltip"))
                .setSaveConsumer(val -> config.expandBlockPos = val)
                .build());

        fixesCategory.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("text.config.signed32.option.expandSectionPos"),
                        config.expandSectionPos)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("text.config.signed32.option.expandSectionPos.tooltip"))
                .setSaveConsumer(val -> config.expandSectionPos = val)
                .build());

        fixesCategory.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("text.config.signed32.option.expandEntitySections"),
                        config.expandEntitySections)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("text.config.signed32.option.expandEntitySections.tooltip"))
                .setSaveConsumer(val -> config.expandEntitySections = val)
                .build());

        fixesCategory.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("text.config.signed32.option.extendedBlockPosProtocol"),
                        config.extendedBlockPosProtocol)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("text.config.signed32.option.extendedBlockPosProtocol.tooltip"))
                .setSaveConsumer(val -> config.extendedBlockPosProtocol = val)
                .build());


        // =============================================================
        // 分類 3：視覺 (Visuals)
        // =============================================================
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