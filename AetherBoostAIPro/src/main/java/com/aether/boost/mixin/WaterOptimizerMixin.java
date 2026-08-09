package com.aether.boost.mixin;

import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WorldRenderer.class)
public class WaterOptimizerMixin {
    // Отключение анимации воды на дальних дистанциях
}
