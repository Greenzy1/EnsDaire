package org.byauth.manager;

import org.byauth.ByCircleGame;
import org.byauth.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.byauth.EnsDaire;

import java.io.File;
import java.util.*;

public class LootManager {

    private final EnsDaire plugin;
    private final File itemsFile;
    private FileConfiguration itemsConfig;
    private final Map<String, ItemStack> specialItems = new HashMap<>();

    public LootManager(EnsDaire plugin) {
        this.plugin = plugin;
        this.itemsFile = new File(plugin.getDataFolder(), "items.yml");
        loadLoot();
    }

    private void loadLoot() {
        if (!itemsFile.exists()) {
            plugin.saveResource("items.yml", false);
        }
        itemsConfig = YamlConfiguration.loadConfiguration(itemsFile);

        specialItems.clear();
        ConfigurationSection specialItemsSection = itemsConfig.getConfigurationSection("special-items");
        if (specialItemsSection != null) {
            for (String key : specialItemsSection.getKeys(false)) {
                Material material = Material.matchMaterial(specialItemsSection.getString(key + ".material", "STONE"));
                String name = plugin.getSettingsManager()
                        .format(specialItemsSection.getString(key + ".name", "Özel Eşya"));
                List<String> lore = specialItemsSection.getStringList(key + ".lore");
                ItemStack item = new ItemBuilder(material).setName(name).setLore(lore).build();
                specialItems.put(key, item);
            }
        }
    }

    public ItemStack getSpecialItem(String id) {
        ItemStack item = specialItems.get(id);
        return item != null ? item.clone() : null;
    }
}
