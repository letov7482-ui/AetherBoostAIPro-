package com.aether.boost.mixin;

import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ParticleManager.class)
public class ParticleLimitMixin {
    @ModifyVariable(method = "addParticle", at = @At("HEAD"), argsOnly = true)
    private int limitParticles(int count) {
        // Ограничиваем количество частиц до 1000
        return Math.min(count, 1000);
    }
}
