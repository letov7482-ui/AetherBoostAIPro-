package com.aether.boost.engine;

import com.aether.boost.AetherBoostMod;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TuningApplier {
    public static void apply(String renderer, boolean isPC) {
        AetherBoostMod.LOGGER.info("Применяю настройки для рендера: {} (PC: {})", renderer, isPC);
        File config = new File(System.getProperty("user.dir"), "options.txt");
        try (FileWriter fw = new FileWriter(config, true)) {
            fw.write("\nrenderer:" + renderer + "\n");
            fw.write("pc_mode:" + isPC + "\n");
        } catch (IOException e) {
            AetherBoostMod.LOGGER.error("Ошибка применения", e);
        }
    }

    public static void reset() {
        AetherBoostMod.LOGGER.info("Сброс всех настроек");
    }
}
