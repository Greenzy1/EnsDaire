package net.ensis.ensdaire.listeners;

import net.ensis.ensdaire.EnsDaire;
import net.ensis.ensdaire.models.Arena;
import net.ensis.ensdaire.models.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {
    private final EnsDaire plugin;
    public PlayerMoveListener(EnsDaire plugin) { this.plugin = plugin; }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getFrom().getBlockX() == e.getTo().getBlockX() && e.getFrom().getBlockZ() == e.getTo().getBlockZ()) return;
        Arena arena = plugin.getArenaManager().getByPlayer(e.getPlayer().getUniqueId());
        if (arena == null) return;
        if (arena.getState() == GameState.STARTING || arena.getState() == GameState.COUNTDOWN) {
            if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ()) {
                e.setTo(e.getFrom().setDirection(e.getTo().getDirection()));
            }
        }
    }
}
