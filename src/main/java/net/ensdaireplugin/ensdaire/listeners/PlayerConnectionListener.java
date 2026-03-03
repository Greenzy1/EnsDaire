package net.ensdaireplugin.ensdaire.listeners;

import net.ensdaireplugin.ensdaire.EnsDaire;
import net.ensdaireplugin.ensdaire.arena.Arena;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {
    private final EnsDaire plugin;
    public PlayerConnectionListener(EnsDaire plugin) { this.plugin = plugin; }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        plugin.getPlayerDataManager().getOrCreate(e.getPlayer().getUniqueId(), e.getPlayer().getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Arena arena = plugin.getArenaManager().getByPlayer(e.getPlayer().getUniqueId());
        if (arena != null) arena.removePlayer(e.getPlayer());
        plugin.getPlayerDataManager().unload(e.getPlayer().getUniqueId());
    }
}
