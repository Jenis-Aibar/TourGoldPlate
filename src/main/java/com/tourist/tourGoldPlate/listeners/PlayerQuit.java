package com.tourist.tourGoldPlate.listeners;

import com.tourist.tourGoldPlate.TourGoldPlate;
import com.tourist.tourGoldPlate.plate.PlateData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {
    private final PlateData plateData;

    public PlayerQuit (TourGoldPlate plugin) {
        plateData = plugin.getPlateData();
    }

    @EventHandler
    public void playerQuit (PlayerQuitEvent event) {
        if (event.getPlayer().getUniqueId().equals(plateData.getCurrentPlayer())) plateData.setCurrentPlayer(null);
    }

}
