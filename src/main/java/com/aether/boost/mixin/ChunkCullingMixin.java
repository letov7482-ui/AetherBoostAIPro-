package com.aether.boost.mixin;

import net.minecraft.client.render.chunk.ChunkBuilder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChunkBuilder.BuiltChunk.class)
public class ChunkCullingMixin {
    // Агрессивное отсечение чанков будет добавлено в версии 1.1
}
