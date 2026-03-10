package org.byauth.listener;

import org.byauth.ByCircleGame;
import org.byauth.game.Arena;
import org.byauth.game.ArenaState;
import org.byauth.game.Team;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    private final ByCircleGame plugin;

    public BlockPlaceListener(ByCircleGame plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Arena arena = plugin.getArenaController().getArenaByPlayer(player);

        if (arena == null) {
            if (!player.hasPermission("ensdaire.admin")) {
                event.setCancelled(true);
            }
            return;
        }

        if (arena.getState() != ArenaState.ACTIVE) {
            event.setCancelled(true);
            return;
        }

        Team team = arena.getGameManager().getTeamOfPlayer(player);
        if (team != null) {
            arena.getGameManager().getPlayerPlacedBlocks().put(event.getBlock().getLocation(), team);
        }
    }
}
