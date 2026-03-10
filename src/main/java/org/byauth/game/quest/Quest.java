package org.byauth.game.quest;

import org.bukkit.entity.Player;

public interface Quest {
    String getId();
    String getDescription();
    int getGoal();
    void onAction(Player player, QuestAction action);
}
