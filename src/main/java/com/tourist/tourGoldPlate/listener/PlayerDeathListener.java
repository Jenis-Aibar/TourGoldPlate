package com.tourist.tourGoldPlate.listener;

import com.tourist.tourGoldPlate.TourGoldPlate;
import com.tourist.tourGoldPlate.config.ConfigManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.UUID;

public class PlayerDeathListener implements Listener {

    private final ConfigManager config;
    private final TourGoldPlate plugin;

    public PlayerDeathListener(TourGoldPlate plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    @EventHandler
    public void OnDeath (PlayerDeathEvent e) {
        UUID id = e.getPlayer().getUniqueId();
        if (config.getCurrentPlayer() == id) {
            plugin.getPlateManager().clearPlayer(id);
        }


    }


}
