package com.aether.boost.engine;

import com.aether.boost.AetherBoostMod;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TuningApplier {
    public static void apply(String renderer) {
        AetherBoostMod.LOGGER.info("Applying settings for renderer: {}", renderer);
        File config = new File(System.getProperty("user.dir"), "options.txt");
        try (FileWriter fw = new FileWriter(config, true)) {
            fw.write("\nrenderer:" + renderer + "\n");
        } catch (IOException e) {
            AetherBoostMod.LOGGER.error("Failed to apply settings", e);
        }
    }

    public static void reset() {
        AetherBoostMod.LOGGER.info("Resetting all settings to default");
    }
}
