package org.byauth.manager;

import org.bukkit.entity.Player;
import org.byauth.EnsDaire;
import org.byauth.data.LevelData;
import org.byauth.service.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LevelManager implements Service {

    private final EnsDaire plugin;
    private final Map<UUID, LevelData> cache = new HashMap<>();

    public LevelManager(EnsDaire plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() {}

    public void addExperience(Player player, long amount) {
        var uuid = player.getUniqueId();
        var current = cache.getOrDefault(uuid, new LevelData(1, 0));
        
        long totalXp = current.xp() + amount;
        int level = current.level();
        
        while (totalXp >= current.getRequiredXp()) {
            totalXp -= current.getRequiredXp();
            level++;
            handleLevelUp(player, level);
        }
        
        cache.put(uuid, new LevelData(level, totalXp));
        
        var msg = plugin.getSettingsManager().getMessage("level.xp-gain")
                .replace("%amount%", String.valueOf(amount));
        player.sendMessage(plugin.getSettingsManager().format(msg));
    }

    private void handleLevelUp(Player player, int newLevel) {
        var msg = plugin.getSettingsManager().getMessage("level.level-up")
                .replace("%level%", String.valueOf(newLevel));
        player.sendMessage(plugin.getSettingsManager().format(msg));
        plugin.getVictoryEffects().playRandomVictoryEffect(player);
    }

    @Override
    public void terminate() {
        cache.clear();
    }
}
