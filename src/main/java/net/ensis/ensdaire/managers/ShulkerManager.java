package net.ensis.ensdaire.managers;

import net.ensis.ensdaire.EnsDaire;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Shulker;
import org.bukkit.inventory.ItemStack;

import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShulkerManager {
    private final EnsDaire plugin;
    private final String arenaId;
    private final List<Block> activeShulkers = new ArrayList<>();
    private final Random random = new Random();

    public ShulkerManager(EnsDaire plugin, String arenaId) {
        this.plugin = plugin;
        this.arenaId = arenaId;
    }

    public void spawnAll(List<Location> spawns) {
        clearAll();
        for (Location loc : spawns) {
            Block block = loc.getBlock();
            block.setType(Material.SHULKER_BOX);
            activeShulkers.add(block);
        }
    }

    public boolean isGameShulker(Shulker shulker) {
        // Shulker bloğu ile koordinat eşleşmesi (basit mantık)
        for (Block b : activeShulkers) {
            if (b.getLocation().distanceSquared(shulker.getLocation()) < 2) return true;
        }
        return false;
    }

    public void saveLoot(List<ItemStack> items) {
        File file = new File(plugin.getDataFolder(), "loot.yml");
        YamlConfiguration config = new YamlConfiguration();
        List<String> serialized = new ArrayList<>();
        for (ItemStack item : items) {
            if (item != null && item.getType() != Material.AIR) {
                serialized.add(net.ensis.ensdaire.utils.Base64Utils.encode(item));
            }
        }
        config.set("items", serialized);
        try { config.save(file); } catch (Exception e) { e.printStackTrace(); }
    }

    public List<ItemStack> getLootTable() {
        File file = new File(plugin.getDataFolder(), "loot.yml");
        if (!file.exists()) return getRandomLoot(); // Eğer dosya yoksa config'deki varsayılanları kullan
        
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<ItemStack> items = new ArrayList<>();
        List<String> serialized = config.getStringList("items");
        
        if (serialized.isEmpty()) return getRandomLoot();

        int count = plugin.getConfig().getInt("shulker.items-per-shulker", 3);
        for (int i = 0; i < count; i++) {
            String data = serialized.get(random.nextInt(serialized.size()));
            ItemStack item = net.ensis.ensdaire.utils.Base64Utils.decode(data);
            if (item != null) items.add(item);
        }
        return items;
    }

    public void clearAll() {
        for (Block block : activeShulkers) {
            block.setType(Material.AIR);
        }
        activeShulkers.clear();
    }

    public List<ItemStack> getRandomLoot() {
        List<ItemStack> loot = new ArrayList<>();
        ConfigurationSection config = plugin.getConfig().getConfigurationSection("shulker.loot");
        if (config == null) return loot;

        int count = plugin.getConfig().getInt("shulker.items-per-shulker", 3);
        List<String> keys = new ArrayList<>(config.getKeys(false));
        
        for (int i = 0; i < count; i++) {
            String key = keys.get(random.nextInt(keys.size()));
            int chance = config.getInt(key);
            if (random.nextInt(100) < chance) {
                try {
                    loot.add(new ItemStack(Material.valueOf(key.toUpperCase())));
                } catch (Exception ignored) {}
            }
        }
        return loot;
    }
}
