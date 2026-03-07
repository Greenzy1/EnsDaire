package net.ensis.ensdaire.listeners;

import net.ensis.ensdaire.EnsDaire;
import net.ensis.ensdaire.models.Arena;
import net.ensis.ensdaire.models.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

public class WorldListener implements Listener {
    private final EnsDaire plugin;
    public WorldListener(EnsDaire plugin) { this.plugin = plugin; }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Arena arena = plugin.getArenaManager().getByPlayer(e.getPlayer().getUniqueId());
        if (arena != null) e.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Arena arena = plugin.getArenaManager().getByPlayer(e.getPlayer().getUniqueId());
        if (arena != null) e.setCancelled(true);
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        for (Arena arena : plugin.getArenaManager().all()) {
            if (arena.getState() != GameState.DISABLED && arena.getLobbySpawn() != null) {
                if (e.getLocation().getWorld().equals(arena.getLobbySpawn().getWorld())) {
                    if (e.getLocation().distanceSquared(arena.getLobbySpawn()) < 10000) {
                        e.blockList().clear();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent e) {
        if (e.getEntityType().isAlive() && !(e.getEntityType().name().contains("PLAYER") || e.getEntityType().name().contains("SHULKER"))) {
            for (Arena arena : plugin.getArenaManager().all()) {
                if (arena.getLobbySpawn() != null && e.getLocation().getWorld().equals(arena.getLobbySpawn().getWorld())) {
                    if (e.getLocation().distanceSquared(arena.getLobbySpawn()) < 10000) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
}
