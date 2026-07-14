package com.tourist.tourGoldPlate.config;

import com.tourist.tourGoldPlate.TourGoldPlate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConfigManager {

    private final TourGoldPlate plugin;

    private ConfigData data;

    private UUID currentPlayer;

    private boolean valid;

    public ConfigManager(TourGoldPlate plugin) {
        this.plugin = plugin;
        this.data = new ConfigData();
    }

    public void load() {
        var cfg = plugin.getConfig();

        data.enabled           = cfg.getBoolean("enabled", false);
        data.world             = cfg.getString("plate.world", "null");
        data.x                 = cfg.getInt("plate.x", 0);
        data.y                 = cfg.getInt("plate.y", 0);
        data.z                 = cfg.getInt("plate.z", 0);
        data.rewardType        = cfg.getString("reward.type", "FIXED");
        data.tick              = cfg.getInt("reward.tick", 20);
        data.fixedAmount       = cfg.getInt("reward.fixed.amount", 10);
        data.perOnlineBase     = cfg.getInt("reward.per-online.base", 10);
        data.perOnlinePerPlayer = cfg.getInt("reward.per-online.each-player", 5);
        data.soundEnabled      = cfg.getBoolean("sound.enabled", true);
        data.soundName         = cfg.getString("sound.name", "ENTITY_EXPERIENCE_ORB_PICKUP");
        data.soundVolume       = (float) cfg.getDouble("sound.volume", 1.0);
        data.soundPitch        = (float) cfg.getDouble("sound.pitch", 1.0);

        valid = validate();

        if (!valid && data.enabled) {
            data.enabled = false;
            plugin.getConfig().set("enabled", false);
            plugin.saveConfig();
        }
    }

    public boolean validate() {
        List<String> errors = new ArrayList<>();

        // Мир
        if (data.world == null || data.world.equalsIgnoreCase("null") || Bukkit.getWorld(data.world) == null) {
            errors.add("  ✖ plate.world: мир '" + data.world + "' не найден");
        } else {
            var block = Bukkit.getWorld(data.world).getBlockAt(data.x, data.y, data.z);
            if (!Tag.PRESSURE_PLATES.isTagged(block.getType())) {
                errors.add("  ✖ plate.x/y/z: блок на " + data.x + " " + data.y + " " + data.z + " не является плитой");
            }
        }

        // Тип награды
        if (!data.rewardType.equalsIgnoreCase("FIXED") && !data.rewardType.equalsIgnoreCase("PER_ONLINE")) {
            errors.add("  ✖ reward.type: неверное значение '" + data.rewardType + "', допустимо: FIXED, PER_ONLINE");
        }

        // Тик
        if (data.tick < 3) {
            errors.add("  ✖ reward.tick: минимум 3, сейчас " + data.tick);
        }

        if (data.rewardType.equalsIgnoreCase("FIXED") && data.fixedAmount <= 0) {
            errors.add("  ✖ reward.fixed.amount: должно быть больше 0, сейчас " + data.fixedAmount);
        }

        if (data.rewardType.equalsIgnoreCase("PER_ONLINE")) {
            if (data.perOnlineBase < 0) {
                errors.add("  ✖ reward.per-online.base: не может быть отрицательным");
            }
            if (data.perOnlinePerPlayer < 0) {
                errors.add("  ✖ reward.per-online.each-player: не может быть отрицательным");
            }
            if (data.perOnlineBase + data.perOnlinePerPlayer <= 0) {
                errors.add("  ✖ reward.per-online: итоговая сумма (base + players*each) должна быть больше 0");
            }
        }

        if (data.soundEnabled) {
            try {
                Sound.valueOf(data.soundName);
            } catch (IllegalArgumentException e) {
                errors.add("  ✖ sound.name: звук '" + data.soundName + "' не существует в Bukkit");
            }
            if (data.soundVolume <= 0) {
                errors.add("  ✖ sound.volume: должно быть больше 0");
            }
        }

        if (!errors.isEmpty()) {
            plugin.getLogger().warning(" ");
            plugin.getLogger().warning("  ╔══ Ошибки в config.yml ═════════════╗");
            for (String error : errors) {
                plugin.getLogger().warning(error);
            }
            plugin.getLogger().warning("  ╚══ Плита выключена до исправления ══╝");
            plugin.getLogger().warning(" ");
            this.valid = false;
            return false;
        }

        plugin.getLogger().info("  ✔ Конфиг проверен, ошибок нет.");
        this.valid = true;
        return true;
    }

    public boolean reload() {
        plugin.reloadConfig();
        load();

        if (valid && data.enabled) {
            plugin.getPlateManager().start();
        } else {
            plugin.getPlateManager().stop();
        }

        return valid;
    }

    public boolean setEnabled(boolean value) {
        if (value && !valid) return false;
        data.enabled = value;
        plugin.getConfig().set("enabled", value);
        plugin.saveConfig();
        return true;
    }

    public void setPlateLocation(Location location) {
        if (location == null) {
            data.world = "null";
            data.x = 0; data.y = 0; data.z = 0;
        } else {
            data.world = location.getWorld().getName();
            data.x = location.getBlockX();
            data.y = location.getBlockY();
            data.z = location.getBlockZ();
        }
        plugin.getConfig().set("plate.x", data.x);
        plugin.getConfig().set("plate.y", data.y);
        plugin.getConfig().set("plate.z", data.z);
        plugin.getConfig().set("plate.world", data.world);
        plugin.saveConfig();
    }

    public boolean setRewardType(String type) {
        if (!type.equalsIgnoreCase("FIXED") && !type.equalsIgnoreCase("PER_ONLINE")) return false;
        data.rewardType = type.toUpperCase();
        plugin.getConfig().set("reward.type", data.rewardType);
        plugin.saveConfig();
        valid = validate();
        return valid;
    }

    public boolean setTick(int tick) {
        if (tick < 3) return false;
        data.tick = tick;
        plugin.getConfig().set("reward.tick", tick);
        plugin.saveConfig();
        if (valid && data.enabled) {
            plugin.getPlateManager().restart();
        }
        return true;
    }

    public void setCurrentPlayer(UUID uuid) { this.currentPlayer = uuid; }
    public UUID getCurrentPlayer() { return currentPlayer; }

    public ConfigData getData() { return data; }
    public boolean isEnabled() { return data.enabled; }
    public boolean isValid() { return valid; }

    public Location getPlateLocation() {
        if (data.world == null || data.world.equalsIgnoreCase("null")) return null;
        var world = Bukkit.getWorld(data.world);
        if (world == null) return null;
        return new Location(world, data.x, data.y, data.z);
    }

    public int calculateReward() {
        if (data.rewardType.equalsIgnoreCase("FIXED")) {
            return data.fixedAmount;
        }
        return data.perOnlineBase + (Bukkit.getOnlinePlayers().size() * data.perOnlinePerPlayer);
    }
}
