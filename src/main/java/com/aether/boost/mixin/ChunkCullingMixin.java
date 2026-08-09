package com.aether.boost.mixin;

import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkBuilder.BuiltChunk.class)
public class ChunkCullingMixin {

    @Inject(method = "method_60963", at = @At("HEAD"), cancellable = true, remap = false)
    private void optimizeVisibilityCheck(Direction from, Direction to, CallbackInfoReturnable<Boolean> cir) {
        // Агрессивное отсечение: не рендерим чанк, если он полностью за другим
        if (from == to.getOpposite()) {
            cir.setReturnValue(false);
        }
    }
}
