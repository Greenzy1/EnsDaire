package net.ensis.ensdaire.managers;

import net.ensis.ensdaire.EnsDaire;
import net.ensis.ensdaire.models.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;

import java.util.List;

public class HologramManager {
    private final EnsDaire plugin;

    public HologramManager(EnsDaire plugin) {
        this.plugin = plugin;
    }

    public void createLeaderboard(Location loc) {
        // Eski hologramı temizle (basit mantık: aynı bloktakini sil)
        loc.getWorld().getNearbyEntities(loc, 1, 1, 1).forEach(e -> {
            if (e instanceof TextDisplay) e.remove();
        });

        TextDisplay display = (TextDisplay) loc.getWorld().spawnEntity(loc, EntityType.TEXT_DISPLAY);
        display.setBillboard(Display.Billboard.CENTER);
        
        List<PlayerData> top = plugin.getPlayerDataManager().getTop(10);
        StringBuilder sb = new StringBuilder("§e§l🏆 EN ÇOK KAZANANLAR 🏆\n\n");
        
        int i = 1;
        for (PlayerData pd : top) {
            sb.append("§6#").append(i++).append(" §f").append(pd.getName())
              .append(" §7- §e").append(pd.getPoints()).append(" Puan\n");
        }
        
        display.setText(sb.toString());
    }
}
