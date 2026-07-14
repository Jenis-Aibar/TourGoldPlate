package com.tourist.tourGoldPlate.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TabComplete implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return filter(args[0], List.of(
                    "help", "reload", "wand", "on", "off",
                    "type", "tick", "config"
            ));
        }

        if (args.length == 2) {
            return switch (args[0].toLowerCase()) {
                case "type"       -> filter(args[1], List.of("fixed", "per-online"));
                case "config"     -> filter(args[1], List.of("reset"));
                default           -> List.of();
            };
        }

        if (args.length == 3) {
            return switch (args[0].toLowerCase()) {
                case "tick" -> List.of("<число>");
                default -> List.of();
            };
        }

        return List.of();
    }

    private List<String> filter(String input, List<String> options) {
        return options.stream()
                .filter(o -> o.toLowerCase().startsWith(input.toLowerCase()))
                .toList();
    }
}
