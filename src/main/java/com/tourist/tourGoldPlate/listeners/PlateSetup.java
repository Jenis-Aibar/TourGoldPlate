package com.tourist.tourGoldPlate.listeners;

import com.tourist.tourGoldPlate.TourGoldPlate;
import com.tourist.tourGoldPlate.plate.PlateData;
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

public class PlateSetup implements Listener {
    private final PlateData plateData;

    public PlateSetup (TourGoldPlate plugin) {
        this.plateData = plugin.getPlateData();
    }

    @EventHandler
    public void onSetup (PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        
        
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (onRightClickBlock (e)) e.setCancelled(true);
        } else if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            if (onLeftClickBlock(e)) e.setCancelled(true);
        }

    }

    private boolean onRightClickBlock (PlayerInteractEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.GOLDEN_SHOVEL) return false;
        Block block = event.getClickedBlock();

        assert block != null;
        if (!Tag.PRESSURE_PLATES.isTagged(block.getType())) return false;

        Player player = event.getPlayer();

        if (plateData.getPlateBlock() == null) player.sendMessage(Component.text("§aПлита привязано!"));
        else player.sendMessage(Component.text("§6Плита перезаписано!"));

        plateData.setPlateBlock(block);
        return true;
    }
    private boolean onLeftClickBlock (PlayerInteractEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.GOLDEN_SHOVEL) return false;

        Block block = event.getClickedBlock();

        assert block != null;
        if (!Tag.PRESSURE_PLATES.isTagged(block.getType())) return false;
        if (!plateData.getPlateBlock().equals(block)) return false;

        plateData.setPlateBlock(null);
        event.getPlayer().sendMessage(Component.text("§aПлита удалена!"));
        return true;
    }

}
