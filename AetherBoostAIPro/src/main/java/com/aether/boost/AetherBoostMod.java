package com.aether.boost;

import com.aether.boost.gui.BenchmarkScreen;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
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
    }
}
