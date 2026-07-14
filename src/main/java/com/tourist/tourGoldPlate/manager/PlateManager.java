package com.tourist.tourGoldPlate.manager;

import com.tourist.tourGoldPlate.TourGoldPlate;
import com.tourist.tourGoldPlate.config.ConfigManager;
import com.tourist.tourGoldPlate.util.SoundUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class PlateManager {

    private final TourGoldPlate plugin;
    private final ConfigManager config;
    private final Economy economy;

    private BukkitTask task;

    public PlateManager(TourGoldPlate plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.economy = plugin.getEconomy();
    }

    public boolean start() {
        if (task != null) return false;

        int tick = config.getData().tick;

        task = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, tick, tick);

        return true;
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        UUID current = config.getCurrentPlayer();
        if (current != null) {
            clearPlayer(current);
        }
    }

    public void restart() {
        stop();

        if (!config.isEnabled() || !config.isValid()) {
            return;
        }

        start();
        findPlayerOnPlate();
    }

    private void findPlayerOnPlate() {
        Location plate = config.getPlateLocation();

        if (plate == null) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getLocation().getBlock().equals(plate.getBlock())) {
                config.setCurrentPlayer(player.getUniqueId());
                return;
            }
        }

        config.setCurrentPlayer(null);
    }

    private void tick() {
        if (!config.isEnabled()) return;

        UUID currentUuid = config.getCurrentPlayer();
        if (currentUuid == null) return;

        Player player = Bukkit.getPlayer(currentUuid);
        if (player == null || !player.isOnline()) {
            clearPlayer(currentUuid);
            return;
        }

        int reward = config.calculateReward();

        economy.depositPlayer(player, reward);

        player.sendMessage(plugin.getMessagesManager().get("reward.received",
                "{amount}", String.valueOf(reward)));

        plugin.getHologramManager().addLine(player, "+" + reward + "$");

        plugin.getBossBarManager().update(player);

        SoundUtil.play(player, config.getData());
    }

    public void onPlayerStep(Player player) {
        UUID current = config.getCurrentPlayer();

        if (current != null) {
            Player occupant = Bukkit.getPlayer(current);
            String name = (occupant != null) ? occupant.getName() : "кто-то";
            player.sendMessage(plugin.getMessagesManager().get("plate.occupied",
                    "{player}", name));
            return;
        }

        config.setCurrentPlayer(player.getUniqueId());

        plugin.getBossBarManager().show(player);

        if (task == null && config.isEnabled()) {
            start();
        }
    }
    public void onPlayerLeave(Player player) {
        if (!player.getUniqueId().equals(config.getCurrentPlayer())) return;
        clearPlayer(player.getUniqueId());
    }

    public void clearPlayer(UUID uuid) {
        config.setCurrentPlayer(null);

        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            plugin.getHologramManager().clearLines(player);
            plugin.getBossBarManager().hide(player);
        } else {
            plugin.getHologramManager().clearLinesByUuid(uuid);
            plugin.getBossBarManager().hideByUuid(uuid);
        }
    }
}
