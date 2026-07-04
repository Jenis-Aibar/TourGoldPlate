package com.tourist.tourGoldPlate.listeners;

import com.tourist.tourGoldPlate.TourGoldPlate;
import com.tourist.tourGoldPlate.config.ConfigManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class ControlUser implements Listener {

    private final TourGoldPlate plugin;
    private final ConfigManager config;
    private BukkitTask rewardTask;

    public ControlUser (TourGoldPlate plugin) {
        this.plugin = plugin;
        config = plugin.getConfigManager();
    }

    @EventHandler
    public void OnPlayerMove (PlayerMoveEvent e) {
        Location from = e.getFrom();
        Location to = e.getTo();

        Block fromBlock = from.clone().getBlock();
        Block toBlock = to.clone().getBlock();

        if (fromBlock.equals(toBlock)) {
            return;
        }

        Block plateBlock = plugin.getPlateData().getPlateBlock();

        if (plateBlock == null) return;

        if (!toBlock.equals(plateBlock)) {
            if (fromBlock.equals(plateBlock)) {
                stopRewardTask();
                plugin.getPlateData().setCurrentPlayer(null);
            }

            return;
        }

        UUID id = plugin.getPlateData().getCurrentPlayer();

        if (id != null) {
            Player currentPlayer = Bukkit.getPlayer(id);

            if (currentPlayer != null) {
                e.getPlayer().sendMessage(Component.text(
                        "§cНа плите стоит " + currentPlayer.getName() + ", столкни его!"
                ));
                return;
            } else {
                plugin.getPlateData().setCurrentPlayer(null);
            }
        }

        Player player = e.getPlayer();

        plugin.getPlateData().setCurrentPlayer(player.getUniqueId());
        startRewardTask(player);

    }

    public void startRewardTask(Player player) {
        if (rewardTask != null) {
            return;
        }

        rewardTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            player.sendMessage(Component.text("§a+" + config.getReward() + "$ за плиту"));
            plugin.getEconomy().depositPlayer(player, config.getReward());

            player.getWorld().playSound(
                    player.getLocation(),
                    Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                    1.0f, // громкость
                    1.0f  // высота (pitch)
            );

        }, 0L, plugin.getConfigManager().getTick());
    }

    public void stopRewardTask() {
        if (rewardTask == null) {
            return;
        }

        rewardTask.cancel();
        rewardTask = null;
    }

}
