package net.ensis.ensdaire.managers;

import net.ensis.ensdaire.EnsDaire;
import net.ensis.ensdaire.models.Arena;
import net.ensis.ensdaire.models.CircleColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ArenaManager {
    private final EnsDaire plugin;
    private final Map<String, Arena> arenas = new HashMap<>();
    private final File file;
    private FileConfiguration config;

    public ArenaManager(EnsDaire plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "arenas.yml");
        if (!file.exists()) {
            plugin.getDataFolder().mkdirs();
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void createArena(String name) {
        if (arenas.containsKey(name)) return;
        Arena arena = new Arena(plugin, name);
        arenas.put(name, arena);
        saveArenas();
    }

    public void loadArenas() {
        arenas.clear();
        this.config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("arenas");
        if (section == null) return;
        
        for (String id : section.getKeys(false)) {
            Arena arena = new Arena(plugin, id);
            String path = "arenas." + id + ".";
            
            arena.setLobbySpawn(deserializeLoc(config.getString(path + "lobby")));
            arena.setSpectatorSpawn(deserializeLoc(config.getString(path + "spectator")));
            
            ConfigurationSection caps = config.getConfigurationSection(path + "capsule_map");
            if (caps != null) {
                for (String colorName : caps.getKeys(false)) {
                    try {
                        CircleColor color = CircleColor.valueOf(colorName);
                        arena.setCapsuleSpawn(color, deserializeLoc(caps.getString(colorName)));
                    } catch (Exception ignored) {}
                }
            }
            
            arenas.put(id, arena);
        }
        plugin.getLogger().info("✔ " + arenas.size() + " adet arena başarıyla yüklendi.");
    }

    public void saveArenas() {
        config.set("arenas", null);
        for (Arena arena : arenas.values()) {
            String path = "arenas." + arena.getId() + ".";
            config.set(path + "lobby", serializeLoc(arena.getLobbySpawn()));
            config.set(path + "spectator", serializeLoc(arena.getSpectatorSpawn()));
            
            for (CircleColor color : CircleColor.values()) {
                Location loc = arena.getCapsuleSpawn(color);
                if (loc != null) {
                    config.set(path + "capsule_map." + color.name(), serializeLoc(loc));
                }
            }
        }
        try { config.save(file); } catch (IOException e) { e.printStackTrace(); }
    }

    public Arena get(String id) { return arenas.get(id); }
    public Collection<Arena> all() { return arenas.values(); }
    
    public Arena getByPlayer(UUID uuid) {
        for (Arena a : arenas.values()) {
            if (a.hasPlayer(uuid)) return a;
        }
        return null;
    }

    public void shutdownAll() {
        for (Arena a : arenas.values()) a.stopAllTasks();
    }

    private String serializeLoc(Location l) {
        if (l == null) return null;
        return l.getWorld().getName() + "," + l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch();
    }

    private Location deserializeLoc(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            String[] p = s.split(",");
            World w = Bukkit.getWorld(p[0]);
            if (w == null) return null;
            return new Location(w, Double.parseDouble(p[1]), Double.parseDouble(p[2]), Double.parseDouble(p[3]), 
                p.length > 4 ? Float.parseFloat(p[4]) : 0, p.length > 5 ? Float.parseFloat(p[5]) : 0);
        } catch (Exception e) { return null; }
    }
}
