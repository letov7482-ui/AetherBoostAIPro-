package com.aether.boost.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRenderDistanceMixin<T extends Entity> {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void limitRenderDistance(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        // Не рендерим мобов дальше 48 блоков
        if (entity != null && entity.squaredDistanceTo(renderManager().camera.getPos()) > 2304) { // 48^2
            ci.cancel();
        }
    }

    private net.minecraft.client.render.entity.EntityRenderDispatcher renderManager() {
        return net.minecraft.client.MinecraftClient.getInstance().getEntityRenderDispatcher();
    }
}
