package net.ensdaireplugin.ensdaire.arena;

import net.ensdaireplugin.ensdaire.EnsDaire;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class ArenaManager {
    private final EnsDaire plugin;
    private final Map<String, Arena> arenas = new HashMap<>();

    public ArenaManager(EnsDaire plugin) {
        this.plugin = plugin;
    }

    public void loadArenas() {
        arenas.clear();
        File folder = new File(plugin.getDataFolder(), "arenas");
        if (!folder.exists()) folder.mkdirs();

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            String id = file.getName().replace(".yml", "");
            Arena arena = new Arena(plugin, id);
            
            if (config.contains("lobby")) arena.setLobbySpawn(deserializeLoc(config.getString("lobby")));
            if (config.contains("spectator")) arena.setSpectatorSpawn(deserializeLoc(config.getString("spectator")));
            if (config.contains("capsules")) {
                for (String s : config.getStringList("capsules")) {
                    Location l = deserializeLoc(s);
                    if (l != null) arena.getCapsuleSpawns().add(l);
                }
            }
            if (config.contains("shulkers")) {
                for (String s : config.getStringList("shulkers")) {
                    Location l = deserializeLoc(s);
                    if (l != null) arena.getShulkerSpawns().add(l);
                }
            }
            arenas.put(id, arena);
        }
    }

    public void saveArenas() {
        File folder = new File(plugin.getDataFolder(), "arenas");
        for (Arena arena : arenas.values()) {
            File file = new File(folder, arena.getId() + ".yml");
            FileConfiguration config = new YamlConfiguration();
            
            if (arena.getLobbySpawn() != null) config.set("lobby", serializeLoc(arena.getLobbySpawn()));
            if (arena.getSpectatorSpawn() != null) config.set("spectator", serializeLoc(arena.getSpectatorSpawn()));
            
            List<String> caps = new ArrayList<>();
            for (Location l : arena.getCapsuleSpawns()) caps.add(serializeLoc(l));
            config.set("capsules", caps);

            List<String> shulks = new ArrayList<>();
            for (Location l : arena.getShulkerSpawns()) shulks.add(serializeLoc(l));
            config.set("shulkers", shulks);

            try { config.save(file); } catch (Exception ignored) {}
        }
    }

    public void createArena(String id) {
        if (!arenas.containsKey(id)) {
            arenas.put(id, new Arena(plugin, id));
            saveArenas();
        }
    }

    public Arena get(String id) { return arenas.get(id); }
    public Arena getByPlayer(UUID uuid) {
        for (Arena a : arenas.values()) { if (a.hasPlayer(uuid)) return a; }
        return null;
    }
    public Collection<Arena> all() { return arenas.values(); }
    public int getArenaCount() { return arenas.size(); }
    public void shutdownAll() { arenas.values().forEach(Arena::stopAllTasks); }

    private String serializeLoc(Location l) {
        return l.getWorld().getName() + "," + l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch();
    }

    private Location deserializeLoc(String s) {
        if (s == null) return null;
        String[] p = s.split(",");
        try {
            World w = Bukkit.getWorld(p[0]);
            if (w == null) return null;
            return new Location(w, Double.parseDouble(p[1]), Double.parseDouble(p[2]), Double.parseDouble(p[3]), 
                p.length > 4 ? Float.parseFloat(p[4]) : 0, p.length > 5 ? Float.parseFloat(p[5]) : 0);
        } catch (Exception e) { return null; }
    }
}
