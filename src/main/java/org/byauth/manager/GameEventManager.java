package org.byauth.manager;

import org.bukkit.scheduler.BukkitRunnable;
import org.byauth.EnsDaire;
import org.byauth.game.Arena;
import org.byauth.game.ArenaState;
import org.byauth.game.event.GameEvent;
import org.byauth.service.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameEventManager implements Service {

    private final EnsDaire plugin;
    private final List<GameEvent> registeredEvents = new ArrayList<>();
    private final Random random = new Random();

    public GameEventManager(EnsDaire plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() {
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getArenaController().getArenas().stream()
                    .filter(arena -> arena.getState() == ArenaState.ACTIVE)
                    .forEach(GameEventManager.this::attemptTriggerEvent);
            }
        }.runTaskTimer(plugin, 600L, 600L);
    }

    private void attemptTriggerEvent(Arena arena) {
        if (registeredEvents.isEmpty() || random.nextDouble() > 0.3) return;
        
        GameEvent event = registeredEvents.get(random.nextInt(registeredEvents.size()));
        event.trigger(arena);
        
        var msg = plugin.getSettingsManager().getMessage("event.trigger")
                .replace("%name%", event.getName());
        arena.getGameManager().broadcastArenaMessage(msg);
    }

    public void registerEvent(GameEvent event) {
        registeredEvents.add(event);
    }

    @Override
    public void terminate() {
        registeredEvents.clear();
    }
}
