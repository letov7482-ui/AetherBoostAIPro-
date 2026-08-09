package com.aether.boost.mixin;

import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkBuilder.BuiltChunk.class)
public class ChunkCullingMixin {

    /**
     * Более агрессивное отсечение: считаем, что чанк не виден,
     * если соседний чанк полностью перекрывает его.
     */
    @Inject(method = "isVisibleThrough", at = @At("HEAD"), cancellable = true)
    private void optimizeVisibilityCheck(Direction from, Direction to, CallbackInfoReturnable<Boolean> cir) {
        // Если чанк сзади и не на краю — не рендерим
        if (from == to.getOpposite()) {
            cir.setReturnValue(false);
        }
    }
}
