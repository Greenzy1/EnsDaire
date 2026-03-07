package net.ensis.ensdaire.listeners;

import net.ensis.ensdaire.EnsDaire;
import net.ensis.ensdaire.models.*;
import net.ensis.ensdaire.models.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerDeathListener implements Listener {
    private final EnsDaire plugin;
    public PlayerDeathListener(EnsDaire plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(PlayerDeathEvent e) {
        Player victim = e.getEntity();
        Arena arena = plugin.getArenaManager().getByPlayer(victim.getUniqueId());
        if (arena == null || arena.getState() != GameState.RUNNING) return;
        e.setDeathMessage(null);
        e.getDrops().clear();
        e.setDroppedExp(0);
        arena.eliminate(victim, victim.getKiller(), EliminationCause.KILLED);
        new BukkitRunnable() {
            @Override public void run() {
                if (victim.isOnline()) victim.spigot().respawn();
            }
        }.runTaskLater(plugin, 1L);
    }
}
