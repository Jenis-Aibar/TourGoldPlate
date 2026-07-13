package com.tourist.tourGoldPlate.manager;

import com.tourist.tourGoldPlate.TourGoldPlate;
import com.tourist.tourGoldPlate.config.ConfigManager;
import com.tourist.tourGoldPlate.util.SoundUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

/**
 * Главный менеджер логики плиты.
 *
 * Отвечает за:
 *  - Запуск/остановку таймера
 *  - Начисление награды каждые tick тиков
 *  - Координацию голограмм и BossBar
 *  - Воспроизведение звука
 *
 * НЕ отвечает за:
 *  - Загрузку конфига (это ConfigManager)
 *  - Отрисовку голограмм (это HologramManager)
 *  - BossBar UI (это BossBarManager)
 */
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

    // ─── Запуск / Остановка ──────────────────────────────────────────────────

    public void start() {
        if (task != null) return; // уже запущен

        int tick = config.getData().tick;

        task = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, tick, tick);
        plugin.getLogger().info("  Таймер плиты запущен (каждые " + tick + " тиков).");
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        // Сбрасываем состояние
        UUID current = config.getCurrentPlayer();
        if (current != null) {
            clearPlayer(current);
        }
        plugin.getLogger().info("  Таймер плиты остановлен.");
    }

    public void restart() {
        stop();
        if (config.isEnabled() && config.isValid()) {
            start();
        }
    }

    // ─── Тик ─────────────────────────────────────────────────────────────────

    /**
     * Вызывается каждые tick тиков.
     * Если на плите стоит игрок — начисляем награду.
     */
    private void tick() {
        if (!config.isEnabled()) return;

        UUID currentUuid = config.getCurrentPlayer();
        if (currentUuid == null) return;

        Player player = Bukkit.getPlayer(currentUuid);
        if (player == null || !player.isOnline()) {
            // Игрок оффлайн — сбрасываем
            clearPlayer(currentUuid);
            return;
        }

        int reward = config.calculateReward();

        // 1. Начислить деньги
        economy.depositPlayer(player, reward);

        // 2. Сообщение в чат
        player.sendMessage(plugin.getMessagesManager().get("reward.received",
                "{amount}", String.valueOf(reward)));

        // 3. Голограмма
        plugin.getHologramManager().addLine(player, "+" + reward + "$");

        // 4. BossBar
        plugin.getBossBarManager().update(player);

        // 5. Звук
        SoundUtil.play(player, config.getData());
    }

    // ─── Игрок встал на плиту ────────────────────────────────────────────────

    /**
     * Вызывается из PlateListener когда игрок заходит на плиту.
     */
    public void onPlayerStep(Player player) {
        UUID current = config.getCurrentPlayer();

        if (current != null) {
            // Плита занята
            Player occupant = Bukkit.getPlayer(current);
            String name = (occupant != null) ? occupant.getName() : "кто-то";
            player.sendMessage(plugin.getMessagesManager().get("plate.occupied",
                    "{player}", name));
            return;
        }

        // Плита свободна
        config.setCurrentPlayer(player.getUniqueId());

        // Показываем BossBar
        plugin.getBossBarManager().show(player);

        // Если таймер не запущен (плита только что включена) — запускаем
        if (task == null && config.isEnabled()) {
            start();
        }
    }

    // ─── Игрок ушёл с плиты ──────────────────────────────────────────────────

    /**
     * Вызывается из PlateListener когда игрок уходит с плиты.
     */
    public void onPlayerLeave(Player player) {
        if (!player.getUniqueId().equals(config.getCurrentPlayer())) return;
        clearPlayer(player.getUniqueId());
    }

    // ─── Очистка игрока ──────────────────────────────────────────────────────

    /**
     * Сбрасывает всё состояние связанное с текущим игроком на плите.
     */
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
