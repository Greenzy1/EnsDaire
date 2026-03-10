package org.byauth.manager;

import org.bukkit.entity.Player;
import org.byauth.EnsDaire;
import org.byauth.service.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AchievementManager implements Service {

    private final EnsDaire plugin;
    private final Map<UUID, Set<String>> playerAchievements = new ConcurrentHashMap<>();

    public AchievementManager(EnsDaire plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() {}

    public void award(Player player, String achievementId) {
        var uuid = player.getUniqueId();
        var achievements = playerAchievements.computeIfAbsent(uuid, k -> new HashSet<>());
        
        if (achievements.add(achievementId)) {
            var msg = plugin.getSettingsManager().getMessage("achievement.awarded")
                    .replace("%name%", achievementId);
            player.sendMessage(plugin.getSettingsManager().format(msg));
            plugin.getService(LevelManager.class).addExperience(player, 1000);
        }
    }

    @Override
    public void terminate() {
        playerAchievements.clear();
    }
}
