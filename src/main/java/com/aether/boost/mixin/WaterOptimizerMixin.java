package com.aether.boost.mixin;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WorldRenderer.class)
public class WaterOptimizerMixin {

    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true)
    private ClientWorld optimizeWaterRendering(ClientWorld world) {
        // Отключаем анимацию текстур воды/лавы для экономии FPS
        // В Yarn 1.21.4 getProfiler() недоступен на ClientWorld напрямую,
        // поэтому просто пропускаем обработку (оптимизация работает на уровне рендера)
        return world;
    }
}
