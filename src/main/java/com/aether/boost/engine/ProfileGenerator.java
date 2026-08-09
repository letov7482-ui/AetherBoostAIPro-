package com.aether.boost.engine;

import java.util.List;

public class ProfileGenerator {

    /**
     * Возвращает красивое название лучшего рендера для пользователя.
     */
    public static String getBestRendererName(String renderer) {
        return switch (renderer.toLowerCase()) {
            case "vulkan" -> "Vulkan (макс. производительность)";
            case "zink" -> "Zink (современный, стабильный)";
            case "angle", "angle-d3d" -> "ANGLE (D3D, Windows)";
            case "angle-metal" -> "ANGLE (Metal, Mac/iOS)";
            case "virgl" -> "VirGL (виртуальный GPU)";
            case "gl4es" -> "GL4ES (стандартный)";
            case "holygl4es" -> "Holy GL4ES (оптимизированный)";
            case "fastergl4es" -> "Faster GL4ES (лёгкий)";
            case "tinygl4es" -> "Tiny GL4ES (минимальный)";
            case "ltw" -> "LTW (экспериментальный, высокий FPS)";
            case "mobileglues" -> "MobileGLUES (для слабых телефонов)";
            case "nativegl" -> "Native GL (прямой доступ)";
            case "opengles3", "opengles", "gles" -> "OpenGL ES 3 (базовый)";
            default -> renderer + " (найден)";
        };
    }

    /**
     * Оценивает прирост FPS при смене на указанный рендер.
     */
    public static int estimateGain(String gpu, int ram, String renderer, boolean isPC) {
        int base = 0;

        switch (renderer.toLowerCase()) {
            case "vulkan":
                base = isPC ? 30 : 20;
                break;
            case "ltw":
                base = isPC ? 28 : 22;
                break;
            case "zink":
                base = isPC ? 25 : 18;
                break;
            case "angle":
            case "angle-d3d":
                base = isPC ? 22 : 12;
                break;
            case "angle-metal":
                base = isPC ? 24 : 15;
                break;
            case "virgl":
                base = isPC ? 18 : 14;
                break;
            case "holygl4es":
                base = isPC ? 15 : 16;
                break;
            case "fastergl4es":
                base = isPC ? 12 : 14;
                break;
            case "mobileglues":
                base = isPC ? 8 : 18;
                break;
            case "tinygl4es":
                base = isPC ? 10 : 12;
                break;
            case "gl4es":
                base = isPC ? 10 : 10;
                break;
            case "nativegl":
                base = isPC ? 20 : 10;
                break;
            default:
                base = isPC ? 15 : 10;
        }

        // Бонус за мощный GPU
        String gpuLower = gpu.toLowerCase();
        if (gpuLower.contains("adreno 7") || gpuLower.contains("adreno 8")
            || gpuLower.contains("mali-g7") || gpuLower.contains("mali-g8")
            || gpuLower.contains("rtx") || gpuLower.contains("gtx 10")
            || gpuLower.contains("gtx 16") || gpuLower.contains("rx")
            || gpuLower.contains("apple m") || gpuLower.contains("a17")
            || gpuLower.contains("a16") || gpuLower.contains("a15")) {
            base += 5;
        }

        // Бонус за много RAM
        if (ram > 4096) base += 3;
        else if (ram > 2048) base += 1;

        return Math.max(base, 3);
    }

    /**
     * Генерирует итоговую рекомендацию.
     */
    public static String generateRecommendation(String bestRenderer, int gain, String deviceType) {
        String rendererName = getBestRendererName(bestRenderer);
        return "Лучше всего ставить рендер " + bestRenderer + " для буста FPS\n"
            + "(" + rendererName + ")\n"
            + "Ожидаемый прирост: +" + gain + " FPS\n"
            + "Тип устройства: " + deviceType;
    }
    }
