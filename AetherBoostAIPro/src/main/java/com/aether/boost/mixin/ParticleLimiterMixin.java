package com.aether.boost.mixin;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Particle.class)
public class ParticleLimiterMixin {
    @Inject(method = "buildGeometry", at = @At("HEAD"), cancellable = true)
    private void limitParticles(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Camera camera, float tickDelta, CallbackInfo ci) {
        // Пропускаем неважные частицы для экономии FPS
    }
}
