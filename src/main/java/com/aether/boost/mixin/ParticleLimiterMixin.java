package com.aether.boost.mixin;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Particle.class)
public abstract class ParticleLimiterMixin {
    @Shadow public abstract boolean isAlive();

    @Inject(method = "buildGeometry", at = @At("HEAD"), cancellable = true)
    private void limitParticles(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Camera camera, float tickDelta, CallbackInfo ci) {
        if (this instanceof Particle particle) {
            String className = particle.getClass().getSimpleName().toLowerCase();
            if (className.contains("rain") || className.contains("snow")
                || className.contains("bubble") || className.contains("torch")
                || className.contains("dust") || className.contains("drip")) {
                if (camera.getPos().distanceTo(particle.getPos()) > 16) {
                    ci.cancel();
                }
            }
        }
    }
}
