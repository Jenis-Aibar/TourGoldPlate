package com.tourist.tourGoldPlate.manager;

import com.tourist.tourGoldPlate.TourGoldPlate;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Управляет голограммами над плитой через DecentHolograms.
 * Если DecentHolograms не установлен — методы просто ничего не делают.
 *
 * Логика «ленты»:
 *  - Максимум 4 строки одновременно
 *  - Каждый тик добавляется новая строка снизу
 *  - Самая верхняя (4-я) удаляется
 *  - При уходе игрока строки убираются одна за другой
 *
 * Структура голограмм:
 *  [+10$]  ← 3 тика назад (самая верхняя)
 *  [+10$]  ← 2 тика назад
 *  [+10$]  ← 1 тик назад
 *  [+10$]  ← только что (прямо над головой)
 *  [ИГРОК]
 */
public class HologramManager {

    private static final int MAX_LINES = 4;
    private static final double LINE_HEIGHT = 0.35; // расстояние между строками
    private static final double BASE_HEIGHT = 2.4;  // высота над плитой (над головой)

    private final TourGoldPlate plugin;
    private final boolean enabled;

    // UUID игрока → список его голограмм (от самой новой к старой)
    private final Map<UUID, Deque<Hologram>> playerHolograms = new HashMap<>();

    // Счётчик для уникальных имён голограмм
    private int counter = 0;

    public HologramManager(TourGoldPlate plugin) {
        this.plugin = plugin;
        this.enabled = isDecentHologramsAvailable();

        if (enabled) {
            plugin.getLogger().info("  DecentHolograms найден, голограммы включены.");
        } else {
            plugin.getLogger().info("  DecentHolograms не найден, голограммы выключены.");
        }
    }

    // ─── Проверка наличия плагина ────────────────────────────────────────────

    private boolean isDecentHologramsAvailable() {
        try {
            Class.forName("eu.decentsoftware.holograms.api.DHAPI");
            return plugin.getServer().getPluginManager().getPlugin("DecentHolograms") != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    // ─── Добавить строку (вызывается каждый тик) ─────────────────────────────

    /**
     * Добавляет новую строку внизу ленты.
     * Если строк уже 4 — удаляет самую верхнюю.
     */
    public void addLine(Player player, String text) {
        if (!enabled) return;

        Location plateLoc = plugin.getConfigManager().getPlateLocation();
        if (plateLoc == null) return;

        Deque<Hologram> lines = playerHolograms.computeIfAbsent(
                player.getUniqueId(), k -> new ArrayDeque<>());

        // Удаляем старую если достигли лимита
        if (lines.size() >= MAX_LINES) {
            Hologram oldest = lines.pollLast(); // самая верхняя
            if (oldest != null) oldest.delete();
        }

        // Создаём новую голограмму снизу (BASE_HEIGHT)
        Location spawnLoc = plateLoc.clone().add(0.5, BASE_HEIGHT, 0.5);
        String holoName = "tgp_" + player.getUniqueId().toString().substring(0, 8) + "_" + (counter++);

        Hologram holo = DHAPI.createHologram(holoName, spawnLoc);
        DHAPI.addHologramLine(holo, text);

        lines.addFirst(holo); // новая — в начало (снизу)

        // Сдвигаем все существующие вверх
        repositionLines(lines, plateLoc);
    }

    // ─── Убрать все строки ───────────────────────────────────────────────────

    public void clearLines(Player player) {
        clearLinesByUuid(player.getUniqueId());
    }

    public void clearLinesByUuid(UUID uuid) {
        if (!enabled) return;
        Deque<Hologram> lines = playerHolograms.remove(uuid);
        if (lines == null) return;
        for (Hologram holo : lines) {
            holo.delete();
        }
    }

    // ─── Убрать все голограммы (при отключении плагина) ─────────────────────

    public void removeAll() {
        if (!enabled) return;
        for (Deque<Hologram> lines : playerHolograms.values()) {
            for (Hologram holo : lines) {
                holo.delete();
            }
        }
        playerHolograms.clear();
    }

    // ─── Вспомогательные ─────────────────────────────────────────────────────

    /**
     * Пересчитывает позиции всех строк.
     * Нулевая (новая) — на BASE_HEIGHT, каждая следующая выше на LINE_HEIGHT.
     */
    private void repositionLines(Deque<Hologram> lines, Location base) {
        Location plateLoc = base.clone().add(0.5, 0, 0.5);
        int index = 0;
        for (Hologram holo : lines) {
            double y = plateLoc.getY() + BASE_HEIGHT + (index * LINE_HEIGHT);
            Location newLoc = new Location(plateLoc.getWorld(), plateLoc.getX(), y, plateLoc.getZ());
            DHAPI.moveHologram(holo, newLoc);
            index++;
        }
    }
}
