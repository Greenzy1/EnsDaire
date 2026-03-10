package org.byauth.listener;

import org.byauth.ByCircleGame;
import org.byauth.controller.ArenaController;
import org.byauth.data.PlayerStats;
import org.byauth.game.Arena;
import org.byauth.manager.GameManager;
import org.byauth.manager.PlayerDataManager;
import org.byauth.utils.SettingsManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerDeathListener implements Listener {

    private final ByCircleGame plugin;
    private final ArenaController arenaController;
    private final PlayerDataManager playerDataManager;
    private final SettingsManager settings;

    public PlayerDeathListener(ByCircleGame plugin) {
        this.plugin = plugin;
        this.arenaController = plugin.getArenaController();
        this.playerDataManager = plugin.getPlayerDataManager();
        this.settings = plugin.getSettingsManager();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Arena arena = arenaController.getArenaByPlayer(player);

        if (arena == null)
            return;

        event.setDeathMessage(null);
        Player killer = player.getKiller();

        if (killer != null && killer != player) {
            playerDataManager.processKillStats(killer, 10, 0); // Placeholder points/coins
            String msg = "&e" + player.getName() + " &7was killed by &c" + killer.getName();
            arenaController.broadcastArenaMessage(arena, msg);
        } else {
            String msg = "&e" + player.getName() + " &7eliminated.";
            arenaController.broadcastArenaMessage(arena, msg);
        }

        playerDataManager.incrementDeaths(player);
        arenaController.addSpectator(player, arena);

        new BukkitRunnable() {
            @Override
            public void run() {
                player.spigot().respawn();
                player.setGameMode(GameMode.SPECTATOR);
                if (arena.getCenterLocation() != null) {
                    player.teleport(arena.getCenterLocation().clone().add(0.5, 10, 0.5));
                }
            }
        }.runTaskLater(plugin, 1L);
    }
}
