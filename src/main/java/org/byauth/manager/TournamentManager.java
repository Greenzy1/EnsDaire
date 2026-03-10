package org.byauth.manager;

import org.bukkit.scheduler.BukkitRunnable;
import org.byauth.EnsDaire;
import org.byauth.service.Service;

import java.time.LocalTime;

public class TournamentManager implements Service {

    private final EnsDaire plugin;
    private boolean tournamentActive = false;

    public TournamentManager(EnsDaire plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() {
        new BukkitRunnable() {
            @Override
            public void run() {
                var now = LocalTime.now();
                if (now.getMinute() == 0 && !tournamentActive) {
                    startScheduledTournament();
                }
            }
        }.runTaskTimer(plugin, 0L, 1200L);
    }

    private void startScheduledTournament() {
        tournamentActive = true;
        plugin.getServer().broadcastMessage(plugin.getSettingsManager().format("&6&lᴛᴜʀɴᴜᴠᴀ! &eꜱᴀᴀᴛʟɪᴋ ᴛᴜʀɴᴜᴠᴀ ʙᴀşʟɪʏᴏʀ!"));
    }

    @Override
    public void terminate() {}
}
