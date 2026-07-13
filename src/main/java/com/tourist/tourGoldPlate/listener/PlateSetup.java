package com.tourist.tourGoldPlate.listener;

import com.tourist.tourGoldPlate.TourGoldPlate;
import com.tourist.tourGoldPlate.config.ConfigManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Слушает взаимодействие с золотой лопатой для привязки/удаления плиты.
 *
 * ПКМ по плите — привязать
 * ЛКМ по плите — удалить
 */
public class PlateSetup implements Listener {

    private final TourGoldPlate plugin;
    private final ConfigManager config;

    public PlateSetup(TourGoldPlate plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        // Игнорируем событие для второй руки (дублируется Bukkit'ом)
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();

        // Только с золотой лопатой
        if (player.getInventory().getItemInMainHand().getType() != Material.GOLDEN_SHOVEL) return;

        // Только с правами
        if (!player.hasPermission("tourgoldplate.setup")) {
            player.sendMessage(Component.text("§c[GP] У вас нет прав для настройки плиты."));
            return;
        }

        Block clicked = event.getClickedBlock();
        if (clicked == null) return;

        // Только по плитам
        if (!Tag.PRESSURE_PLATES.isTagged(clicked.getType())) return;

        event.setCancelled(true);

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            bindPlate(player, clicked);
        } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            removePlate(player, clicked);
        }
    }

    private void bindPlate(Player player, Block block) {
        if (config.getPlateLocation() == null) {
            player.sendMessage(Component.text("§a[GP] Плита привязана!"));
        } else if (config.getPlateLocation().getBlock().equals(block)) {
            player.sendMessage(Component.text("§6[GP] Эта плита уже привязана."));
            return;
        } else {
            player.sendMessage(Component.text("§6[GP] Плита перепривязана!"));
        }

        config.setPlateLocation(block.getLocation());

        // Проверяем конфиг и включаем если всё ок
        if (config.validate()) {
            config.setEnabled(true);
            plugin.getPlateManager().start();
            player.sendMessage(Component.text("§a[GP] Конфиг валиден, плита включена."));
        } else {
            player.sendMessage(Component.text("§c[GP] Конфиг содержит ошибки. Смотрите консоль."));
        }
    }

    private void removePlate(Player player, Block block) {
        if (config.getPlateLocation() == null) {
            player.sendMessage(Component.text("§6[GP] Нет привязанной плиты."));
            return;
        }

        if (!config.getPlateLocation().getBlock().equals(block)) {
            player.sendMessage(Component.text("§6[GP] Эта плита не привязана."));
            return;
        }

        plugin.getPlateManager().stop();
        config.setPlateLocation(null);
        config.setEnabled(false);
        player.sendMessage(Component.text("§a[GP] Плита удалена."));
    }
}
