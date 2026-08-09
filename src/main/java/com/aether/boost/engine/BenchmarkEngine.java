package com.aether.boost.engine;

import com.aether.boost.AetherBoostMod;
import net.minecraft.client.MinecraftClient;
import java.util.List;

public class BenchmarkEngine {

    private static final int TEST_DURATION_MS = 3000; // 3 секунды на каждый рендер
    private static final int WARMUP_MS = 1000;        // 1 секунда на прогрев

    /**
     * Тестирует все доступные рендеры и возвращает имя лучшего.
     */
    public static String findBestRenderer(List<String> available, boolean isPC) {
        String best = available.get(0);
        int bestFPS = 0;

        AetherBoostMod.LOGGER.info("Начинаю тестирование {} рендеров...", available.size());

        for (String renderer : available) {
            int fps = testRendererReal(renderer, isPC);
            AetherBoostMod.LOGGER.info("Рендер {} — средний FPS: {}", renderer, fps);

            if (fps > bestFPS) {
                bestFPS = fps;
                best = renderer;
            }
        }

        AetherBoostMod.LOGGER.info("Лучший рендер: {} ({} FPS)", best, bestFPS);
        return best;
    }

    /**
     * Реально переключает рендер, прогревает сцену и замеряет FPS.
     */
    private static int testRendererReal(String renderer, boolean isPC) {
        MinecraftClient client = MinecraftClient.getInstance();

        // Сохраняем текущий рендер
        String previousRenderer = System.getenv("POJAV_RENDERER");
        if (previousRenderer == null) previousRenderer = "GL4ES";

        // Применяем тестируемый рендер
        TuningApplier.applyRendererOnly(renderer);

        // Прогрев
        try {
            Thread.sleep(WARMUP_MS);
        } catch (InterruptedException ignored) {}

        // Замер FPS
        long startTime = System.currentTimeMillis();
        int frameCount = 0;

        while (System.currentTimeMillis() - startTime < TEST_DURATION_MS) {
            if (client.getCurrentFps() > 0) {
                frameCount++;
            }
            try {
                Thread.sleep(16); // ~60 FPS опрос
            } catch (InterruptedException ignored) {}
        }

        int avgFPS = (int)(frameCount / (TEST_DURATION_MS / 1000.0));

        // Возвращаем предыдущий рендер
        TuningApplier.applyRendererOnly(previousRenderer);

        return Math.max(avgFPS, 1); // Минимум 1 FPS, чтобы не делить на ноль
    }
}
