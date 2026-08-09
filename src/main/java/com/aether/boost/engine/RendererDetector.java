package com.aether.boost.engine;

import com.aether.boost.AetherBoostMod;
import java.io.*;
import java.util.*;

public class RendererDetector {

    // Все известные рендеры с приоритетом (чем выше, тем вероятнее лучший)
    private static final Map<String, Integer> RENDERER_PRIORITY = new LinkedHashMap<>();
    static {
        RENDERER_PRIORITY.put("Vulkan", 100);
        RENDERER_PRIORITY.put("Zink", 90);
        RENDERER_PRIORITY.put("LTW", 85);
        RENDERER_PRIORITY.put("ANGLE", 80);
        RENDERER_PRIORITY.put("ANGLE-D3D", 75);
        RENDERER_PRIORITY.put("ANGLE-Metal", 75);
        RENDERER_PRIORITY.put("MobileGLUES", 70);
        RENDERER_PRIORITY.put("HolyGL4ES", 65);
        RENDERER_PRIORITY.put("FasterGL4ES", 60);
        RENDERER_PRIORITY.put("VirGL", 55);
        RENDERER_PRIORITY.put("GL4ES", 50);
        RENDERER_PRIORITY.put("TinyGL4ES", 40);
        RENDERER_PRIORITY.put("OpenGL", 30);
        RENDERER_PRIORITY.put("Software", 10);
    }

    private static final String[] LAUNCHER_ROOTS = {
        "/storage/emulated/0/Android/data/io.github.fold.launcher/files",
        "/sdcard/Android/data/io.github.fold.launcher/files",
        "/storage/emulated/0/Android/data/net.kdt.pojavlaunch/files",
        "/sdcard/Android/data/net.kdt.pojavlaunch/files",
        "/storage/emulated/0/Android/data/com.movtery.zalithlauncher/files",
        "/sdcard/Android/data/com.movtery.zalithlauncher/files"
    };

    public static List<String> detectAllRenderers() {
        Set<String> found = new LinkedHashSet<>();

        // 1. Сканируем папки лаунчеров
        for (String root : LAUNCHER_ROOTS) {
            File dir = new File(root);
            if (dir.exists()) {
                scanForRenderers(dir, found, 0);
            }
        }

        // 2. Проверяем переменные окружения
        String envRenderer = System.getenv("POJAV_RENDERER");
        if (envRenderer != null && !envRenderer.isEmpty()) {
            found.add(envRenderer);
        }

        // 3. Проверяем libgl
        String libgl = System.getenv("LIBGL_ES");
        if (libgl != null && !libgl.isEmpty()) {
            if (libgl.contains("zink")) found.add("Zink");
            else if (libgl.contains("angle")) found.add("ANGLE");
            else found.add("GL4ES");
        }

        // 4. Если ничего не нашли — пробуем Vulkan
        if (found.isEmpty()) {
            if (checkVulkanSupport()) {
                found.add("Vulkan");
                found.add("Zink"); // Zink может работать через Vulkan
            }
            found.add("GL4ES"); // Всегда есть GL4ES
        }

        // 5. Сортируем по приоритету
        List<String> sorted = new ArrayList<>(found);
        sorted.sort((a, b) -> {
            int pa = RENDERER_PRIORITY.getOrDefault(a, 0);
            int pb = RENDERER_PRIORITY.getOrDefault(b, 0);
            return pb - pa; // От лучшего к худшему
        });

        AetherBoostMod.LOGGER.info("Detected renderers: {}", sorted);
        return sorted;
    }

    private static void scanForRenderers(File dir, Set<String> found, int depth) {
        if (depth > 4 || !dir.exists() || !dir.isDirectory()) return;
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            String name = file.getName().toLowerCase();
            for (String renderer : RENDERER_PRIORITY.keySet()) {
                if (name.contains(renderer.toLowerCase()) && !name.endsWith(".so") && !name.endsWith(".dll")) {
                    found.add(renderer);
                }
            }
            if (file.isDirectory() && depth < 3) {
                scanForRenderers(file, found, depth + 1);
            }
        }
    }

    private static boolean checkVulkanSupport() {
        try {
            Process p = Runtime.getRuntime().exec("ls /system/lib64/libvulkan.so");
            p.waitFor();
            return p.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
