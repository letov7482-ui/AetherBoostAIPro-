package com.aether.boost.engine;

import com.aether.boost.AetherBoostMod;
import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public class TuningApplier {
    private static final Path CONFIG_DIR = Paths.get(System.getProperty("user.dir"), "config");
    private static final Path BACKUP_DIR = CONFIG_DIR.resolve("aetherboost_backup");
    public static final String OPTIMIZATION_KEY = "aetherboost.optimizations.enabled";

    public static boolean areOptimizationsEnabled() {
        return Boolean.parseBoolean(System.getProperty(OPTIMIZATION_KEY, "false"));
    }

    // ========== ГЛАВНЫЙ МЕТОД ==========
    public static void apply(String renderer, boolean isPC) {
        AetherBoostMod.LOGGER.info("Применяю полную оптимизацию...");
        backupCurrentConfigs();
        applyRendererOnly(renderer);
        applySodiumConfig(isPC);
        applyIrisConfig(isPC);
        applyMinecraftConfig(isPC);
        applyJVMArgs(isPC);
        applyLauncherConfig(isPC);
        System.setProperty(OPTIMIZATION_KEY, "true");
        AetherBoostMod.LOGGER.info("Все оптимизации применены. Перезапустите игру!");
    }

    // ========== РЕНДЕР ==========
    public static void applyRendererOnly(String renderer) {
        AetherBoostMod.LOGGER.info("Переключаю рендер на: {}", renderer);
        String[] paths = {
            "/storage/emulated/0/Android/data/io.github.fold.launcher/files/config.json",
            "/storage/emulated/0/Android/data/net.kdt.pojavlaunch/files/config.json",
            "/storage/emulated/0/Android/data/com.movtery.zalithlauncher/files/config.json"
        };
        for (String p : paths) {
            File f = new File(p);
            if (f.exists()) {
                try {
                    String c = Files.readString(f.toPath());
                    c = c.replaceAll("\"renderer\"\\s*:\\s*\"[^\"]*\"", "\"renderer\": \"" + renderer + "\"");
                    Files.writeString(f.toPath(), c);
                    AetherBoostMod.LOGGER.info("Обновлён: {}", p);
                    return;
                } catch (IOException e) {
                    AetherBoostMod.LOGGER.error("Ошибка обновления конфига", e);
                }
            }
        }
        System.setProperty("pojav.renderer", renderer);
    }

    // ========== SODIUM ==========
    private static void applySodiumConfig(boolean isPC) {
        File f = CONFIG_DIR.resolve("sodium-options.json").toFile();
        if (!f.exists()) return;
        try {
            String c = Files.readString(f.toPath());
            String perf = isPC
                ? "\"performance\": {\"chunk_builder_threads\": 0, \"always_defer_chunk_updates\": true, \"animate_only_visible_textures\": true, \"use_entity_culling\": true, \"use_particle_culling\": true, \"use_fog_occlusion\": true, \"use_block_face_culling\": true, \"use_compact_vertex_format\": true}"
                : "\"performance\": {\"chunk_builder_threads\": 0, \"always_defer_chunk_updates\": true, \"animate_only_visible_textures\": false, \"use_entity_culling\": true, \"use_particle_culling\": true, \"use_fog_occlusion\": true, \"use_block_face_culling\": true, \"use_compact_vertex_format\": true}";
            c = c.replaceAll("\"quality\"\\s*:\\s*\\{[^}]*\\}", "\"quality\": {\"weather_quality\": \"FAST\", \"leaves_quality\": \"FAST\", \"enable_vignette\": false}");
            c = c.replaceAll("\"performance\"\\s*:\\s*\\{[^}]*\\}", perf);
            Files.writeString(f.toPath(), c);
            AetherBoostMod.LOGGER.info("Sodium обновлён");
        } catch (IOException e) {
            AetherBoostMod.LOGGER.error("Ошибка Sodium", e);
        }
    }

    // ========== IRIS ==========
    private static void applyIrisConfig(boolean isPC) {
        File f = CONFIG_DIR.resolve("iris.properties").toFile();
        if (!f.exists()) return;
        try {
            Properties p = new Properties();
            try (FileInputStream fis = new FileInputStream(f)) { p.load(fis); }
            p.setProperty("maxShadowRenderDistance", isPC ? "8" : "4");
            p.setProperty("enableParticles", isPC ? "true" : "false");
            p.setProperty("enableClouds", "false");
            try (FileOutputStream fos = new FileOutputStream(f)) { p.store(fos, "AetherBoost"); }
            AetherBoostMod.LOGGER.info("Iris обновлён");
        } catch (IOException e) {
            AetherBoostMod.LOGGER.error("Ошибка Iris", e);
        }
    }

    // ========== OPTIONS.TXT ==========
    private static void applyMinecraftConfig(boolean isPC) {
        File f = new File(System.getProperty("user.dir"), "options.txt");
        if (!f.exists()) return;
        try {
            String c = Files.readString(f.toPath());
            c = c.replaceAll("renderDistance:\\d+", "renderDistance:" + (isPC ? "10" : "6"));
            c = c.replaceAll("graphicsMode:\\w+", "graphicsMode:fast");
            c = c.replaceAll("ao:\\w+", "ao:false");
            c = c.replaceAll("enableVsync:\\w+", "enableVsync:false");
            c = c.replaceAll("maxFps:\\d+", "maxFps:120");
            c = c.replaceAll("enableClouds:\\w+", "enableClouds:false");
            Files.writeString(f.toPath(), c);
            AetherBoostMod.LOGGER.info("options.txt обновлён");
        } catch (IOException e) {
            AetherBoostMod.LOGGER.error("Ошибка options.txt", e);
        }
    }

    // ========== JVM-АРГУМЕНТЫ ==========
    private static void applyJVMArgs(boolean isPC) {
        String args = "-XX:+UseZGC -XX:+DisableExplicitGC -Djava.awt.headless=true";
        if (!isPC) {
            args += " -Xms256M -Xmx" + Math.min(SystemScanner.getTotalRAM() / 2, 2048) + "M";
        }
        File jvmFile = new File(System.getProperty("user.dir"), "aetherboost_jvm_args.txt");
        try {
            Files.writeString(jvmFile.toPath(), args);
            AetherBoostMod.LOGGER.info("JVM-аргументы сохранены в {}", jvmFile);
        } catch (IOException e) {
            AetherBoostMod.LOGGER.error("Ошибка сохранения JVM", e);
        }
    }

    // ========== НАСТРОЙКИ ЛАУНЧЕРА ==========
    private static void applyLauncherConfig(boolean isPC) {
        String[] paths = {
            "/storage/emulated/0/Android/data/io.github.fold.launcher/files/config.json",
            "/storage/emulated/0/Android/data/net.kdt.pojavlaunch/files/config.json",
            "/storage/emulated/0/Android/data/com.movtery.zalithlauncher/files/config.json"
        };
        for (String p : paths) {
            File f = new File(p);
            if (f.exists()) {
                try {
                    String c = Files.readString(f.toPath());
                    c = c.replaceAll("\"resolution\"\\s*:\\s*\\d+", "\"resolution\": 70");
                    c = c.replaceAll("\"forceVsync\"\\s*:\\s*\\w+", "\"forceVsync\": false");
                    Files.writeString(f.toPath(), c);
                    AetherBoostMod.LOGGER.info("Лаунчер обновлён: {}", p);
                    return;
                } catch (IOException e) {
                    AetherBoostMod.LOGGER.error("Ошибка лаунчера", e);
                }
            }
        }
    }

    // ========== ПРЕСЕТЫ ==========
    public static void applyPvPConfig(boolean isPC) {
        applyMinecraftConfig(isPC);
        AetherBoostMod.LOGGER.info("PvP-пресет применён");
    }

    public static void applyBalancedConfig(boolean isPC) {
        File f = new File(System.getProperty("user.dir"), "options.txt");
        if (!f.exists()) return;
        try {
            String c = Files.readString(f.toPath());
            c = c.replaceAll("renderDistance:\\d+", "renderDistance:" + (isPC ? "12" : "8"));
            c = c.replaceAll("graphicsMode:\\w+", "graphicsMode:fancy");
            c = c.replaceAll("ao:\\w+", "ao:true");
            c = c.replaceAll("enableClouds:\\w+", "enableClouds:true");
            Files.writeString(f.toPath(), c);
            AetherBoostMod.LOGGER.info("Balanced-пресет применён");
        } catch (IOException e) {
            AetherBoostMod.LOGGER.error("Ошибка Balanced", e);
        }
    }

    public static void applyMaxFPSConfig(boolean isPC) {
        File f = new File(System.getProperty("user.dir"), "options.txt");
        if (!f.exists()) return;
        try {
            String c = Files.readString(f.toPath());
            c = c.replaceAll("masterVolume:\\d+\\.\\d+", "masterVolume:0.0");
            c = c.replaceAll("renderDistance:\\d+", "renderDistance:" + (isPC ? "6" : "4"));
            c = c.replaceAll("graphicsMode:\\w+", "graphicsMode:fast");
            c = c.replaceAll("ao:\\w+", "ao:false");
            c = c.replaceAll("enableVsync:\\w+", "enableVsync:false");
            c = c.replaceAll("enableClouds:\\w+", "enableClouds:false");
            Files.writeString(f.toPath(), c);
            AetherBoostMod.LOGGER.info("Max FPS-пресет применён");
        } catch (IOException e) {
            AetherBoostMod.LOGGER.error("Ошибка Max FPS", e);
        }
    }

    // ========== БЕКАП ==========
    private static void backupCurrentConfigs() {
        try {
            Files.createDirectories(BACKUP_DIR);
            for (String name : new String[]{"sodium-options.json", "iris.properties"}) {
                File f = CONFIG_DIR.resolve(name).toFile();
                if (f.exists()) Files.copy(f.toPath(), BACKUP_DIR.resolve(name), StandardCopyOption.REPLACE_EXISTING);
            }
            File opt = new File(System.getProperty("user.dir"), "options.txt");
            if (opt.exists()) Files.copy(opt.toPath(), BACKUP_DIR.resolve("options.txt"), StandardCopyOption.REPLACE_EXISTING);
            AetherBoostMod.LOGGER.info("Бекап создан");
        } catch (IOException e) {
            AetherBoostMod.LOGGER.error("Ошибка бекапа", e);
        }
    }

    // ========== СБРОС ==========
    public static void reset() {
        AetherBoostMod.LOGGER.info("Сброс...");
        try {
            for (String name : new String[]{"sodium-options.json", "iris.properties"}) {
                File backup = BACKUP_DIR.resolve(name).toFile();
                if (backup.exists()) Files.copy(backup.toPath(), CONFIG_DIR.resolve(name), StandardCopyOption.REPLACE_EXISTING);
            }
            File optBackup = BACKUP_DIR.resolve("options.txt").toFile();
            if (optBackup.exists()) Files.copy(optBackup.toPath(), Paths.get(System.getProperty("user.dir"), "options.txt"), StandardCopyOption.REPLACE_EXISTING);
            System.setProperty("pojav.renderer", "");
            System.setProperty(OPTIMIZATION_KEY, "false");
            AetherBoostMod.LOGGER.info("Сброшено");
        } catch (IOException e) {
            AetherBoostMod.LOGGER.error("Ошибка сброса", e);
        }
    }
                 }
