package com.aether.boost.engine;

import com.aether.boost.AetherBoostMod;

public class ProfileGenerator {

    /**
     * Оценивает прирост FPS от смены рендера.
     */
    public static int estimateGain(String gpu, int ram, String renderer, boolean isPC) {
        int base = 0;

        // Базовый прирост от рендера
        switch (renderer.toLowerCase()) {
            case "vulkan":
                base = isPC ? 25 : 20;
                break;
            case "zink":
                base = isPC ? 20 : 18;
                break;
            case "ltw":
                base = isPC ? 18 : 22;
                break;
            case "angle":
            case "angle-d3d":
                base = isPC ? 15 : 12;
                break;
            case "angle-metal":
                base = isPC ? 18 : 14;
                break;
            case "mobileglues":
                base = isPC ? 10 : 20;
                break;
            case "holygl4es":
                base = isPC ? 12 : 16;
                break;
            case "fastergl4es":
                base = isPC ? 10 : 14;
                break;
            case "virgl":
                base = isPC ? 14 : 12;
                break;
            case "gl4es":
                base = isPC ? 8 : 10;
                break;
            case "tinygl4es":
                base = isPC ? 6 : 8;
                break;
            default:
                base = isPC ? 10 : 10;
        }

        // Бонус за мощный GPU
        String gpuLower = gpu.toLowerCase();
        if (gpuLower.contains("adreno 7") || gpuLower.contains("adreno 8")
            || gpuLower.contains("mali-g7") || gpuLower.contains("mali-g8")
            || gpuLower.contains("rtx") || gpuLower.contains("gtx 10")
            || gpuLower.contains("apple m") || gpuLower.contains("a17")
            || gpuLower.contains("a16") || gpuLower.contains("a15")) {
            base += 5;
        }

        // Бонус за RAM
        if (ram > 4096) base += 3;
        else if (ram > 2048) base += 1;

        AetherBoostMod.LOGGER.info("Estimated gain for {}: +{} FPS", renderer, base);
        return Math.max(base, 3);
    }

    /**
     * Возвращает понятное описание рендера.
     */
    public static String getRendererDescription(String renderer) {
        return switch (renderer.toLowerCase()) {
            case "vulkan" -> "Vulkan — Best performance, modern API";
            case "zink" -> "Zink — OpenGL on Vulkan, very fast";
            case "ltw" -> "LTW — Experimental, high FPS boost";
            case "angle" -> "ANGLE — OpenGL to D3D/Metal, stable";
            case "mobileglues" -> "MobileGLUES — Optimized for weak phones";
            case "holygl4es" -> "HolyGL4ES — Fast GL4ES fork";
            case "fastergl4es" -> "FasterGL4ES — Lightweight GL4ES";
            case "gl4es" -> "GL4ES — Standard, works everywhere";
            case "virgl" -> "VirGL — Virtual GPU, emulators";
            default -> renderer + " — Available renderer";
        };
    }

    /**
     * Генерирует красивую рекомендацию.
     */
    public static String generateRecommendation(String bestRenderer, int gain, String deviceType) {
        String desc = getRendererDescription(bestRenderer);
        return "Best: " + bestRenderer + " (+" + gain + " FPS)\n" + desc;
    }
}
