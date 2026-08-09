package com.aether.boost.engine;

import com.aether.boost.AetherBoostMod;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RendererDetector {
    private static final String[] RENDERER_NAMES = {"GL4ES", "Zink", "ANGLE", "Vulkan"};
    private static final String[][] SEARCH_PATHS = {
        // Лаунчеры
        {"/storage/emulated/0/Android/data/io.github.fold.launcher/files", "/sdcard/Android/data/io.github.fold.launcher/files"},
        {"/storage/emulated/0/Android/data/net.kdt.pojavlaunch/files", "/sdcard/Android/data/net.kdt.pojavlaunch/files"},
        {"/storage/emulated/0/Android/data/com.movtery.zalithlauncher/files", "/sdcard/Android/data/com.movtery.zalithlauncher/files"},
    };

    public static List<String> detectAllRenderers() {
        List<String> found = new ArrayList<>();
        for (String name : RENDERER_NAMES) {
            for (String[] paths : SEARCH_PATHS) {
                for (String p : paths) {
                    if (scanForRenderer(new File(p), name.toLowerCase())) {
                        if (!found.contains(name)) found.add(name);
                    }
                }
            }
        }
        // Проверка переменных окружения
        String env = System.getenv("POJAV_RENDERER");
        if (env != null && !found.contains(env)) found.add(env);
        return found.isEmpty() ? List.of("GL4ES") : found;
    }

    private static boolean scanForRenderer(File dir, String name) {
        if (!dir.exists()) return false;
        File[] files = dir.listFiles();
        if (files == null) return false;
        for (File f : files) {
            if (f.getName().toLowerCase().contains(name)) return true;
            if (f.isDirectory() && scanForRenderer(f, name)) return true;
        }
        return false;
    }
}
