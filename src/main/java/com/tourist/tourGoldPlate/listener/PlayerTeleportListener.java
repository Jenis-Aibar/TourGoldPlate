package com.tourist.tourGoldPlate.listener;

import com.tourist.tourGoldPlate.TourGoldPlate;
import com.tourist.tourGoldPlate.config.ConfigManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.UUID;

public class PlayerTeleportListener implements Listener {

    private final TourGoldPlate plugin;
    private final ConfigManager config;

    public PlayerTeleportListener (TourGoldPlate plugin) {
        this.plugin = plugin;
        config = plugin.getConfigManager();
    }

    @EventHandler
    public void OnTeleport (PlayerTeleportEvent e) {
        UUID id = e.getPlayer().getUniqueId();

        if (config.getCurrentPlayer().equals(id)) {
            plugin.getPlateManager().clearPlayer(id);
        }
    }
}
