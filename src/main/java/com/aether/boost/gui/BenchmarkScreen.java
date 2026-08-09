package com.aether.boost.gui;

import com.aether.boost.AetherBoostMod;
import com.aether.boost.engine.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

public class BenchmarkScreen extends Screen {
    private State state = State.READY;
    private String message = "";
    private int progress = 0;
    private int progressMax = 5;
    private String bestRenderer = "";
    private int estimatedGain = 0;
    private String deviceType = "";
    private List<String> foundRenderers;
    private boolean isPC;

    private enum State {
        READY, TESTING, DONE, APPLIED, ERROR
    }

    public BenchmarkScreen() {
        super(Text.literal("AetherBoost AI Pro"));
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int bottomY = this.height - 55;
        int buttonY = this.height / 2 + 10;

        // === НИЖНИЕ КНОПКИ (всегда) ===
        addDrawableChild(ButtonWidget.builder(
            Text.literal(FPSMonitor.isEnabled() ? "FPS: ON" : "FPS: OFF"), btn -> {
                FPSMonitor.setEnabled(!FPSMonitor.isEnabled());
                clearChildren();
                init();
            }).dimensions(5, bottomY, 60, 20).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("Clear RAM"), btn -> {
                int freed = MemoryCleaner.cleanMemory();
                message = "Freed " + freed + " MB";
            }).dimensions(70, bottomY, 60, 20).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("Close"), btn -> close())
            .dimensions(this.width - 50, bottomY, 40, 20).build());

        // === ОСНОВНЫЕ КНОПКИ ===
        if (state == State.READY) {
            addDrawableChild(ButtonWidget.builder(
                Text.literal("Start Smart Test"), btn -> startTest())
                .dimensions(cx - 70, buttonY, 140, 20).build());
        }

        if (state == State.DONE || state == State.APPLIED) {
            addDrawableChild(ButtonWidget.builder(
                Text.literal("Apply Settings"), btn -> applySettings())
                .dimensions(cx - 70, buttonY, 140, 20).build());
            addDrawableChild(ButtonWidget.builder(
                Text.literal("Reset All"), btn -> resetSettings())
                .dimensions(cx - 70, buttonY + 22, 140, 20).build());
            addDrawableChild(ButtonWidget.builder(
                Text.literal("Presets"), btn -> {
                    client.setScreen(new PresetsScreen(this, isPC));
                }).dimensions(cx - 70, buttonY + 44, 140, 20).build());
        }

        if (state == State.ERROR) {
            addDrawableChild(ButtonWidget.builder(
                Text.literal("Retry"), btn -> {
                    state = State.READY;
                    message = "";
                    clearChildren();
                    init();
                }).dimensions(cx - 40, buttonY, 80, 20).build());
        }
    }

    private void startTest() {
        state = State.TESTING;
        message = "";
        progress = 0;
        clearChildren();
        init();
    }

    private void applySettings() {
        try {
            TuningApplier.apply(bestRenderer, isPC);
            state = State.APPLIED;
            message = "Applied! Restart game.";
        } catch (Exception e) {
            state = State.ERROR;
            message = "Error: " + e.getMessage();
        }
        clearChildren();
        init();
    }

    private void resetSettings() {
        try {
            TuningApplier.reset();
            FPSMonitor.setEnabled(false);
            state = State.READY;
            message = "Reset to defaults.";
        } catch (Exception e) {
            message = "Error: " + e.getMessage();
        }
        clearChildren();
        init();
    }

    @Override
    public void tick() {
        super.tick();
        if (FPSMonitor.isEnabled() && state != State.TESTING) {
            MemoryCleaner.autoCleanIfNeeded();
        }
        if (state != State.TESTING) return;

        progress++;
        if (progress > progressMax) progress = progressMax;

        try {
            switch (progress) {
                case 1:
                    message = "Detecting device...";
                    SystemScanner.DeviceType type = SystemScanner.getDeviceType();
                    deviceType = SystemScanner.getDeviceTypeName();
                    isPC = (type == SystemScanner.DeviceType.PC);
                    break;
                case 2:
                    message = "Scanning renderers...";
                    foundRenderers = RendererDetector.detectAllRenderers();
                    break;
                case 3:
                    message = "Testing renderers...";
                    if (foundRenderers != null && !foundRenderers.isEmpty()) {
                        bestRenderer = BenchmarkEngine.findBestRenderer(foundRenderers, isPC);
                    } else {
                        bestRenderer = "GL4ES";
                    }
                    break;
                case 4:
                    message = "Calculating gain...";
                    estimatedGain = ProfileGenerator.estimateGain(
                        SystemScanner.getGPUInfo(),
                        SystemScanner.getFreeRAM(),
                        bestRenderer,
                        isPC
                    );
                    break;
                case 5:
                    state = State.DONE;
                    message = "";
                    ReportGenerator.generateReport(
                        bestRenderer, estimatedGain, deviceType,
                        SystemScanner.getGPUInfo(),
                        SystemScanner.getFreeRAM(),
                        SystemScanner.getAvailableProcessors(),
                        SystemScanner.getJavaVersion()
                    );
                    clearChildren();
                    init();
                    break;
            }
        } catch (Exception e) {
            state = State.ERROR;
            message = "Test error: " + e.getMessage();
            clearChildren();
            init();
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        super.render(ctx, mouseX, mouseY, delta);

        int cx = this.width / 2;
        int cy = this.height / 2;

        // Заголовок
        ctx.drawCenteredTextWithShadow(textRenderer, "AetherBoost AI Pro", cx, 10, 0xFF00FF00);

        // Сообщение
        if (!message.isEmpty()) {
            int color = message.startsWith("Error") ? 0xFF5555 : 0xFFCCCCCC;
            ctx.drawCenteredTextWithShadow(textRenderer, message, cx, 25, color);
        }

        // Прогресс-бар
        if (state == State.TESTING) {
            int barW = 160, barH = 8;
            int barX = cx - barW / 2, barY = cy - 35;
            ctx.fill(barX, barY, barX + barW, barY + barH, 0xFF333333);
            int fill = barW * progress / progressMax;
            ctx.fill(barX, barY, barX + fill, barY + barH, 0xFF00FF00);
        }

        // Результаты теста
        if (state == State.DONE || state == State.APPLIED) {
            int y = cy - 40;
            ctx.drawCenteredTextWithShadow(textRenderer, "Device: " + deviceType, cx, y, 0xFFAAAAFF);
            y += 12;
            ctx.drawCenteredTextWithShadow(textRenderer, "GPU: " + SystemScanner.getGPUInfo(), cx, y, 0xFFAAAAAA);
            y += 12;
            ctx.drawCenteredTextWithShadow(textRenderer, "Best renderer: " + bestRenderer, cx, y, 0xFFFF55);
            y += 14;
            ctx.drawCenteredTextWithShadow(textRenderer, "Expected gain: +" + estimatedGain + " FPS", cx, y, 0xFF00FF00);
        }
    }
}
