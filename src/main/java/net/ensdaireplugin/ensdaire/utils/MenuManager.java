package net.ensdaireplugin.ensdaire.utils;

import net.ensdaireplugin.ensdaire.EnsDaire;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MenuManager {
    private final EnsDaire plugin;
    private final Map<String, FileConfiguration> menus = new HashMap<>();

    public MenuManager(EnsDaire plugin) {
        this.plugin = plugin;
        loadMenus();
    }

    public void loadMenus() {
        File folder = new File(plugin.getDataFolder(), "menus");
        if (!folder.exists()) folder.mkdirs();

        String[] menuFiles = {"arena_selector.yml", "team_selector.yml", "stats.yml"};
        for (String fileName : menuFiles) {
            File file = new File(folder, fileName);
            if (!file.exists()) plugin.saveResource("menus/" + fileName, false);
            menus.put(fileName.replace(".yml", ""), YamlConfiguration.loadConfiguration(file));
        }
    }

    public FileConfiguration getMenu(String name) {
        return menus.get(name);
    }
}
