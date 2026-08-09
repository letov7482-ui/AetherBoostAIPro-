package com.aether.boost.engine;

public class OptimizationPresets {
    public enum Preset {
        PVP("PvP Mode", "Максимальный FPS для PvP"),
        BALANCED("Balanced", "Баланс графики и FPS"),
        MAX_FPS("Max FPS", "Экстремальная оптимизация");

        public final String name;
        public final String description;

        Preset(String name, String desc) {
            this.name = name;
            this.description = desc;
        }
    }

    public static void applyPreset(Preset preset, boolean isPC) {
        switch (preset) {
            case PVP:
                TuningApplier.applyPvPConfig(isPC);
                break;
            case BALANCED:
                TuningApplier.applyBalancedConfig(isPC);
                break;
            case MAX_FPS:
                TuningApplier.applyMaxFPSConfig(isPC);
                break;
        }
    }
}
