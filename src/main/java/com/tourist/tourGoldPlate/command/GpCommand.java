package com.tourist.tourGoldPlate.command;

import com.tourist.tourGoldPlate.TourGoldPlate;
import com.tourist.tourGoldPlate.config.ConfigManager;
import com.tourist.tourGoldPlate.config.MessagesManager;
import com.tourist.tourGoldPlate.manager.PlateManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GpCommand implements CommandExecutor {

    private final TourGoldPlate plugin;
    private final ConfigManager config;
    private final PlateManager plate;
    private final MessagesManager msg;
    private final String version;

    public GpCommand(TourGoldPlate plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.version = plugin.getPluginMeta().getVersion();
        this.msg = plugin.getMessagesManager();
        this.plate = plugin.getPlateManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cЭту команду может использовать только игрок.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Component.text("§cНеизвестная команда. §6/gp help"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help"   -> cmdHelp(player);
            case "reload" -> cmdReload(player);
            case "wand"   -> cmdWand(player);
            case "on"     -> cmdOn(player);
            case "off"    -> cmdOff(player);
            case "type"   -> cmdType(player, args);
            case "tick"   -> cmdTick(player, args);
            case "config" -> cmdConfig(player, args);
            default -> player.sendMessage(Component.text("§cНеизвестная команда. §6/gp help"));
        }

        return true;
    }

    // ─── Подкоманды ──────────────────────────────────────────────────────────

    private void cmdHelp(Player player) {
        player.sendMessage(Component.text(
                "\n§6┌── TourGoldPlate v" + version + " ──┐\n" +
                "§6│ §f/gp help §7— эта справка\n" +
                "§6│ §f/gp reload §7— перезагрузить конфиг\n" +
                "§6│ §f/gp wand §7— инструмент привязки плиты\n" +
                "§6│ §f/gp on/off §7— включить/выключить плиту\n" +
                "§6│ §f/gp type <fixed|per-online> §7— режим награды\n" +
                "§6│ §f/gp tick <число> §7— интервал начислений (тики)\n" +
                "§6│ §f/gp config reset §7— сбросить конфиг\n" +
                "§6└──────────────────────┘"
        ));
    }

    private void cmdReload(Player player) {
        player.sendMessage(Component.text("§7Перезагрузка конфига..."));
        msg.reload();
        boolean valid = config.reload();
        plate.restart();
        if (valid) {
            player.sendMessage(Component.text("§aКонфиг перезагружен и валиден. Плита работает."));
        } else {
            player.sendMessage(Component.text("§cКонфиг содержит ошибки. Смотрите консоль. Плита выключена."));
        }
    }

    private void cmdWand(Player player) {
        player.getInventory().addItem(ItemStack.of(Material.GOLDEN_SHOVEL));
        player.sendMessage(Component.text(
                "§b[GP] Инструмент получен!\n" +
                "§6    ПКМ §bпо плите §a— привязать\n" +
                "§6    ЛКМ §bпо плите §c— удалить"
        ));
    }

    private void cmdOn(Player player) {
        if (config.isEnabled()) {
            player.sendMessage(Component.text("§6Плита уже включена."));
            return;
        }
        if (!config.isValid()) {
            player.sendMessage(Component.text("§cКонфиг невалиден, плита не может быть включена."));
            return;
        }
        config.setEnabled(true);
        plugin.getPlateManager().start();
        player.sendMessage(Component.text("§aПлита включена!"));
    }

    private void cmdOff(Player player) {
        if (!config.isEnabled()) {
            player.sendMessage(Component.text("§6Плита уже выключена."));
            return;
        }
        config.setEnabled(false);
        plugin.getPlateManager().stop();
        player.sendMessage(Component.text("§cПлита выключена."));
    }

    private void cmdType(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Component.text("§cИспользование: /gp type <fixed|per-online>"));
            return;
        }
        String type = args[1].toLowerCase();
        if (!type.equals("fixed") && !type.equals("per-online")) {
            player.sendMessage(Component.text("§cНеверный тип. Доступно: §ffixed§c, §fper-online"));
            return;
        }
        config.setRewardType(type);
        player.sendMessage(Component.text("§aТип награды изменён на §f" + type.toUpperCase()));
        if (!config.isValid()) {
            player.sendMessage(Component.text("§eВнимание: конфиг содержит ошибки для этого типа. Смотрите консоль."));
        }
    }

    private void cmdTick(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Component.text("§cИспользование: /gp tick <число> (минимум 3)"));
            return;
        }
        int tick = parsePositiveInt(player, args[1], 3);
        if (tick < 0) return;

        if (config.setTick(tick)) {
            player.sendMessage(Component.text("§aТик изменён на §f" + tick));
        } else {
            player.sendMessage(Component.text("§cТик должен быть минимум 3."));
        }
    }

    private void cmdConfig(Player player, String[] args) {
        if (args.length < 2 || !args[1].equalsIgnoreCase("reset")) {
            player.sendMessage(Component.text("§cИспользование: /gp config reset"));
            return;
        }
        plugin.getPlateManager().stop();
        var configFile = new java.io.File(plugin.getDataFolder(), "config.yml");
        configFile.delete();
        plugin.saveDefaultConfig();
        config.reload();
        player.sendMessage(Component.text("§aКонфиг сброшен до дефолтного."));
    }

    private int parsePositiveInt(Player player, String input, int min) {
        try {
            int value = Integer.parseInt(input);
            if (value < min) {
                player.sendMessage(Component.text("§cЗначение должно быть минимум " + min + "."));
                return -1;
            }
            return value;
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("§c'" + input + "' не является числом."));
            return -1;
        }
    }
}
