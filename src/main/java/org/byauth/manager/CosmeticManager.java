package org.byauth.manager;

import org.byauth.ByCircleGame;
import org.byauth.data.VictoryEffect;
import org.byauth.utils.SettingsManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.byauth.EnsDaire;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CosmeticManager {

    private final EnsDaire plugin;
    private final SettingsManager settingsManager;
    private final List<VictoryEffect> victoryEffects = new ArrayList<>();

    public CosmeticManager(EnsDaire plugin) {
        this.plugin = plugin;
        this.settingsManager = plugin.getSettingsManager();
        loadCosmetics();
    }

    public void loadCosmetics() {
        File cosmeticsFile = new File(plugin.getDataFolder(), "cosmetics.yml");
        if (!cosmeticsFile.exists()) {
            plugin.saveResource("cosmetics.yml", false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(cosmeticsFile);

        victoryEffects.clear();
        List<Map<?, ?>> itemsList = config.getMapList("victory-effects.items");
        if (itemsList != null) {
            for (Map<?, ?> map : itemsList) {
                try {
                    String id = (String) map.get("id");
                    String displayName = settingsManager.format((String) map.get("display-name"));
                    Material material = Material.matchMaterial((String) map.get("material"));
                    int slot = (int) map.get("slot");
                    int price = (int) map.get("price");

                    if (id != null) {
                        victoryEffects.add(new VictoryEffect(id, displayName, material, slot, price, new ArrayList<>(),
                                new ArrayList<>(), new ArrayList<>()));
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    public VictoryEffect getEffectById(String id) {
        return victoryEffects.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
    }
}
