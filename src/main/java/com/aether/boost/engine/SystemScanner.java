package com.aether.boost.engine;

import com.aether.boost.AetherBoostMod;
import org.lwjgl.opengl.GL11;

public class SystemScanner {

    public static boolean isPC() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win") || os.contains("mac") || os.contains("linux") && !isAndroid();
    }

    private static boolean isAndroid() {
        try {
            Class.forName("android.os.Build");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
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
