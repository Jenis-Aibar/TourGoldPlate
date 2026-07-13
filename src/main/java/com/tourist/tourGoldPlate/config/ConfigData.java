package com.tourist.tourGoldPlate.config;

/**
 * Просто хранит данные конфига — никакой логики здесь нет.
 * Это называется POJO (Plain Old Java Object).
 *
 * Зачем отделять данные от логики?
 * ConfigManager занимается загрузкой и валидацией.
 * ConfigData просто хранит результат — его легко передавать между классами.
 */
public class ConfigData {

    // ─── Общее ──────────────────────────────────────────────────────────────
    public boolean enabled;

    // ─── Местоположение плиты ───────────────────────────────────────────────
    public String world;
    public int x, y, z;

    // ─── Тип и тик ──────────────────────────────────────────────────────────
    public String rewardType;   // "FIXED" или "PER_ONLINE"
    public int tick;

    // ─── FIXED ──────────────────────────────────────────────────────────────
    public int fixedAmount;

    // ─── PER_ONLINE ─────────────────────────────────────────────────────────
    public int perOnlineBase;
    public int perOnlinePerPlayer;

    // ─── Звук ───────────────────────────────────────────────────────────────
    public boolean soundEnabled;
    public String soundName;
    public float soundVolume;
    public float soundPitch;
}
