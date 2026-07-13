package com.tourist.tourGoldPlate.listener;

import com.tourist.tourGoldPlate.TourGoldPlate;
import com.tourist.tourGoldPlate.config.ConfigManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Следит за выходом игроков.
 * Если игрок стоявший на плите вышел — очищаем состояние.
 */
public class PlayerQuitListener implements Listener {

    private final ConfigManager config;
    private final TourGoldPlate plugin;

    public PlayerQuitListener(TourGoldPlate plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (!event.getPlayer().getUniqueId().equals(config.getCurrentPlayer())) return;
        plugin.getPlateManager().clearPlayer(event.getPlayer().getUniqueId());
    }
}
