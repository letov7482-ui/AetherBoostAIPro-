package com.aether.boost.mixin;

import net.minecraft.client.render.chunk.ChunkBuilder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChunkBuilder.class)
public class ChunkCullingMixin {
    // Агрессивное отсечение невидимых чанков
}
