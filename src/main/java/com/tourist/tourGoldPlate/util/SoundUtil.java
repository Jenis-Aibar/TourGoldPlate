package com.tourist.tourGoldPlate.util;

import com.tourist.tourGoldPlate.config.ConfigData;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class SoundUtil {

    private SoundUtil() {}

    public static void play(Player player, ConfigData data) {
        if (!data.soundEnabled) return;

        try {
            Sound sound = Sound.valueOf(data.soundName);
            player.playSound(player.getLocation(), sound, data.soundVolume, data.soundPitch);
        } catch (IllegalArgumentException e) {
        }
    }
}
