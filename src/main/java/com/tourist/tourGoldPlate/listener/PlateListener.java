package com.tourist.tourGoldPlate.listener;

import com.tourist.tourGoldPlate.TourGoldPlate;
import com.tourist.tourGoldPlate.config.ConfigManager;
import com.tourist.tourGoldPlate.manager.PlateManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlateListener implements Listener {

    private final ConfigManager config;
    private final PlateManager plateManager;

    public PlateListener(TourGoldPlate plugin) {
        this.config = plugin.getConfigManager();
        this.plateManager = plugin.getPlateManager();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) return;
        if (!config.isEnabled()) return;

        Location plateLoc = config.getPlateLocation();
        if (plateLoc == null) return;

        Block plateBlock = plateLoc.getBlock();
        Block from = event.getFrom().getBlock();
        Block to = event.getTo().getBlock();
        Player player = event.getPlayer();

        if (from.equals(plateBlock) && !to.equals(plateBlock)) {
            plateManager.onPlayerLeave(player);
            return;
        }
        if (to.equals(plateBlock) && !from.equals(plateBlock)) {
            if (!Tag.PRESSURE_PLATES.isTagged(to.getType())) {
                config.setPlateLocation(null);
                return;
            }
            plateManager.onPlayerStep(player);
        }
    }
}
