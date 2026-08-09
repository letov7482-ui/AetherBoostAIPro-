package com.aether.boost.engine;

import com.aether.boost.AetherBoostMod;
import org.lwjgl.opengl.GL11;

public class SystemScanner {

    public enum DeviceType {
        PC, ANDROID, IOS, UNKNOWN
    }

    /**
     * Определяет тип устройства.
     */
    public static DeviceType getDeviceType() {
        String os = System.getProperty("os.name", "").toLowerCase();
        String vendor = System.getProperty("java.vendor", "").toLowerCase();
        String javaHome = System.getProperty("java.home", "").toLowerCase();
        String runtime = System.getProperty("java.runtime.name", "").toLowerCase();

        // Проверка на iOS
        if (os.contains("ios") || os.contains("iphone") || os.contains("ipad")
            || vendor.contains("ios") || vendor.contains("apple")
            || runtime.contains("ios")) {
            return DeviceType.IOS;
        }

        // Проверка на Android
        if (os.contains("android") || vendor.contains("android")
            || javaHome.contains("android") || javaHome.startsWith("/data/")
            || runtime.contains("android")) {
            return DeviceType.ANDROID;
        }

        try {
            Class.forName("android.os.Build");
            return DeviceType.ANDROID;
        } catch (ClassNotFoundException ignored) {}

        // Проверка на ПК
        if (os.contains("win") || os.contains("mac") || os.contains("linux")) {
            return DeviceType.PC;
        }

        return DeviceType.UNKNOWN;
    }

    // Для обратной совместимости
    public static boolean isPC() {
        return getDeviceType() == DeviceType.PC;
    }

    public static String getDeviceTypeName() {
        return switch (getDeviceType()) {
            case PC -> "ПК";
            case ANDROID -> "Телефон (Android)";
            case IOS -> "iPhone/iPad (iOS)";
            case UNKNOWN -> "Неизвестно";
        };
    }

    public static String getGPUInfo() {
        try {
            String renderer = GL11.glGetString(GL11.GL_RENDERER);
            return renderer != null ? renderer : "Unknown GPU";
        } catch (Exception e) {
            return "Unknown GPU";
        }
    }

    public static int getTotalRAM() {
        return (int)(Runtime.getRuntime().maxMemory() / (1024 * 1024));
    }

    public static int getFreeRAM() {
        long max = Runtime.getRuntime().maxMemory();
        long total = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        long used = total - free;
        return (int)((max - used) / (1024 * 1024));
    }

    public static int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static String getJavaVersion() {
        return System.getProperty("java.version");
    }

    public static String getOSInfo() {
        return System.getProperty("os.name") + " " + System.getProperty("os.version");
    }
}
