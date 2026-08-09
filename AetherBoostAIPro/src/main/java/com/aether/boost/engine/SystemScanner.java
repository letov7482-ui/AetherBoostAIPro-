package com.aether.boost.engine;

import com.aether.boost.AetherBoostMod;

public class SystemScanner {
    public static boolean isPC() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win") || os.contains("mac") || os.contains("linux") && !isAndroid();
    }

    private static boolean isAndroid() {
        return System.getProperty("java.vendor").toLowerCase().contains("android")
            || System.getProperty("java.home").toLowerCase().contains("android");
    }

    public static String getGPUInfo() {
        try {
            return System.getProperty("sun.graphics.device", "Unknown GPU");
        } catch (Exception e) {
            return "Unknown GPU";
        }
    }

    public static int getFreeRAM() {
        long max = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        long total = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        long free = Runtime.getRuntime().freeMemory() / (1024 * 1024);
        return (int)((max - total + free));
    }
}
