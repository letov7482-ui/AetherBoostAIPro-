package com.aether.boost.gui;

import com.aether.boost.engine.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

public class BenchmarkScreen extends Screen {
    private String status = "Готов к запуску";
    private String loadingMessage = "";
    private boolean tested = false;
    private boolean loading = false;
    private int loadingStep = 0;
    private String bestRenderer = "";
    private int estimatedGain = 0;
    private List<String> availableRenderers;
    private String deviceType = "";

    public BenchmarkScreen() {
        super(Text.literal("AetherBoost AI Pro"));
    }

    @Override
    protected void init() {
        int cx = this.width / 2;

        if (!loading && !tested) {
            addDrawableChild(ButtonWidget.builder(Text.literal("Запустить умный тест"), btn -> startBenchmark())
                .dimensions(cx - 80, 40, 160, 20).build());
        }

        if (tested && !loading) {
            addDrawableChild(ButtonWidget.builder(Text.literal("✅ Применить настройки"), btn -> applyBest())
                .dimensions(cx - 80, 70, 160, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("🔄 Сбросить всё"), btn -> resetAll())
                .dimensions(cx - 80, 95, 160, 20).build());
        }

        addDrawableChild(ButtonWidget.builder(Text.literal("Закрыть"), btn -> close())
            .dimensions(cx - 30, this.height - 30, 60, 20).build());
    }

    private void startBenchmark() {
        loading = true;
        loadingStep = 0;
        loadingMessage = "Сканируем устройство...";
        clearChildren();
        init();
    }

    @Override
    public void tick() {
        super.tick();
        if (!loading) return;

        loadingStep++;
        switch (loadingStep) {
            case 20:
                loadingMessage = "Определяем тип устройства...";
                deviceType = SystemScanner.isPC() ? "ПК" : "Телефон";
                break;
            case 40:
                loadingMessage = "Ищем установленные рендеры...";
                availableRenderers = RendererDetector.detectAllRenderers();
                break;
            case 60:
                loadingMessage = "Анализируем GPU: " + SystemScanner.getGPUInfo();
                break;
            case 80:
                loadingMessage = "Оцениваем доступную RAM...";
                break;
            case 100:
                loadingMessage = "Тестируем " + availableRenderers.size() + " рендеров...";
                break;
            case 140:
                loadingMessage = "Выбираем оптимальные настройки...";
                bestRenderer = BenchmarkEngine.findBestRenderer(availableRenderers, SystemScanner.isPC());
                break;
            case 160:
                loadingMessage = "Рассчитываем прирост FPS...";
                estimatedGain = ProfileGenerator.estimateGain(
                    SystemScanner.getGPUInfo(),
                    SystemScanner.getFreeRAM(),
                    bestRenderer,
                    SystemScanner.isPC()
                );
                break;
            case 180:
                loading = false;
                tested = true;
                status = "✅ Тест завершён!\n\nУстройство: " + deviceType
                    + "\nНайдено рендеров: " + availableRenderers.size()
                    + "\nЛучший рендер: " + bestRenderer
                    + "\nОжидаемый прирост: +" + estimatedGain + " FPS";
                clearChildren();
                init();
                break;
        }
    }

    private void applyBest() {
        TuningApplier.apply(bestRenderer, SystemScanner.isPC());
        status = "✅ Настройки применены! Перезапустите игру для полного эффекта.";
    }

    private void resetAll() {
        TuningApplier.reset();
        tested = false;
        status = "Настройки сброшены.";
        clearChildren();
        init();
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);

        if (loading) {
            String dots = ".".repeat((loadingStep / 10) % 4);
            ctx.drawCenteredTextWithShadow(textRenderer, loadingMessage + dots, this.width / 2, this.height / 2 - 20, 0xFFFF55);
            int barWidth = 200;
            int barHeight = 6;
            int barX = this.width / 2 - barWidth / 2;
            int barY = this.height / 2;
            int progress = Math.min(loadingStep * 100 / 180, 100);
            ctx.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF555555);
            ctx.fill(barX, barY, barX + barWidth * progress / 100, barY + barHeight, 0xFF00FF00);
        } else {
            String[] lines = status.split("\n");
            for (int i = 0; i < lines.length; i++) {
                ctx.drawCenteredTextWithShadow(textRenderer, lines[i], this.width / 2, 15 + i * 12, 0xFFFFFF);
            }
        }

        super.render(ctx, mouseX, mouseY, delta);
    }
              }
