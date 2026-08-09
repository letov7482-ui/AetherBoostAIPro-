package com.aether.boost.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class FPSMonitor {
    private static boolean enabled = false;
    private static int posX = 5;
    private static int posY = 5;

    public static void setEnabled(boolean e) { enabled = e; }
    public static boolean isEnabled() { return enabled; }

    public static void setPosition(int x, int y) { posX = x; posY = y; }

    public static void render(DrawContext ctx) {
        if (!enabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getCurrentFps() <= 0) return;

        int fps = client.getCurrentFps();
        int color;
        if (fps >= 60) color = 0xFF00FF00;
        else if (fps >= 30) color = 0xFFFFFF00;
        else color = 0xFFFF0000;

        String text = fps + " FPS";
        ctx.drawTextWithShadow(client.textRenderer, text, posX, posY, color);
    }
}
