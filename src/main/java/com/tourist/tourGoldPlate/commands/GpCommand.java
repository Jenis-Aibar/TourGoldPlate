package com.tourist.tourGoldPlate.commands;

import com.tourist.tourGoldPlate.TourGoldPlate;
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
    String version;

    public GpCommand (TourGoldPlate plugin) {
        this.plugin = plugin;
        version = plugin.getPluginMeta().getVersion();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!(commandSender instanceof Player player)) {
            plugin.logger.info("§cЭту команду может использовать только игрок!");
            return false;
        }

        if (strings.length < 1) {
            player.sendMessage(Component.text("§cНеизвестная команда. Напишите §6/gp help"));
            return true;
        }

        if (strings.length == 1) {
            switch (strings[0].toLowerCase()) {
                case "help":
                    onHelpCommand (player);
                    break;
                case "wand":
                    onWandCommand (player);
                    break;
                case "reload":
                    onReloadCommand (player);
                    break;
            }
        }
        return true;
    }

    private void onHelpCommand (Player player) {
        player.sendMessage(Component.text("§fПомощь по плагину §6TourGoldPlate v" + version +
                "\n§6> gp help - §fПомощь по командам\n" +
                "§6> gp reload - §fПерезагрузить конфиг\n" +
                "§6> gp wand - §fИнструмент привязки плиты\n"));
    }
    private void onWandCommand (Player player) {
        player.getInventory().addItem(ItemStack.of(Material.GOLDEN_SHOVEL));
        player.sendMessage(Component.text("§bЛКМ по плите - §cудалить плиту\n§bПКМ по плите - §aпривязать плиту"));
    }
    private void onReloadCommand (Player player) {
        plugin.getConfigManager ().reload();
        plugin.getControlUser().stopRewardTask();

        player.sendMessage(Component.text("§aКонфиг успешно перезагружен!"));
    }
}
