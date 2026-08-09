package com.aether.boost.engine;

import com.aether.boost.AetherBoostMod;
import net.minecraft.client.MinecraftClient;
import java.util.List;

public class BenchmarkEngine {

    private static final int WARMUP_MS = 500;
    private static final int TEST_DURATION_MS = 2000;

    public static String findBestRenderer(List<String> available, boolean isPC) {
        if (available.isEmpty()) return "GL4ES";

        String best = available.get(0);
        double bestFPS = 0;

        AetherBoostMod.LOGGER.info("Тестирую {} рендеров...", available.size());

        // Сохраняем текущий рендер
        String originalRenderer = System.getProperty("pojav.renderer", "GL4ES");

        for (String renderer : available) {
            // Переключаем рендер
            TuningApplier.applyRendererOnly(renderer);

            // Прогрев
            sleep(WARMUP_MS);

            // Замер FPS
            double fps = measureFPS();
            AetherBoostMod.LOGGER.info("  {} → {} FPS", renderer, String.format("%.1f", fps));

            if (fps > bestFPS) {
                bestFPS = fps;
                best = renderer;
            }
        }

        // Возвращаем исходный рендер
        TuningApplier.applyRendererOnly(originalRenderer);

        AetherBoostMod.LOGGER.info("Лучший: {} ({} FPS)", best, String.format("%.1f", bestFPS));
        return best;
    }

    private static double measureFPS() {
        MinecraftClient client = MinecraftClient.getInstance();
        long start = System.currentTimeMillis();
        int frames = 0;

        while (System.currentTimeMillis() - start < TEST_DURATION_MS) {
            int currentFps = client.getCurrentFps();
            if (currentFps > 0) {
                frames++;
            }
            sleep(50);
        }

        return frames / (TEST_DURATION_MS / 1000.0);
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }
}
