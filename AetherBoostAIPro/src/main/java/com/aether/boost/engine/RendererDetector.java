package com.aether.boost.engine;

import com.aether.boost.AetherBoostMod;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RendererDetector {

    // Полный список известных рендеров для Pojav/FCL/Zalith/ПК
    private static final String[] ALL_KNOWN_RENDERERS = {
        // Основные OpenGL ES обёртки (мобильные)
        "GL4ES",
        "Zink",
        "ANGLE",
        "Vulkan",
        "VirGL",
        "MobileGLUES",
        "LTW",
        "HolyGL4ES",
        "FasterGL4ES",
        "TinyGL4ES",
        // Рендеры для ПК
        "OpenGL",
        "DirectX",
        "Software",
        // Дополнительные (могут встречаться в кастомных сборках)
        "Zink-Legacy",
        "ANGLE-D3D",
        "ANGLE-Metal",
        "Vulkan-Zink",
        "NativeGL"
    };

    // Возможные корневые пути лаунчеров
    private static final String[][] LAUNCHER_ROOTS = {
        {"/storage/emulated/0/Android/data/io.github.fold.launcher/files", "/sdcard/Android/data/io.github.fold.launcher/files"},
        {"/storage/emulated/0/Android/data/net.kdt.pojavlaunch/files", "/sdcard/Android/data/net.kdt.pojavlaunch/files"},
        {"/storage/emulated/0/Android/data/com.movtery.zalithlauncher/files", "/sdcard/Android/data/com.movtery.zalithlauncher/files"},
        // ПК-пути (Windows/Linux/Mac)
        {System.getProperty("user.home") + "/.minecraft"},
        {System.getProperty("user.home") + "/AppData/Roaming/.minecraft"}
    };

    public static List<String> detectAllRenderers() {
        Set<String> found = new LinkedHashSet<>();

        // 1. Сканируем файловую систему
        for (String[] paths : LAUNCHER_ROOTS) {
            for (String path : paths) {
                File root = new File(path);
                if (root.exists()) {
                    scanDirectory(root, found, 0);
                }
            }
        }

        // 2. Проверяем переменные окружения
        String envRenderer = System.getenv("POJAV_RENDERER");
        if (envRenderer != null && !envRenderer.isEmpty()) {
            found.add(envRenderer);
            AetherBoostMod.LOGGER.info("Найден рендер через POJAV_RENDERER: {}", envRenderer);
        }

        String libglEnv = System.getenv("LIBGL_ES");
        if (libglEnv != null && !libglEnv.isEmpty()) {
            found.add("GL4ES (env)");
        }

        // 3. Проверяем системные свойства
        String mesaVersion = System.getenv("MESA_GL_VERSION_OVERRIDE");
        if (mesaVersion != null && !mesaVersion.isEmpty()) {
            found.add("VirGL (MESA)");
        }

        // 4. Если ничего не нашли, добавляем стандартный GL4ES
        if (found.isEmpty()) {
            found.add("GL4ES");
            AetherBoostMod.LOGGER.warn("Рендеры не найдены, используется GL4ES по умолчанию");
        }

        AetherBoostMod.LOGGER.info("Обнаружено рендеров: {} — {}", found.size(), found);
        return new ArrayList<>(found);
    }

    private static void scanDirectory(File dir, Set<String> found, int depth) {
        if (depth > 4 || !dir.exists() || !dir.isDirectory()) return;

        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            String name = file.getName().toLowerCase();

            // Проверяем имя файла/папки на совпадение с известными рендерами
            for (String renderer : ALL_KNOWN_RENDERERS) {
                if (name.contains(renderer.toLowerCase())) {
                    found.add(renderer);
                }
            }

            // Специальные проверки
            if (name.contains("renderer") || name.contains("gl") || name.contains("es")) {
                // Возможно, это папка с рендерами
                if (file.isDirectory()) {
                    scanDirectory(file, found, depth + 1);
                }
            }

            // Рекурсивный поиск
            if (file.isDirectory() && depth < 3) {
                scanDirectory(file, found, depth + 1);
            }
        }
    }
}
