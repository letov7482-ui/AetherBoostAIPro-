package com.aether.boost.engine;

import com.aether.boost.AetherBoostMod;
import net.minecraft.client.MinecraftClient;

public class MemoryCleaner {
    private static long lastCleanTime = 0;
    private static final int CLEAN_INTERVAL_MS = 30000; // 30 секунд
    private static final int FPS_THRESHOLD = 20;

    /**
     * Автоматически очищает память, если FPS низкий.
     * @return сколько MB освобождено, или 0 если очистка не нужна
     */
    public static int autoCleanIfNeeded() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getCurrentFps() <= 0) return 0;

        long now = System.currentTimeMillis();
        if (now - lastCleanTime < CLEAN_INTERVAL_MS) return 0;

        if (client.getCurrentFps() < FPS_THRESHOLD) {
            return cleanMemory();
        }
        return 0;
    }

    /**
     * Принудительная очистка памяти.
     */
    public static int cleanMemory() {
        long before = Runtime.getRuntime().freeMemory();
        System.gc();
        long after = Runtime.getRuntime().freeMemory();
        lastCleanTime = System.currentTimeMillis();

        int freed = (int)((after - before) / (1024 * 1024));
        AetherBoostMod.LOGGER.info("Память очищена. Освобождено: {} MB", freed);
        return Math.max(freed, 0);
    }
}
