package net.ensis.ensdaire.listeners;

import net.ensis.ensdaire.EnsDaire;
import net.ensis.ensdaire.models.Arena;
import net.ensis.ensdaire.models.GamePlayer;
import net.ensis.ensdaire.models.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;

public class PlayerInventoryListener implements Listener {
    private final EnsDaire plugin;
    public PlayerInventoryListener(EnsDaire plugin) { this.plugin = plugin; }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Arena arena = plugin.getArenaManager().getByPlayer(e.getPlayer().getUniqueId());
        if (arena == null) return;
        
        // Oyun esnasında item düşürme sadece belli durumlarda (örn. shulker'dan düşen itemler) serbest olabilir.
        // Ama oyuncu kendi inventory'sini boşaltmamalı.
        e.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerAttemptPickupItemEvent e) {
        Arena arena = plugin.getArenaManager().getByPlayer(e.getPlayer().getUniqueId());
        if (arena == null) return;

        GamePlayer gp = arena.getGamePlayer(e.getPlayer().getUniqueId());
        if (gp == null) return;

        // Lobide veya İzleyiciyken item toplama yasak
        if (arena.getState() == GameState.WAITING || arena.getState() == GameState.COUNTDOWN || gp.isSpectator()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        Arena arena = plugin.getArenaManager().getByPlayer(player.getUniqueId());
        if (arena == null) return;

        // Lobide veya oyun başlıyorken envanterle oynamak yasak
        if (arena.getState() != GameState.RUNNING) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        Arena arena = plugin.getArenaManager().getByPlayer(player.getUniqueId());
        if (arena == null) return;

        if (arena.getState() != GameState.RUNNING) {
            e.setCancelled(true);
        }
    }
}
