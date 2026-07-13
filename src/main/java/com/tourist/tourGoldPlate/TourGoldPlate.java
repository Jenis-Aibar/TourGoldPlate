package com.tourist.tourGoldPlate;

import com.tourist.tourGoldPlate.command.GpCommand;
import com.tourist.tourGoldPlate.command.TabComplete;
import com.tourist.tourGoldPlate.config.ConfigManager;
import com.tourist.tourGoldPlate.config.MessagesManager;
import com.tourist.tourGoldPlate.listener.PlateListener;
import com.tourist.tourGoldPlate.listener.PlateSetup;
import com.tourist.tourGoldPlate.listener.PlayerQuitListener;
import com.tourist.tourGoldPlate.manager.BossBarManager;
import com.tourist.tourGoldPlate.manager.HologramManager;
import com.tourist.tourGoldPlate.manager.PlateManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class TourGoldPlate extends JavaPlugin {

    private Economy economy;
    private ConfigManager configManager;
    private MessagesManager messagesManager;
    private PlateManager plateManager;
    private HologramManager hologramManager;
    private BossBarManager bossBarManager;

    @Override
    public void onEnable() {
        // --- Конфиги ---
        saveDefaultConfig();
        saveResource("messages.yml", false);

        messagesManager = new MessagesManager(this);
        configManager = new ConfigManager(this);
        configManager.load();

        // --- Vault ---
        if (!setupEconomy()) {
            getLogger().severe("Vault или плагин экономики не найден! Плагин отключается.");
            printBanner(false);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // --- Менеджеры ---
        bossBarManager = new BossBarManager(this);
        hologramManager = new HologramManager(this);
        plateManager = new PlateManager(this);

        // --- Команды ---
        getCommand("tourgoldplate").setExecutor(new GpCommand(this));
        getCommand("tourgoldplate").setTabCompleter(new TabComplete());

        // --- Слушатели ---
        getServer().getPluginManager().registerEvents(new PlateSetup(this), this);
        getServer().getPluginManager().registerEvents(new PlateListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);

        // --- Запуск если конфиг валиден ---
        if (configManager.isValid()) {
            plateManager.start();
        }

        printBanner(true);
    }

    @Override
    public void onDisable() {
        if (plateManager != null) plateManager.stop();
        if (hologramManager != null) hologramManager.removeAll();
        if (bossBarManager != null) bossBarManager.removeAll();
        printBanner(false);
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp =
                getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        economy = rsp.getProvider();
        return true;
    }

    // ─── Геттеры ────────────────────────────────────────────────────────────

    public Economy getEconomy() { return economy; }
    public ConfigManager getConfigManager() { return configManager; }
    public MessagesManager getMessagesManager() { return messagesManager; }
    public PlateManager getPlateManager() { return plateManager; }
    public HologramManager getHologramManager() { return hologramManager; }
    public BossBarManager getBossBarManager() { return bossBarManager; }

    // ─── Баннер ─────────────────────────────────────────────────────────────

    private void printBanner(boolean enabled) {
        String v = getPluginMeta().getVersion();
        getLogger().info(" ");
        getLogger().info("  ██████╗  ██████╗ ██╗     ██████╗     ██████╗ ██╗      █████╗ ████████╗███████╗");
        getLogger().info(" ██╔════╝ ██╔═══██╗██║     ██╔══██╗    ██╔══██╗██║     ██╔══██╗╚══██╔══╝██╔════╝");
        getLogger().info(" ██║  ███╗██║   ██║██║     ██║  ██║    ██████╔╝██║     ███████║   ██║   █████╗  ");
        getLogger().info(" ██║   ██║██║   ██║██║     ██║  ██║    ██╔═══╝ ██║     ██╔══██║   ██║   ██╔══╝  ");
        getLogger().info(" ╚██████╔╝╚██████╔╝███████╗██████╔╝    ██║     ███████╗██║  ██║   ██║   ███████╗");
        getLogger().info("  ╚═════╝  ╚═════╝ ╚══════╝╚═════╝     ╚═╝     ╚══════╝╚═╝  ╚═╝   ╚═╝   ╚══════╝");
        getLogger().info(" ");
        getLogger().info("  TourGoldPlate v" + v + " — by tourist__");
        getLogger().info(" ");
        if (enabled) {
            getLogger().info("  Статус: ВКЛЮЧЁН ✔");
        } else {
            getLogger().info("  Статус: ВЫКЛЮЧЕН ✖");
        }
        getLogger().info(" ");
    }
}
