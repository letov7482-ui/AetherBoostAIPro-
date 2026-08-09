package com.aether.boost.engine;

public class ProfileGenerator {
    public static int estimateGain(String gpu, int ram, String renderer, boolean isPC) {
        int base = isPC ? 20 : 10;
        if (renderer.contains("Vulkan")) base += 15;
        else if (renderer.contains("Zink")) base += 10;
        else if (renderer.contains("ANGLE")) base += 5;
        if (ram > 4096) base += 5;
        if (gpu.toLowerCase().contains("adreno")) base += 3;
        return base;
    }
}
