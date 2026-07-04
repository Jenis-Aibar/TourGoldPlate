package com.tourist.tourGoldPlate.config;

import com.tourist.tourGoldPlate.TourGoldPlate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

public class ConfigManager {

    private final TourGoldPlate plugin;
    private static int reward;
    private static int tick;

    public ConfigManager (TourGoldPlate plugin) {
        this.plugin = plugin;
        reload();
    }

    // GETTER`s
    public int getReward () {
        return reward;
    }
    public int getTick () {
        return tick;
    }
    //

    public void load () {
        reward = plugin.getConfig().getInt("reward-amount", 0);
        tick = plugin.getConfig().getInt("reward-tick", 30);

        boolean changed = false;

        if (reward < 0) {
            plugin.getConfig().set("reward-amount", 0);
            changed = true;
        }

        if (tick < 5) {
            plugin.getConfig().set("reward-tick", 5);
            changed = true;
        }

        if (changed) {
            plugin.saveConfig();
            reload ();
        }
    }

    public void reload () {
        plugin.reloadConfig();
        load ();
    }

    public void savePlate () {
        Block plateBlock = plugin.getPlateData().getPlateBlock();
        if (plateBlock == null) return;


        String world = plateBlock.getWorld().getName();
        int x, y, z;

        x = plateBlock.getX();
        y = plateBlock.getY();
        z = plateBlock.getZ();

        plugin.getConfig().set("plateBlock.world", world);
        plugin.getConfig().set("plateBlock.x", x);
        plugin.getConfig().set("plateBlock.y", y);
        plugin.getConfig().set("plateBlock.z", z);

        plugin.saveConfig();
    }

    public void loadPlate () {
        String worldS = plugin.getConfig().getString("plateBlock.world");

        if (worldS == null) return;

        World world = Bukkit.getWorld(worldS);

        if (world == null) return;

        int x, y, z;

        x = plugin.getConfig().getInt("plateBlock.x");
        y = plugin.getConfig().getInt("plateBlock.y");
        z = plugin.getConfig().getInt("plateBlock.z");

        Block block = world.getBlockAt (x, y, z);
        plugin.getPlateData().setPlateBlock(block);
    }

}
