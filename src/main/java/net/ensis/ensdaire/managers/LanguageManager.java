package net.ensis.ensdaire.managers;

import net.ensis.ensdaire.EnsDaire;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {
    private final EnsDaire plugin;
    private final Map<String, FileConfiguration> languages = new HashMap<>();
    private String defaultLang;

    public LanguageManager(EnsDaire plugin) {
        this.plugin = plugin;
        this.defaultLang = plugin.getConfig().getString("settings.language", "tr_TR");
        loadLanguages();
    }

    public void loadLanguages() {
        File folder = new File(plugin.getDataFolder(), "languages");
        if (!folder.exists()) folder.mkdirs();

        String[] resources = {"tr_TR.yml", "en_US.yml", "de_DE.yml", "fr_FR.yml", "es_ES.yml"};
        for (String res : resources) {
            File file = new File(folder, res);
            if (!file.exists()) plugin.saveResource("languages/" + res, false);
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                languages.put(file.getName().replace(".yml", ""), YamlConfiguration.loadConfiguration(file));
            }
        }
    }

    public String getMessage(String key) {
        FileConfiguration lang = languages.get(defaultLang);
        if (lang == null) lang = languages.get("tr_TR");
        if (lang == null && !languages.isEmpty()) lang = languages.values().iterator().next();
        if (lang == null) return "Lang Error: " + key;
        
        String msg = lang.getString(key, "Missing key: " + key);
        return ChatColor.translateAlternateColorCodes('&', msg.replace("{prefix}", lang.getString("prefix", "")));
    }

    public String getRaw(String key) {
        FileConfiguration lang = languages.get(defaultLang);
        if (lang == null) lang = languages.get("tr_TR");
        return lang != null ? lang.getString(key, key) : key;
    }
}
