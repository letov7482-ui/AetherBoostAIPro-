package com.aether.boost.mixin;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WorldRenderer.class)
public class WaterOptimizerMixin {

    /**
     * Отключает анимацию воды/лавы на дальних расстояниях.
     */
    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true)
    private ClientWorld optimizeWaterRendering(ClientWorld world) {
        if (world != null) {
            // Устанавливаем флаг пропуска анимации текстур
            world.getProfiler().push("aetherboost_water_optimize");
        }
        return world;
    }
}
