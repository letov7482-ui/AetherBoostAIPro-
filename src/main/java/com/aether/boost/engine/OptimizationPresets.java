package com.aether.boost.engine;

import com.aether.boost.AetherBoostMod;

public class OptimizationPresets {
    public enum Preset {
        PVP("PvP Mode", "Max FPS for PvP"),
        BALANCED("Balanced", "Balance of FPS and quality"),
        MAX_FPS("Ultra FPS", "Extreme optimization");

        public final String name;
        public final String description;

        Preset(String name, String desc) {
            this.name = name;
            this.description = desc;
        }
    }

    public static void applyPreset(Preset preset, boolean isPC) {
        AetherBoostMod.LOGGER.info("Applying preset: {}", preset.name);
        switch (preset) {
            case PVP -> TuningApplier.applyPvPConfig(isPC);
            case BALANCED -> TuningApplier.applyBalancedConfig(isPC);
            case MAX_FPS -> TuningApplier.applyMaxFPSConfig(isPC);
        }
    }
}
