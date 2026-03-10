package org.byauth.listener;

import org.byauth.EnsDaire;
import org.byauth.controller.ArenaController;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final ArenaController arenaController;

    public PlayerQuitListener(EnsDaire plugin) {
        this.arenaController = plugin.getArenaController();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        arenaController.removePlayer(player);
    }
}
