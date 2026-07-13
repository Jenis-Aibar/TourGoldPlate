package com.tourist.tourGoldPlate.manager;

import com.tourist.tourGoldPlate.TourGoldPlate;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Управляет BossBar для игрока стоящего на плите.
 *
 * BossBar показывает:
 *  - Название ивента
 *  - Имя игрока стоящего на плите
 *  - Прогресс (всегда полный — это ивент, не HP)
 *
 * Показывается только тому кто стоит на плите.
 * Остальные видят другой текст (что плита занята).
 */
public class BossBarManager {

    private final TourGoldPlate plugin;

    // UUID → его BossBar
    private final Map<UUID, BossBar> bars = new HashMap<>();

    public BossBarManager(TourGoldPlate plugin) {
        this.plugin = plugin;
    }

    // ─── Показать BossBar игроку ─────────────────────────────────────────────

    public void show(Player player) {
        // Если уже есть — обновляем
        if (bars.containsKey(player.getUniqueId())) {
            update(player);
            return;
        }

        BossBar bar = BossBar.bossBar(
                buildTitle(player.getName()),
                1.0f,
                BossBar.Color.YELLOW,
                BossBar.Overlay.NOTCHED_10
        );

        bars.put(player.getUniqueId(), bar);
        player.showBossBar(bar);
    }

    // ─── Обновить текст ──────────────────────────────────────────────────────

    public void update(Player player) {
        BossBar bar = bars.get(player.getUniqueId());
        if (bar == null) return;
        bar.name(buildTitle(player.getName()));
    }

    // ─── Скрыть и удалить ────────────────────────────────────────────────────

    public void hide(Player player) {
        hideByUuid(player.getUniqueId());
    }

    public void hideByUuid(UUID uuid) {
        BossBar bar = bars.remove(uuid);
        if (bar == null) return;

        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.hideBossBar(bar);
        }
    }

    public void removeAll() {
        for (Map.Entry<UUID, BossBar> entry : bars.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null) {
                player.hideBossBar(entry.getValue());
            }
        }
        bars.clear();
    }

    // ─── Построить заголовок ─────────────────────────────────────────────────

    private Component buildTitle(String playerName) {
        return Component.text("⚡ ", NamedTextColor.YELLOW)
                .append(Component.text("Золотая Плита", NamedTextColor.GOLD, TextDecoration.BOLD))
                .append(Component.text(" — Занят: ", NamedTextColor.GRAY))
                .append(Component.text(playerName, NamedTextColor.YELLOW));
    }
}
