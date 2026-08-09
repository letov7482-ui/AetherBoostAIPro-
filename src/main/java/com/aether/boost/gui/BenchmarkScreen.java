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
    private String recommendation = "";
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

        // Кнопка запуска теста
        if (state == State.READY) {
            addDrawableChild(ButtonWidget.builder(
                Text.literal("Запустить умный тест"), btn -> startTest())
                .dimensions(cx - 80, 40, 160, 20).build());
        }

        // Кнопки после теста
        if (state == State.DONE || state == State.APPLIED) {
            addDrawableChild(ButtonWidget.builder(
                Text.literal("✅ Применить настройки"), btn -> applySettings())
                .dimensions(cx - 80, 100, 160, 20).build());
            addDrawableChild(ButtonWidget.builder(
                Text.literal("🔄 Сбросить"), btn -> resetSettings())
                .dimensions(cx - 80, 125, 160, 20).build());

            // Пресеты
            addDrawableChild(ButtonWidget.builder(
                Text.literal("⚔️ PvP Mode"), btn -> {
                    OptimizationPresets.applyPreset(OptimizationPresets.Preset.PVP, isPC);
                    message = "✅ Пресет PvP применён!";
                }).dimensions(cx - 80, 155, 160, 20).build());

            addDrawableChild(ButtonWidget.builder(
                Text.literal("⚖️ Balanced"), btn -> {
                    OptimizationPresets.applyPreset(OptimizationPresets.Preset.BALANCED, isPC);
                    message = "✅ Пресет Balanced применён!";
                }).dimensions(cx - 80, 180, 160, 20).build());

            addDrawableChild(ButtonWidget.builder(
                Text.literal("⚡ Max FPS"), btn -> {
                    OptimizationPresets.applyPreset(OptimizationPresets.Preset.MAX_FPS, isPC);
                    message = "✅ Пресет Max FPS применён!";
                }).dimensions(cx - 80, 205, 160, 20).build());
        }

        // Кнопка ошибки
        if (state == State.ERROR) {
            addDrawableChild(ButtonWidget.builder(
                Text.literal("Попробовать снова"), btn -> {
                    state = State.READY;
                    message = "";
                    clearChildren();
                    init();
                }).dimensions(cx - 80, 100, 160, 20).build());
        }

        // FPS-монитор
        addDrawableChild(ButtonWidget.builder(
            Text.literal(FPSMonitor.isEnabled() ? "📊 FPS: ON" : "📊 FPS: OFF"), btn -> {
                FPSMonitor.setEnabled(!FPSMonitor.isEnabled());
                clearChildren();
                init();
            }).dimensions(10, height - 55, 80, 20).build());

        // Очистка памяти
        addDrawableChild(ButtonWidget.builder(
            Text.literal("🧹 Очистить RAM"), btn -> {
                int freed = MemoryCleaner.cleanMemory();
                message = "✅ Освобождено " + freed + " MB";
            }).dimensions(width - 100, height - 55, 90, 20).build());

        // Закрыть
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Закрыть"), btn -> close())
            .dimensions(cx - 30, height - 30, 60, 20).build());
    }

    private void startTest() {
        state = State.TESTING;
        message = "";
        progress = 0;
        progressMax = 5;
        clearChildren();
        init();
    }

    private void applySettings() {
        try {
            TuningApplier.apply(bestRenderer, isPC);
            FPSMonitor.setEnabled(true);
            state = State.APPLIED;
            message = "✅ Настройки применены! Перезапустите игру.";
        } catch (Exception e) {
            state = State.ERROR;
            message = "❌ Ошибка: " + e.getMessage();
        }
        clearChildren();
        init();
    }

    private void resetSettings() {
        try {
            TuningApplier.reset();
            FPSMonitor.setEnabled(false);
            state = State.READY;
            message = "Настройки сброшены.";
            recommendation = "";
        } catch (Exception e) {
            message = "❌ Ошибка сброса: " + e.getMessage();
        }
        clearChildren();
        init();
    }

    @Override
    public void tick() {
        super.tick();

        // Авто-очистка памяти при низком FPS
        if (FPSMonitor.isEnabled() && state != State.TESTING) {
            MemoryCleaner.autoCleanIfNeeded();
        }

        if (state != State.TESTING) return;

        progress++;
        if (progress > progressMax) progress = progressMax;

        try {
            switch (progress) {
                case 1:
                    message = "Определяем тип устройства...";
                    SystemScanner.DeviceType type = SystemScanner.getDeviceType();
                    deviceType = SystemScanner.getDeviceTypeName();
                    isPC = (type == SystemScanner.DeviceType.PC);
                    break;
                case 2:
                    message = "Ищем установленные рендеры...";
                    foundRenderers = RendererDetector.detectAllRenderers();
                    break;
                case 3:
                    message = "Тестируем рендеры...";
                    if (foundRenderers != null && !foundRenderers.isEmpty()) {
                        bestRenderer = BenchmarkEngine.findBestRenderer(foundRenderers, isPC);
                    } else {
                        bestRenderer = "GL4ES";
                    }
                    break;
                case 4:
                    message = "Рассчитываем прирост FPS...";
                    estimatedGain = ProfileGenerator.estimateGain(
                        SystemScanner.getGPUInfo(),
                        SystemScanner.getFreeRAM(),
                        bestRenderer,
                        isPC
                    );
                    recommendation = ProfileGenerator.generateRecommendation(bestRenderer, estimatedGain, deviceType);
                    break;
                case 5:
                    state = State.DONE;
                    message = "✅ Тест завершён!";
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
            message = "❌ Ошибка теста: " + e.getMessage();
            AetherBoostMod.LOGGER.error("Ошибка тестирования", e);
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
        ctx.drawCenteredTextWithShadow(textRenderer, "⚡ AetherBoost AI Pro ⚡", cx, 12, 0xFF00FF00);

        // Сообщение
        if (!message.isEmpty()) {
            int color = message.startsWith("❌") ? 0xFF5555 : 0xFFFFFF;
            ctx.drawCenteredTextWithShadow(textRenderer, message, cx, 28, color);
        }

        // Прогресс-бар
        if (state == State.TESTING) {
            int barW = 200, barH = 10;
            int barX = cx - barW / 2, barY = cy - 10;
            ctx.fill(barX, barY, barX + barW, barY + barH, 0xFF444444);
            int fill = barW * progress / progressMax;
            ctx.fill(barX, barY, barX + fill, barY + barH, 0xFF00FF00);
            String progressText = progress + "/" + progressMax;
            ctx.drawCenteredTextWithShadow(textRenderer, progressText, cx, barY + barH + 4, 0xFFAAAAAA);
        }

        // Рекомендация
        if ((state == State.DONE || state == State.APPLIED) && !recommendation.isEmpty()) {
            String[] lines = recommendation.split("\n");
            int startY = cy - 35;
            for (int i = 0; i < lines.length; i++) {
                int color = i == 0 ? 0xFFFF55 : 0xFFAAAAFF;
                ctx.drawCenteredTextWithShadow(textRenderer, lines[i], cx, startY + i * 12, color);
            }
        }
    }
    }
