package org.byauth.manager;

import org.byauth.ByCircleGame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.byauth.EnsDaire;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HologramManager {

    private final EnsDaire plugin;
    private final List<UUID> holograms = new ArrayList<>();

    public HologramManager(EnsDaire plugin) {
        this.plugin = plugin;
    }

    public void createLeaderboard(Location loc, String type) {
        // Simple leaderboard logic:
        // 1. Title
        // 2. Headings
        // 3. Top 5 players

        spawnLine(loc.clone().add(0, 2.5, 0), "&6&lTOP 5 " + type.toUpperCase());
        spawnLine(loc.clone().add(0, 2.2, 0), "&7&m------------------");

        Map<String, Integer> top = plugin.getPlayerDataManager().getTopStats(type, 5);
        int i = 1;
        for (Map.Entry<String, Integer> entry : top.entrySet()) {
            spawnLine(loc.clone().add(0, 2.2 - (i * 0.3), 0),
                    "&e#" + i + " &f" + entry.getKey() + " &7- &a" + entry.getValue());
            i++;
        }
    }

    private void spawnLine(Location loc, String text) {
        ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        as.setVisible(false);
        as.setGravity(false);
        as.setCustomName(ChatColor.translateAlternateColorCodes('&', text));
        as.setCustomNameVisible(true);
        as.setMarker(true);
        holograms.add(as.getUniqueId());
    }

    public void removeAll() {
        for (UUID uuid : holograms) {
            org.bukkit.entity.Entity e = Bukkit.getEntity(uuid);
            if (e != null)
                e.remove();
        }
        holograms.clear();
    }
}
