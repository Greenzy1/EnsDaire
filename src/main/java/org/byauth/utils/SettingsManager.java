package org.byauth.utils;

import org.byauth.ByCircleGame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Location;

import java.io.File;
import java.util.*;

public class SettingsManager {

    private final ByCircleGame plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacySection();
    private FileConfiguration config;
    private final Map<String, FileConfiguration> menus = new HashMap<>();
    private final Map<String, FileConfiguration> languages = new HashMap<>();
    private final Map<String, FileConfiguration> arenas = new HashMap<>();

    public static String PREFIX;
    public String ACTIVE_LANGUAGE = "tr";
    public String COIN_SYSTEM_NAME = "BCoin";

    // Global Game Settings
    public int MIN_PLAYERS_TO_START;
    public int LOBBY_COUNTDOWN_SECONDS;
    public int ROUND_DURATION_SECONDS;

    // Default Arena Settings
    public int ARENA_RADIUS;
    public int ARENA_Y_LEVEL;
    public int LAVA_Y_LEVEL;

    // Team Selector Settings
    public int TEAM_SELECTOR_SLOT;
    public String TEAM_SELECTOR_NAME;
    public org.bukkit.Material TEAM_SELECTOR_MATERIAL;

    public int LEAVE_LOBBY_SLOT;
    public String LEAVE_LOBBY_NAME;
    public org.bukkit.Material LEAVE_LOBBY_MATERIAL;

    public SettingsManager(ByCircleGame plugin) {
        this.plugin = plugin;
        loadAllConfigs();
    }

    public void reload() {
        plugin.reloadConfig();
        loadAllConfigs();
    }

    private void loadAllConfigs() {
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();

        PREFIX = format(config.getString("prefix", "&b&lENSDAIRE &8» &r"));
        ACTIVE_LANGUAGE = config.getString("language", "tr");
        COIN_SYSTEM_NAME = config.getString("coin-system.name", "BCoin");

        MIN_PLAYERS_TO_START = config.getInt("game.min-players", 2);
        LOBBY_COUNTDOWN_SECONDS = config.getInt("game.lobby-countdown", 30);
        ROUND_DURATION_SECONDS = config.getInt("game.round-duration", 15);

        ARENA_RADIUS = config.getInt("arena.radius", 20);
        ARENA_Y_LEVEL = config.getInt("arena.y-level", 150);
        LAVA_Y_LEVEL = config.getInt("arena.lava-y-level", 100);

        TEAM_SELECTOR_SLOT = config.getInt("lobby.team-selector.slot", 4);
        TEAM_SELECTOR_NAME = format(config.getString("lobby.team-selector.name", "&aTakım Seç"));
        TEAM_SELECTOR_MATERIAL = org.bukkit.Material
                .matchMaterial(config.getString("lobby.team-selector.material", "BARREL"));

        LEAVE_LOBBY_SLOT = config.getInt("lobby.leave-item.slot", 8);
        LEAVE_LOBBY_NAME = format(config.getString("lobby.leave-item.name", "&cArenadan Ayrıl"));
        LEAVE_LOBBY_MATERIAL = org.bukkit.Material
                .matchMaterial(config.getString("lobby.leave-item.material", "RED_BED"));

        loadFolder(new File(plugin.getDataFolder(), "menus"), menus);
        loadFolder(new File(plugin.getDataFolder(), "lang"), languages);
        loadFolder(new File(plugin.getDataFolder(), "arenas"), arenas);

        // Ensure default files exist if empty
        checkDefaults();
    }

    private void loadFolder(File folder, Map<String, FileConfiguration> map) {
        if (!folder.exists()) {
            folder.mkdirs();
            return;
        }
        map.clear();
        File[] files = folder.listFiles();
        if (files == null)
            return;
        for (File file : files) {
            if (file.getName().endsWith(".yml")) {
                String name = file.getName().replace(".yml", "");
                map.put(name, YamlConfiguration.loadConfiguration(file));
            }
        }
    }

    private void checkDefaults() {
        // This will be expanded as we create the default files
        if (languages.isEmpty()) {
            plugin.saveResource("lang/messages_tr.yml", false);
            loadFolder(new File(plugin.getDataFolder(), "lang"), languages);
        }
    }

    public FileConfiguration getMenuConfig(String name) {
        return menus.get(name);
    }

    public FileConfiguration getLangConfig(String lang) {
        return languages.get(lang != null ? lang : ACTIVE_LANGUAGE);
    }

    public FileConfiguration getArenaConfig(String name) {
        return arenas.get(name);
    }

    public String getMessage(String path) {
        FileConfiguration lang = getLangConfig(ACTIVE_LANGUAGE);
        if (lang == null)
            return format("&c[Lang Error] " + path);
        String msg = lang.getString(path);
        return format(msg != null ? msg : "&c[Missing Message] " + path);
    }

    public Set<String> getArenaNames() {
        return arenas.keySet();
    }

    public Location getArenaLocation(String arena, String type) {
        FileConfiguration cfg = arenas.get(arena);
        if (cfg == null)
            return null;
        return cfg.getLocation(type);
    }

    public void saveArenaLocation(String arena, String type, Location loc) {
        FileConfiguration cfg = arenas.get(arena);
        if (cfg == null) {
            cfg = new YamlConfiguration();
            arenas.put(arena, cfg);
        }
        cfg.set(type, loc);
        try {
            cfg.save(new File(plugin.getDataFolder(), "arenas/" + arena + ".yml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getTeamSize(String arena) {
        FileConfiguration cfg = arenas.get(arena);
        return cfg != null ? cfg.getInt("team-size", 1) : 1;
    }

    public List<Integer> getRoundDurations(String arena) {
        FileConfiguration cfg = arenas.get(arena);
        return cfg != null ? cfg.getIntegerList("round-durations") : Arrays.asList(120, 90, 60);
    }

    public String getDisplayName(String arena) {
        FileConfiguration cfg = arenas.get(arena);
        return cfg != null ? format(cfg.getString("display-name", arena)) : arena;
    }

    public Location getMainSpawnLocation() {
        return config.getLocation("main-spawn");
    }

    public String format(String text) {
        if (text == null)
            return "";
        // MiniMessage supports <color> tags and gradients
        Component component = miniMessage.deserialize(text);
        return legacySerializer.serialize(component);
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
