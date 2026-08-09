package com.aether.boost.engine;

import com.aether.boost.AetherBoostMod;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportGenerator {

    public static void generateReport(String bestRenderer, int gain, String deviceType,
                                       String gpu, int ram, int processors, String javaVersion) {
        Path reportPath = Paths.get(System.getProperty("user.dir"), "aetherboost_report.txt");
        StringBuilder sb = new StringBuilder();

        sb.append("══════════════════════════════════\n");
        sb.append("  AetherBoost AI Pro — Отчёт\n");
        sb.append("══════════════════════════════════\n\n");
        sb.append("Дата: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))).append("\n\n");

        sb.append("📱 Система:\n");
        sb.append("  Устройство: ").append(deviceType).append("\n");
        sb.append("  GPU: ").append(gpu).append("\n");
        sb.append("  RAM: ").append(ram).append(" MB\n");
        sb.append("  Ядер CPU: ").append(processors).append("\n");
        sb.append("  Java: ").append(javaVersion).append("\n\n");

        sb.append("🧪 Результаты теста:\n");
        sb.append("  Лучший рендер: ").append(bestRenderer).append("\n");
        sb.append("  Ожидаемый прирост: +").append(gain).append(" FPS\n\n");

        sb.append("⚙️ Рекомендуемые JVM-аргументы:\n");
        sb.append("  -XX:+UseZGC -XX:+DisableExplicitGC -Djava.awt.headless=true\n");
        if (!deviceType.contains("ПК")) {
            sb.append("  -Xms256M -Xmx").append(Math.min(ram / 2, 2048)).append("M\n");
        }
        sb.append("\n");

        sb.append("📋 Что оптимизировано:\n");
        sb.append("  ✅ Рендер лаунчера\n");
        sb.append("  ✅ Sodium (если установлен)\n");
        sb.append("  ✅ Iris (если установлен)\n");
        sb.append("  ✅ options.txt (графика, облака, V-Sync)\n");
        sb.append("  ✅ FPS-монитор активирован\n");
        sb.append("  ✅ Авто-очистка памяти активирована\n\n");

        sb.append("══════════════════════════════════\n");
        sb.append("  AetherBoost AI Pro by Aether\n");
        sb.append("  Telegram: @AetherMods\n");
        sb.append("══════════════════════════════════\n");

        try {
            Files.writeString(reportPath, sb.toString());
            AetherBoostMod.LOGGER.info("Отчёт сохранён в {}", reportPath);
        } catch (IOException e) {
            AetherBoostMod.LOGGER.error("Ошибка сохранения отчёта", e);
        }
    }
}
