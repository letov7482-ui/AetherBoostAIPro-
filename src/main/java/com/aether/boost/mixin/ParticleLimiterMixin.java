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
        Particle self = (Particle) (Object) this;
        String className = self.getClass().getSimpleName().toLowerCase();

        // Пропускаем неважные частицы на расстоянии
        if (className.contains("rain") || className.contains("snow")
            || className.contains("bubble") || className.contains("torch")
            || className.contains("dust") || className.contains("drip")) {
            double distX = camera.getPos().x - self.x;
            double distY = camera.getPos().y - self.y;
            double distZ = camera.getPos().z - self.z;
            if (distX * distX + distY * distY + distZ * distZ > 256) { // 16^2
                ci.cancel();
            }
        }
    }
}
