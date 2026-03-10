package org.byauth.manager;

import org.bukkit.entity.Player;
import org.byauth.EnsDaire;
import org.byauth.game.quest.Quest;
import org.byauth.game.quest.QuestAction;
import org.byauth.service.Service;

import java.util.*;

public class QuestManager implements Service {

    private final EnsDaire plugin;
    private final Map<String, Quest> quests = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> progress = new HashMap<>();

    public QuestManager(EnsDaire plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() {}

    public void trackAction(Player player, QuestAction action) {
        var uuid = player.getUniqueId();
        var playerProgress = progress.computeIfAbsent(uuid, k -> new HashMap<>());
        
        quests.values().forEach(quest -> {
            quest.onAction(player, action);
            int current = playerProgress.getOrDefault(quest.getId(), 0);
            if (current < quest.getGoal()) {
                playerProgress.put(quest.getId(), current + 1);
                
                var msg = plugin.getSettingsManager().getMessage("quest.progress")
                        .replace("%current%", String.valueOf(current + 1))
                        .replace("%goal%", String.valueOf(quest.getGoal()))
                        .replace("%description%", quest.getDescription());
                player.sendMessage(plugin.getSettingsManager().format(msg));

                if (current + 1 == quest.getGoal()) {
                    handleQuestComplete(player, quest);
                }
            }
        });
    }

    private void handleQuestComplete(Player player, Quest quest) {
        var msg = plugin.getSettingsManager().getMessage("quest.completed")
                .replace("%description%", quest.getDescription());
        player.sendMessage(plugin.getSettingsManager().format(msg));
        plugin.getService(LevelManager.class).addExperience(player, 500);
    }

    @Override
    public void terminate() {
        quests.clear();
        progress.clear();
    }
}
