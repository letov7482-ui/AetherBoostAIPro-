package com.aether.boost.engine;

import com.aether.boost.AetherBoostMod;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RendererDetector {
    private static final String[] ALL_KNOWN_RENDERERS = {
        "GL4ES", "Zink", "ANGLE", "Vulkan", "VirGL", "MobileGLUES", "LTW",
        "HolyGL4ES", "FasterGL4ES", "TinyGL4ES", "OpenGL", "DirectX", "Software",
        "Zink-Legacy", "ANGLE-D3D", "ANGLE-Metal", "Vulkan-Zink", "NativeGL"
    };

    private static final String[][] LAUNCHER_ROOTS = {
        {"/storage/emulated/0/Android/data/io.github.fold.launcher/files", "/sdcard/Android/data/io.github.fold.launcher/files"},
        {"/storage/emulated/0/Android/data/net.kdt.pojavlaunch/files", "/sdcard/Android/data/net.kdt.pojavlaunch/files"},
        {"/storage/emulated/0/Android/data/com.movtery.zalithlauncher/files", "/sdcard/Android/data/com.movtery.zalithlauncher/files"},
        {System.getProperty("user.home") + "/.minecraft"},
        {System.getProperty("user.home") + "/AppData/Roaming/.minecraft"}
    };

    public static List<String> detectAllRenderers() {
        Set<String> found = new LinkedHashSet<>();
        for (String[] paths : LAUNCHER_ROOTS) {
            for (String path : paths) {
                File root = new File(path);
                if (root.exists()) scanDirectory(root, found, 0);
            }
        }
        String envRenderer = System.getenv("POJAV_RENDERER");
        if (envRenderer != null && !envRenderer.isEmpty()) found.add(envRenderer);
        if (System.getenv("LIBGL_ES") != null) found.add("GL4ES (env)");
        if (System.getenv("MESA_GL_VERSION_OVERRIDE") != null) found.add("VirGL (MESA)");
        if (found.isEmpty()) found.add("GL4ES");
        AetherBoostMod.LOGGER.info("Обнаружено рендеров: {} — {}", found.size(), found);
        return new ArrayList<>(found);
    }

    private static void scanDirectory(File dir, Set<String> found, int depth) {
        if (depth > 4 || !dir.exists() || !dir.isDirectory()) return;
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File file : files) {
            String name = file.getName().toLowerCase();
            for (String renderer : ALL_KNOWN_RENDERERS) {
                if (name.contains(renderer.toLowerCase())) found.add(renderer);
            }
            if (file.isDirectory() && depth < 3) scanDirectory(file, found, depth + 1);
        }
    }
}
