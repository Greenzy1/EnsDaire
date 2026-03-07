package net.ensis.ensdaire.listeners;

import net.ensis.ensdaire.EnsDaire;
import net.ensis.ensdaire.models.Arena;
import net.ensis.ensdaire.models.GamePlayer;
import net.ensis.ensdaire.models.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.entity.Projectile;

public class PlayerDamageListener implements Listener {
    private final EnsDaire plugin;
    public PlayerDamageListener(EnsDaire plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player victim)) return;
        Arena arena = plugin.getArenaManager().getByPlayer(victim.getUniqueId());
        if (arena == null) return;
        GamePlayer gp = arena.getGamePlayer(victim.getUniqueId());
        if (gp == null || gp.isSpectator() || arena.getState() != GameState.RUNNING) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPvp(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player victim) || !(e.getDamager() instanceof Player attacker)) return;
        Arena vArena = plugin.getArenaManager().getByPlayer(victim.getUniqueId());
        Arena aArena = plugin.getArenaManager().getByPlayer(attacker.getUniqueId());
        if (vArena == null || !vArena.equals(aArena) || vArena.getState() != GameState.RUNNING) e.setCancelled(true);
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        Arena arena = plugin.getArenaManager().getByPlayer(p.getUniqueId());
        if (arena != null && arena.getState() == GameState.RUNNING) e.setCancelled(true);
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player player && e.getProjectile() instanceof Projectile proj) {
            plugin.getCosmeticManager().handleProjectile(proj, player);
        }
    }
}
