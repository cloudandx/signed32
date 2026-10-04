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

    // === 設定選項 ===
    public boolean cameraJitter = true;      // 攝影機視角抖動
    public boolean entityJitter = true;      // 生物與實體渲染吸附抖動
    public boolean blockTearing = true;      // 紅石粉與方塊模型幾何撕裂
    public int jitterThreshold = 12550821;   // 觸發起始座標（預設 Beta 遠方之地距離）

    /**
     * 從 signed32.json 讀取設定，若檔案不存在則自動建立預設值
     */
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

    /**
     * 儲存設定至 signed32.json
     */
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
