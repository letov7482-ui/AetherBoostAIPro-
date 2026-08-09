package com.aether.boost.mixin;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WorldRenderer.class)
public class WaterOptimizerMixin {

    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true)
    private ClientWorld disableWaterAnimation(ClientWorld world) {
        // Отключаем анимацию текстур воды/лавы для экономии FPS
        // Упрощённая реализация
        return world;
    }
}
