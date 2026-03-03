package net.ensdaireplugin.ensdaire.arena;

import net.ensdaireplugin.ensdaire.EnsDaire;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ArenaManager {

    private final EnsDaire plugin;
    private final Map<String, Arena> arenas = new LinkedHashMap<>();
    private final File arenasFile;
    private FileConfiguration cfg;

    public ArenaManager(EnsDaire plugin) {
        this.plugin = plugin;
        this.arenasFile = new File(plugin.getDataFolder(), "arenas.yml");
    }

    public void loadArenas() {
        if (!arenasFile.exists()) {
            try { arenasFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        cfg = YamlConfiguration.loadConfiguration(arenasFile);

        if (!cfg.isConfigurationSection("arenas")) return;

        for (String id : cfg.getConfigurationSection("arenas").getKeys(false)) {
            Arena a = new Arena(plugin, id);
            String p = "arenas." + id;

            Location lobby = locFromStr(cfg.getString(p + ".lobby"));
            Location spec  = locFromStr(cfg.getString(p + ".spectator"));
            if (lobby != null) a.setLobbySpawn(lobby);
            if (spec  != null) a.setSpectatorSpawn(spec);

            for (String s : cfg.getStringList(p + ".capsules")) {
                Location l = locFromStr(s); if (l != null) a.getCapsuleSpawns().add(l);
            }
            for (String s : cfg.getStringList(p + ".shulker-spawns")) {
                Location l = locFromStr(s); if (l != null) a.getShulkerSpawns().add(l);
            }

            boolean disabled = cfg.getBoolean(p + ".disabled", false);
            if (disabled) a.setState(net.ensdaireplugin.ensdaire.game.GameState.DISABLED);

            arenas.put(id, a);
            plugin.getLogger().info("  Arena yüklendi: " + id);
        }
    }

    public void saveArenas() {
        if (cfg == null) cfg = YamlConfiguration.loadConfiguration(arenasFile);

        for (Arena a : arenas.values()) {
            String p = "arenas." + a.getId();
            if (a.getLobbySpawn()     != null) cfg.set(p + ".lobby",      locToStr(a.getLobbySpawn()));
            if (a.getSpectatorSpawn() != null) cfg.set(p + ".spectator",  locToStr(a.getSpectatorSpawn()));
            cfg.set(p + ".capsules",      a.getCapsuleSpawns().stream().map(this::locToStr).toList());
            cfg.set(p + ".shulker-spawns",a.getShulkerSpawns().stream().map(this::locToStr).toList());
            cfg.set(p + ".disabled", a.getState() == net.ensdaireplugin.ensdaire.game.GameState.DISABLED);
        }

        try { cfg.save(arenasFile); } catch (IOException e) { e.printStackTrace(); }
    }

    public Arena create(String id) {
        Arena a = new Arena(plugin, id);
        arenas.put(id, a);
        saveArenas();
        return a;
    }

    public boolean delete(String id) {
        Arena a = arenas.remove(id);
        if (a == null) return false;
        a.stopAllTasks();
        if (cfg != null) {
            cfg.set("arenas." + id, null);
            try { cfg.save(arenasFile); } catch (IOException e) { e.printStackTrace(); }
        }
        return true;
    }

    public Arena get(String id) { return arenas.get(id); }

    public Arena getByPlayer(UUID uuid) {
        for (Arena a : arenas.values()) if (a.hasPlayer(uuid)) return a;
        return null;
    }

    public Collection<Arena> all() { return arenas.values(); }
    public int getArenaCount() { return arenas.size(); }

    public void shutdownAll() {
        arenas.values().forEach(a -> {
            a.stopAllTasks();
            a.getCircleManager().removeAllCircles();
            a.getShulkerManager().clearAll();
        });
    }

    private String locToStr(Location l) {
        return l.getWorld().getName() + "," + l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch();
    }

    private Location locFromStr(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            String[] p = s.split(",");
            World w = Bukkit.getWorld(p[0]);
            if (w == null) return null;
            return new Location(w, Double.parseDouble(p[1]), Double.parseDouble(p[2]),
                Double.parseDouble(p[3]), p.length > 4 ? Float.parseFloat(p[4]) : 0,
                p.length > 5 ? Float.parseFloat(p[5]) : 0);
        } catch (Exception e) { return null; }
    }
}
