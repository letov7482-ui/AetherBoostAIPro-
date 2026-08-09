package com.aether.boost.engine;

import java.util.List;

public class BenchmarkEngine {
    public static String findBestRenderer(List<String> available, boolean isPC) {
        String best = available.get(0);
        int bestFPS = 0;
        for (String r : available) {
            int fps = simulateTest(r, isPC);
            if (fps > bestFPS) {
                bestFPS = fps;
                best = r;
            }
        }
        return best;
    }

    private static int simulateTest(String renderer, boolean isPC) {
        if (isPC) {
            return switch (renderer) {
                case "Vulkan" -> 120;
                case "Zink" -> 100;
                case "ANGLE" -> 90;
                default -> 80;
            };
        } else {
            return switch (renderer) {
                case "Zink" -> 45;
                case "ANGLE" -> 35;
                case "GL4ES" -> 25;
                default -> 30;
            };
        }
    }
}
