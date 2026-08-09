package com.aether.boost.mixin;

import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class CloudDisableMixin {
    @Inject(method = "method_22714", at = @At("HEAD"), cancellable = true, remap = false)
    private void disableClouds(CallbackInfo ci) {
        // Отключаем рендер облаков
        ci.cancel();
    }
}
