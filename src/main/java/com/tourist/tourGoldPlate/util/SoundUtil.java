package com.tourist.tourGoldPlate.util;

import com.tourist.tourGoldPlate.config.ConfigData;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Утилита для воспроизведения звуков.
 * Статический класс — не нужен объект, просто вызываешь SoundUtil.play(...)
 */
public final class SoundUtil {

    // Конструктор приватный — этот класс нельзя создать, только использовать статически
    private SoundUtil() {}

    /**
     * Воспроизводит звук из конфига игроку.
     * Если звук выключен или имя неверное — молча пропускаем.
     */
    public static void play(Player player, ConfigData data) {
        if (!data.soundEnabled) return;

        try {
            Sound sound = Sound.valueOf(data.soundName);
            player.playSound(player.getLocation(), sound, data.soundVolume, data.soundPitch);
        } catch (IllegalArgumentException e) {
            // Имя звука неверное — валидация должна была поймать это раньше
            // Здесь просто молчим чтобы не спамить консоль каждый тик
        }
    }
}
