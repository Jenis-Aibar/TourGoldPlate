package com.tourist.tourGoldPlate.config;

import com.tourist.tourGoldPlate.TourGoldPlate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessagesManager {

    private final TourGoldPlate plugin;

    private File file;
    private FileConfiguration messages;

    public MessagesManager(TourGoldPlate plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        file = new File(plugin.getDataFolder(), "messages.yml");

        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        messages = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        messages = YamlConfiguration.loadConfiguration(file);
    }

    public String get(String key, String... placeholders) {
        String text = messages.getString(key, "§c[TGP] Сообщение не найдено: " + key);
        text = text.replace("&", "§");

        for (int i = 0; i + 1 < placeholders.length; i += 2) {
            text = text.replace(placeholders[i], placeholders[i + 1]);
        }

        return text;
    }

    public FileConfiguration getConfig() {
        return messages;
    }
}
