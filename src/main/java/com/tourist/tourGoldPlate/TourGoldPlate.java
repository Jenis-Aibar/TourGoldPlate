package com.tourist.tourGoldPlate;

import com.tourist.tourGoldPlate.commands.GpCommand;
import com.tourist.tourGoldPlate.config.ConfigManager;
import com.tourist.tourGoldPlate.listeners.ControlUser;
import com.tourist.tourGoldPlate.listeners.PlateSetup;
import com.tourist.tourGoldPlate.listeners.PlayerQuit;
import com.tourist.tourGoldPlate.plate.PlateData;
import com.tourist.tourGoldPlate.tab.TabComplete;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class TourGoldPlate extends JavaPlugin {

    private Economy economy;
    public final Logger logger;
    private final PlateData plateData = new PlateData ();
    private ControlUser controlUser;
    private ConfigManager configManager;

    public TourGoldPlate () {
        logger = getServer().getLogger();
    }

    @Override
    public void onEnable() {
        // Initialize Config
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        // Config
        configManager = new ConfigManager(this);
        controlUser = new ControlUser(this);

        // Load Plate on Enable
        configManager.loadPlate();

        // Commands and Tab
        getCommand("tourgoldplate").setExecutor(new GpCommand(this));
        getCommand("tourgoldplate").setTabCompleter(new TabComplete());
        logger.info("[TourGoldPlate] Плагин включен! UwU");

        // Listeners
        getServer().getPluginManager().registerEvents(new PlateSetup (this), this);
        getServer().getPluginManager().registerEvents(controlUser, this);
        getServer().getPluginManager().registerEvents(new PlayerQuit(this), this);

        // Vault API
        RegisteredServiceProvider<Economy> rsp =
                getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) {
            getLogger().severe("Vault не найден!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        economy = rsp.getProvider();
    }

    @Override
    public void onDisable() {
        configManager.savePlate();

        logger.info("&b[TourGoldPlate]&c Плагин выключен!");
    }

    // GETTER`s
    public PlateData getPlateData () {
        return plateData;
    }
    public ConfigManager getConfigManager () {
        return configManager;
    }
    public ControlUser getControlUser () {
        return controlUser;
    }
    public Economy getEconomy() {
        return economy;
    }
}
