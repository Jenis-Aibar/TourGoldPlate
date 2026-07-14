package com.tourist.tourGoldPlate.manager;

import com.tourist.tourGoldPlate.TourGoldPlate;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class HologramManager {

    private static final int MAX_LINES = 4;
    private static final double LINE_HEIGHT = 0.35;
    private static final double BASE_HEIGHT = 2.4;

    private final TourGoldPlate plugin;
    private final boolean enabled;

    private final Map<UUID, Deque<Hologram>> playerHolograms = new HashMap<>();

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

    private boolean isDecentHologramsAvailable() {
        try {
            Class.forName("eu.decentsoftware.holograms.api.DHAPI");
            return plugin.getServer().getPluginManager().getPlugin("DecentHolograms") != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public void addLine(Player player, String text) {
        if (!enabled) return;

        Location plateLoc = plugin.getConfigManager().getPlateLocation();
        if (plateLoc == null) return;

        Deque<Hologram> lines = playerHolograms.computeIfAbsent(
                player.getUniqueId(), k -> new ArrayDeque<>());

        if (lines.size() >= MAX_LINES) {
            Hologram oldest = lines.pollLast(); // самая верхняя
            if (oldest != null) oldest.delete();
        }

        Location spawnLoc = plateLoc.clone().add(0.5, BASE_HEIGHT, 0.5);
        String holoName = "tgp_" + player.getUniqueId().toString().substring(0, 8) + "_" + (counter++);

        Hologram holo = DHAPI.createHologram(holoName, spawnLoc);
        DHAPI.addHologramLine(holo, text);

        lines.addFirst(holo);

        repositionLines(lines, plateLoc);
    }

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
    public void removeAll() {
        if (!enabled) return;
        for (Deque<Hologram> lines : playerHolograms.values()) {
            for (Hologram holo : lines) {
                holo.delete();
            }
        }
        playerHolograms.clear();
    }
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
