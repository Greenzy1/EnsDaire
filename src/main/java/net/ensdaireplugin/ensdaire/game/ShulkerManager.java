package net.ensdaireplugin.ensdaire.game;

import net.ensdaireplugin.ensdaire.EnsDaire;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Shulker;

import java.util.ArrayList;
import java.util.List;

public class ShulkerManager {
    private final EnsDaire plugin;
    private final String arenaId;
    private final List<Shulker> activeShulkers = new ArrayList<>();
    private final LootTable lootTable;

    public ShulkerManager(EnsDaire plugin, String arenaId) {
        this.plugin = plugin;
        this.arenaId = arenaId;
        this.lootTable = new LootTable(plugin);
    }

    public void spawnAll(List<Location> spawns) {
        clearAll();
        for (Location loc : spawns) {
            Shulker s = (Shulker) loc.getWorld().spawnEntity(loc, EntityType.SHULKER);
            s.setAI(false);
            activeShulkers.add(s);
        }
    }

    public void clearAll() {
        for (Shulker s : activeShulkers) {
            if (s.isValid()) s.remove();
        }
        activeShulkers.clear();
    }

    public boolean isGameShulker(Shulker s) {
        return activeShulkers.contains(s);
    }

    public LootTable getLootTable() {
        return lootTable;
    }
}
