package com.tourist.tourGoldPlate.listener;

import com.tourist.tourGoldPlate.TourGoldPlate;
import com.tourist.tourGoldPlate.config.ConfigManager;
import com.tourist.tourGoldPlate.manager.PlateManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Слушает движение игроков и определяет:
 *  - Зашёл ли игрок на плиту
 *  - Ушёл ли игрок с плиты
 *
 * Вся логика что делать — делегируется в PlateManager.
 * Этот класс только детектирует событие.
 */
public class PlateListener implements Listener {

    private final ConfigManager config;
    private final PlateManager plateManager;

    public PlateListener(TourGoldPlate plugin) {
        this.config = plugin.getConfigManager();
        this.plateManager = plugin.getPlateManager();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        // Оптимизация: если игрок не сменил блок — игнорируем
        // hasChangedBlock() проверяет именно смену блока, не каждый пиксель движения
        if (!event.hasChangedBlock()) return;
        if (!config.isEnabled()) return;

        Location plateLoc = config.getPlateLocation();
        if (plateLoc == null) return;

        Block plateBlock = plateLoc.getBlock();
        Block from = event.getFrom().getBlock();
        Block to = event.getTo().getBlock();
        Player player = event.getPlayer();

        // Ушёл с плиты
        if (from.equals(plateBlock) && !to.equals(plateBlock)) {
            plateManager.onPlayerLeave(player);
            return;
        }

        // Встал на плиту
        if (to.equals(plateBlock) && !from.equals(plateBlock)) {
            plateManager.onPlayerStep(player);
        }
    }
}
