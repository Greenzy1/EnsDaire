package org.byauth.game.event;

import org.bukkit.entity.Player;
import org.byauth.game.Arena;

public interface GameEvent {
    String getName();
    void trigger(Arena arena);
    default void onJoin(Player player) {}
    default void onQuit(Player player) {}
}
