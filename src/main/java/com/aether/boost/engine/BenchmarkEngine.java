package com.aether.boost.engine;

import com.aether.boost.AetherBoostMod;
import net.minecraft.client.MinecraftClient;
import java.util.*;

public class BenchmarkEngine {

    /**
     * Тестирует все рендеры и возвращает имя лучшего.
     */
    public static String findBestRenderer(List<String> available, boolean isPC) {
        if (available.isEmpty()) return "GL4ES";

        AetherBoostMod.LOGGER.info("=== Starting renderer benchmark ===");
        AetherBoostMod.LOGGER.info("Testing {} renderers: {}", available.size(), available);

        String bestRenderer = available.get(0);
        int bestFPS = 0;
        Map<String, Integer> results = new LinkedHashMap<>();

        for (String renderer : available) {
            AetherBoostMod.LOGGER.info("Testing: {}...", renderer);
            int fps = testRenderer(renderer);
            results.put(renderer, fps);
            AetherBoostMod.LOGGER.info("  {} = {} FPS", renderer, fps);

            if (fps > bestFPS) {
                bestFPS = fps;
                bestRenderer = renderer;
            }
        }

        AetherBoostMod.LOGGER.info("=== Results ===");
        for (var entry : results.entrySet()) {
            String marker = entry.getKey().equals(bestRenderer) ? " ★ BEST" : "";
            AetherBoostMod.LOGGER.info("  {} = {} FPS{}", entry.getKey(), entry.getValue(), marker);
        }

        return bestRenderer;
    }

    /**
     * Реально замеряет FPS на текущем рендере.
     */
    private static int testRenderer(String renderer) {
        MinecraftClient client = MinecraftClient.getInstance();

        // Сохраняем текущий рендер
        String oldRenderer = System.getProperty("pojav.renderer", "");

        // Переключаем рендер
        TuningApplier.applyRendererOnly(renderer);

        // Ждём стабилизации
        sleep(500);

        // Замеряем FPS
        int totalFPS = 0;
        int samples = 0;
        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < 2000) {
            int fps = client.getCurrentFps();
            if (fps > 0 && fps < 999) { // Игнорируем нереалистичные значения
                totalFPS += fps;
                samples++;
            }
            sleep(100);
        }

        // Возвращаем старый рендер
        if (!oldRenderer.isEmpty()) {
            TuningApplier.applyRendererOnly(oldRenderer);
        }

        if (samples == 0) return 1;
        return totalFPS / samples;
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
