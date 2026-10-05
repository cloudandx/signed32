package org.cloudandx.signed32.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class Signed32Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("signed32.json");

    public static Signed32Config INSTANCE = new Signed32Config();

    // =============================================================
    // 分類 1：世界與邊界生成 (World & Generation)
    // =============================================================
    public boolean expandWorldBorder = true;
    public boolean farLandsNoise = true;
    public int farLandsThreshold = 12550821;              // 邊境之地門檻 (預設 1255 萬)
    public int fartherLandsThreshold = 1004065920;        // 遙遠之地門檻 (預設 10.04 億)

    // =============================================================
    // 分類 2：系統修復與相容性 (Fixes & Compatibility)
    // =============================================================
    public boolean fixChunkOverflow = true;
    public boolean expandBlockPos = true;
    public boolean expandSectionPos = true;
    public boolean expandEntitySections = true;
    public boolean extendedBlockPosProtocol = false;

    // =============================================================
    // 分類 3：遠方視覺特性 (Visual Artifacts)
    // =============================================================
    public boolean cameraJitter = false;
    public boolean entityJitter = false;
    public boolean blockTearing = false;
    public int jitterThreshold = 0;

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            save();
            return;
        }
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            INSTANCE = GSON.fromJson(reader, Signed32Config.class);
            if (INSTANCE == null) {
                INSTANCE = new Signed32Config();
            }
        } catch (Exception e) {
            e.printStackTrace();
            INSTANCE = new Signed32Config();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(INSTANCE, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}