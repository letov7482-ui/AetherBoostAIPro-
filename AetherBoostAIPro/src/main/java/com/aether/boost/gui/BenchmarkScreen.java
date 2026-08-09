package com.aether.boost.gui;

import com.aether.boost.engine.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

public class BenchmarkScreen extends Screen {
    private String status = "Ready. Press 'Start Smart Test'";
    private boolean tested = false;
    private String bestRenderer = "";
    private int estimatedGain = 0;
    private List<String> availableRenderers;

    public BenchmarkScreen() {
        super(Text.literal("AetherBoost AI Pro"));
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        addDrawableChild(ButtonWidget.builder(Text.literal("Start Smart Test"), btn -> runBenchmark())
            .dimensions(cx - 60, 40, 120, 20).build());
        if (tested) {
            addDrawableChild(ButtonWidget.builder(Text.literal("Apply Best Settings"), btn -> applyBest())
                .dimensions(cx - 70, 70, 140, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Reset All"), btn -> resetAll())
                .dimensions(cx - 70, 100, 140, 20).build());
        }
        addDrawableChild(ButtonWidget.builder(Text.literal("Close"), btn -> close())
            .dimensions(cx - 30, 130, 60, 20).build());
    }

    private void runBenchmark() {
        boolean isPC = SystemScanner.isPC();
        availableRenderers = RendererDetector.detectAllRenderers();
        bestRenderer = BenchmarkEngine.findBestRenderer(availableRenderers, isPC);
        String gpu = SystemScanner.getGPUInfo();
        int ram = SystemScanner.getFreeRAM();
        estimatedGain = ProfileGenerator.estimateGain(gpu, ram, bestRenderer, isPC);
        status = "PC: " + isPC + "\nFound: " + String.join(", ", availableRenderers)
            + "\nBest: " + bestRenderer + "\nEst. gain: +" + estimatedGain + " FPS";
        tested = true;
        clearChildren();
        init();
    }

    private void applyBest() {
        TuningApplier.apply(bestRenderer);
        status = "Applied! Restart for full effect.";
    }

    private void resetAll() {
        TuningApplier.reset();
        status = "Reset to defaults.";
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        ctx.drawTextWithShadow(textRenderer, status, 10, 10, 0xFFFFFF);
        super.render(ctx, mouseX, mouseY, delta);
    }
}
