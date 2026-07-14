package com.tourist.tourGoldPlate.listener;

import com.tourist.tourGoldPlate.TourGoldPlate;
import com.tourist.tourGoldPlate.config.ConfigManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

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
