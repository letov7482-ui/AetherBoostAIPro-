package com.aether.boost.engine;

public class ProfileGenerator {

    public static int estimateGain(String gpu, int ram, String renderer, boolean isPC) {
        int base = 0;

        // Базовая оценка по типу рендера
        switch (renderer.toLowerCase()) {
            case "vulkan":
                base = isPC ? 30 : 20;
                break;
            case "zink":
                base = isPC ? 25 : 15;
                break;
            case "angle":
            case "angle-d3d":
            case "angle-metal":
                base = isPC ? 20 : 12;
                break;
            case "virgl":
                base = isPC ? 15 : 10;
                break;
            case "gl4es":
            case "holygl4es":
            case "fastergl4es":
                base = isPC ? 10 : 8;
                break;
            default:
                base = isPC ? 15 : 10;
        }

        // Бонус за мощный GPU
        String gpuLower = gpu.toLowerCase();
        if (gpuLower.contains("adreno 7") || gpuLower.contains("adreno 8")
            || gpuLower.contains("mali-g7") || gpuLower.contains("mali-g8")
            || gpuLower.contains("rtx") || gpuLower.contains("gtx 10")
            || gpuLower.contains("gtx 16") || gpuLower.contains("rx")) {
            base += 5;
        }

        // Бонус за много RAM
        if (ram > 4096) base += 3;
        else if (ram > 2048) base += 1;

        return Math.max(base, 3);
    }
}
