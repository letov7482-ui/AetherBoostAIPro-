package com.aether.boost.mixin;

import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Sprite.class)
public class TextureAnimationMixin {
    @Inject(method = "method_24204", at = @At("HEAD"), cancellable = true, remap = false)
    private void disableAnimation(CallbackInfo ci) {
        // Отключаем анимацию текстур для экономии FPS
        ci.cancel();
    }
}
