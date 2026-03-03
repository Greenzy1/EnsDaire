package net.ensdaireplugin.ensdaire.game;

import net.ensdaireplugin.ensdaire.EnsDaire;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LootTable {
    private final EnsDaire plugin;
    private final List<LootEntry> entries = new ArrayList<>();
    private int totalWeight = 0;
    private final Random random = new Random();

    public LootTable(EnsDaire plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        entries.clear();
        totalWeight = 0;
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("shulker.loot");
        if (section == null) return;
        for (String key : section.getKeys(false)) {
            Material mat = Material.matchMaterial(key);
            if (mat == null) continue;
            int weight = section.getInt(key);
            entries.add(new LootEntry(mat, weight));
            totalWeight += weight;
        }
    }

    public List<ItemStack> rollItems(int count) {
        List<ItemStack> result = new ArrayList<>();
        if (totalWeight <= 0) return result;
        for (int i = 0; i < count; i++) {
            int roll = random.nextInt(totalWeight);
            int current = 0;
            for (LootEntry entry : entries) {
                current += entry.weight;
                if (roll < current) {
                    result.add(new ItemStack(entry.material));
                    break;
                }
            }
        }
        return result;
    }

    private record LootEntry(Material material, int weight) {}
}
