package org.byauth.listener;

import org.byauth.ByCircleGame;
import org.byauth.game.Arena;
import org.byauth.game.ArenaState;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    private final ByCircleGame plugin;

    public BlockBreakListener(ByCircleGame plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Arena arena = plugin.getArenaController().getArenaByPlayer(player);

        if (arena == null) {
            if (!player.hasPermission("ensdaire.admin")) {
                event.setCancelled(true);
            }
            return;
        }

        if (arena.getState() != ArenaState.ACTIVE && arena.getState() != ArenaState.STARTING) {
            event.setCancelled(true);
            return;
        }

        Location blockLoc = event.getBlock().getLocation();
        if (!arena.getGameManager().getPlayerPlacedBlocks().containsKey(blockLoc)) {
            event.setCancelled(true);
        } else {
            arena.getGameManager().getPlayerPlacedBlocks().remove(blockLoc);
        }
    }
}
