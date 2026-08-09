package com.aether.boost.engine;

import com.aether.boost.AetherBoostMod;
import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public class TuningApplier {
    private static final Path CONFIG_DIR = Paths.get(System.getProperty("user.dir"), "config");
    private static final Path BACKUP_DIR = CONFIG_DIR.resolve("aetherboost_backup");

    public static void apply(String renderer, boolean isPC) {
        AetherBoostMod.LOGGER.info("Применяю полную оптимизацию (рендер: {}, PC: {})", renderer, isPC);
        backupCurrentConfigs();
        applyRendererOnly(renderer);
        applySodiumConfig(isPC);
        applyIrisConfig(isPC);
        applyMinecraftConfig(isPC);
        AetherBoostMod.LOGGER.info("Все настройки применены. Перезапустите игру.");
    }

    public static void applyRendererOnly(String renderer) {
        AetherBoostMod.LOGGER.info("Переключаю рендер на: {}", renderer);
        String[] configPaths = {
            "/storage/emulated/0/Android/data/io.github.fold.launcher/files/config.json",
            "/storage/emulated/0/Android/data/net.kdt.pojavlaunch/files/config.json",
            "/storage/emulated/0/Android/data/com.movtery.zalithlauncher/files/config.json"
        };
        for (String path : configPaths) {
            File configFile = new File(path);
            if (configFile.exists()) {
                try {
                    String content = Files.readString(configFile.toPath());
                    content = content.replaceAll("\"renderer\"\\s*:\\s*\"[^\"]*\"", "\"renderer\": \"" + renderer + "\"");
                    Files.writeString(configFile.toPath(), content);
                    AetherBoostMod.LOGGER.info("Конфиг лаунчера обновлён: {}", path);
                    return;
                } catch (IOException e) {
                    AetherBoostMod.LOGGER.error("Не удалось обновить конфиг лаунчера", e);
                }
            }
        }
        System.setProperty("pojav.renderer", renderer);
    }

    private static void applySodiumConfig(boolean isPC) {
        File sodiumConfig = CONFIG_DIR.resolve("sodium-options.json").toFile();
        if (!sodiumConfig.exists()) { AetherBoostMod.LOGGER.info("Sodium не установлен, пропускаю"); return; }
        try {
            String content = Files.readString(sodiumConfig.toPath());
            if (isPC) {
                content = content.replaceAll("\"quality\"\\s*:\\s*\\{[^}]*\\}", "\"quality\": {\"weather_quality\": \"FAST\", \"leaves_quality\": \"FAST\", \"enable_vignette\": false}");
                content = content.replaceAll("\"performance\"\\s*:\\s*\\{[^}]*\\}", "\"performance\": {\"chunk_builder_threads\": 0, \"always_defer_chunk_updates\": true, \"animate_only_visible_textures\": true, \"use_entity_culling\": true, \"use_particle_culling\": true, \"use_fog_occlusion\": true, \"use_block_face_culling\": true, \"use_compact_vertex_format\": true, \"use_translucent_face_sorting\": false}");
            } else {
                content = content.replaceAll("\"quality\"\\s*:\\s*\\{[^}]*\\}", "\"quality\": {\"weather_quality\": \"FAST\", \"leaves_quality\": \"FAST\", \"enable_vignette\": false}");
                content = content.replaceAll("\"performance\"\\s*:\\s*\\{[^}]*\\}", "\"performance\": {\"chunk_builder_threads\": 0, \"always_defer_chunk_updates\": true, \"animate_only_visible_textures\": false, \"use_entity_culling\": true, \"use_particle_culling\": true, \"use_fog_occlusion\": true, \"use_block_face_culling\": true, \"use_compact_vertex_format\": true, \"use_translucent_face_sorting\": false}");
            }
            Files.writeString(sodiumConfig.toPath(), content);
            AetherBoostMod.LOGGER.info("Конфиг Sodium обновлён");
        } catch (IOException e) {
            AetherBoostMod.LOGGER.error("Ошибка обновления Sodium", e);
        }
    }

    private static void applyIrisConfig(boolean isPC) {
        File irisConfig = CONFIG_DIR.resolve("iris.properties").toFile();
        if (!irisConfig.exists()) { AetherBoostMod.LOGGER.info("Iris не установлен, пропускаю"); return; }
        try {
            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(irisConfig)) { props.load(fis); }
            props.setProperty("maxShadowRenderDistance", isPC ? "8" : "4");
            props.setProperty("enableParticles", isPC ? "true" : "false");
            try (FileOutputStream fos = new FileOutputStream(irisConfig)) { props.store(fos, "Optimized by AetherBoost AI Pro"); }
            AetherBoostMod.LOGGER.info("Конфиг Iris обновлён");
        } catch (IOException e) {
            AetherBoostMod.LOGGER.error("Ошибка обновления Iris", e);
        }
    }

    private static void applyMinecraftConfig(boolean isPC) {
        File optionsFile = new File(System.getProperty("user.dir"), "options.txt");
        if (!optionsFile.exists()) return;
        try {
            String content = Files.readString(optionsFile.toPath());
            content = content.replaceAll("renderDistance:\\d+", "renderDistance:" + (isPC ? "12" : "8"));
            content = content.replaceAll("graphicsMode:\\w+", "graphicsMode:fast");
            content = content.replaceAll("ao:\\w+", "ao:false");
            content = content.replaceAll("enableVsync:\\w+", "enableVsync:false");
            Files.writeString(optionsFile.toPath(), content);
            AetherBoostMod.LOGGER.info("Конфиг Minecraft обновлён");
        } catch (IOException e) {
            AetherBoostMod.LOGGER.error("Ошибка обновления Minecraft", e);
        }
    }

    private static void backupCurrentConfigs() {
        try {
            Files.createDirectories(BACKUP_DIR);
            File[] configFiles = CONFIG_DIR.toFile().listFiles((dir, name) ->
                name.equals("sodium-options.json") || name.equals("iris.properties"));
            if (configFiles != null) {
                for (File file : configFiles) {
                    Files.copy(file.toPath(), BACKUP_DIR.resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);
                }
            }
            File optionsFile = new File(System.getProperty("user.dir"), "options.txt");
            if (optionsFile.exists()) {
                Files.copy(optionsFile.toPath(), BACKUP_DIR.resolve("options.txt"), StandardCopyOption.REPLACE_EXISTING);
            }
            AetherBoostMod.LOGGER.info("Бекап конфигов создан");
        } catch (IOException e) {
            AetherBoostMod.LOGGER.error("Ошибка создания бекапа", e);
        }
    }

    public static void reset() {
        AetherBoostMod.LOGGER.info("Сброс всех настроек из бекапа...");
        try {
            File sodiumBackup = BACKUP_DIR.resolve("sodium-options.json").toFile();
            if (sodiumBackup.exists()) {
                Files.copy(sodiumBackup.toPath(), CONFIG_DIR.resolve("sodium-options.json"), StandardCopyOption.REPLACE_EXISTING);
            }
            File irisBackup = BACKUP_DIR.resolve("iris.properties").toFile();
            if (irisBackup.exists()) {
                Files.copy(irisBackup.toPath(), CONFIG_DIR.resolve("iris.properties"), StandardCopyOption.REPLACE_EXISTING);
            }
            File optionsBackup = BACKUP_DIR.resolve("options.txt").toFile();
            if (optionsBackup.exists()) {
                Files.copy(optionsBackup.toPath(), Paths.get(System.getProperty("user.dir"), "options.txt"), StandardCopyOption.REPLACE_EXISTING);
            }
            System.setProperty("pojav.renderer", "");
            AetherBoostMod.LOGGER.info("Настройки сброшены до исходных");
        } catch (IOException e) {
            AetherBoostMod.LOGGER.error("Ошибка сброса настроек", e);
        }
    }
                              }
