package com.aether.boost.gui;

import com.aether.boost.engine.OptimizationPresets;
import com.aether.boost.engine.TuningApplier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class PresetsScreen extends Screen {
    private final Screen parent;
    private final boolean isPC;
    private String message = "";

    public PresetsScreen(Screen parent, boolean isPC) {
        super(Text.literal("Presets"));
        this.parent = parent;
        this.isPC = isPC;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int startY = 40;

        // PvP Mode
        addDrawableChild(ButtonWidget.builder(
            Text.literal("PvP Mode (Max FPS)"), btn -> {
                OptimizationPresets.applyPreset(OptimizationPresets.Preset.PVP, isPC);
                message = "PvP Mode applied! +5-15 FPS";
            }).dimensions(cx - 80, startY, 160, 20).build());

        // Balanced
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Balanced (FPS + Quality)"), btn -> {
                OptimizationPresets.applyPreset(OptimizationPresets.Preset.BALANCED, isPC);
                message = "Balanced applied!";
            }).dimensions(cx - 80, startY + 25, 160, 20).build());

        // Max FPS
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Ultra FPS (Potato Mode)"), btn -> {
                OptimizationPresets.applyPreset(OptimizationPresets.Preset.MAX_FPS, isPC);
                message = "Ultra FPS applied! +10-20 FPS";
            }).dimensions(cx - 80, startY + 50, 160, 20).build());

        // Назад
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Back"), btn -> client.setScreen(parent))
            .dimensions(cx - 30, startY + 85, 60, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        super.render(ctx, mouseX, mouseY, delta);

        ctx.drawCenteredTextWithShadow(textRenderer, "Select Preset", this.width / 2, 15, 0xFF00FF00);

        if (!message.isEmpty()) {
            ctx.drawCenteredTextWithShadow(textRenderer, message, this.width / 2, 25, 0xFFFF55);
        }
    }
}
