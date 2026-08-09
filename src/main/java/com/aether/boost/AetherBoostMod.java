package com.aether.boost;

import com.aether.boost.gui.BenchmarkScreen;
import com.aether.boost.gui.FPSMonitor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AetherBoostMod implements ModInitializer {
    public static final String MOD_ID = "aetherboost";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static KeyBinding openGuiKey;

    @Override
    public void onInitialize() {
        LOGGER.info("AetherBoost AI Pro initializing...");

        // Горячая клавиша F6 (для ПК)
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.aetherboost.open",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_F6,
            "category.aetherboost"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.wasPressed()) {
                client.setScreen(new BenchmarkScreen());
            }
        });

        // Кнопка в главном меню (для телефона и ПК)
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof TitleScreen) {
                Screens.getButtons(screen).add(
                    ButtonWidget.builder(Text.literal("⚡ AetherBoost AI"), btn -> {
                        client.setScreen(new BenchmarkScreen());
                    }).dimensions(10, 10, 120, 20).build()
                );
            }
        });

        // FPS-монитор через Fabric API (без миксинов!)
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            FPSMonitor.render(drawContext);
        });
    }
}
