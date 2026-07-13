package com.tourist.tourGoldPlate.config;

import com.tourist.tourGoldPlate.TourGoldPlate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * Загружает messages.yml и отдаёт сообщения по ключу.
 * Все тексты — только здесь, нигде в коде не должно быть хардкода сообщений.
 *
 * Плейсхолдеры: {player}, {amount}, {reward}, {tick} и т.д.
 * Заменяются через метод format().
 */
public class MessagesManager {

    private final TourGoldPlate plugin;
    private FileConfiguration messages;

    public MessagesManager(TourGoldPlate plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        messages = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Получить сообщение по ключу с заменой цветовых кодов.
     * Пример: get("plate.occupied", "{player}", "Steve")
     */
    public String get(String key, String... placeholders) {
        String text = messages.getString(key, "§c[TGP] Сообщение не найдено: " + key);
        text = text.replace("&", "§");

        // Заменяем плейсхолдеры попарно: {ключ}, значение
        for (int i = 0; i + 1 < placeholders.length; i += 2) {
            text = text.replace(placeholders[i], placeholders[i + 1]);
        }
        return text;
    }
}
