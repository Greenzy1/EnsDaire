package org.byauth.listener;

import org.byauth.EnsDaire;
import org.byauth.controller.ArenaController;
import org.byauth.game.Arena;
import org.byauth.utils.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final EnsDaire plugin;
    private final ArenaController arenaController;
    private final SettingsManager settings;

    public PlayerJoinListener(EnsDaire plugin) {
        this.plugin = plugin;
        this.arenaController = plugin.getArenaController();
        this.settings = plugin.getSettingsManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getPlayerDataManager().loadPlayerData(player);

        Arena arena = arenaController.getArenaByPlayer(player);
        if (arena == null) {
            // Check if player is near any active arena and teleport out if they aren't in
            // it
            for (Arena a : arenaController.getArenas()) {
                Location center = a.getCenterLocation();
                if (center != null && center.getWorld().equals(player.getWorld())) {
                    if (player.getLocation().distance(center) < settings.ARENA_RADIUS + 10) {
                        arenaController.removePlayer(player);
                    }
                }
            }
        }
        arenaController.updatePlayerVisibility(player);
    }
}
