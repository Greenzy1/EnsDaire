package net.ensis.ensdaire.managers;

import net.ensis.ensdaire.EnsDaire;
import net.ensis.ensdaire.models.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

public class SignManager implements Listener {
    private final EnsDaire plugin;
    private final Map<Location, String> signs = new HashMap<>();

    public SignManager(EnsDaire plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        if (e.getLine(0).equalsIgnoreCase("[ensdaire]")) {
            String arenaId = e.getLine(1);
            if (plugin.getArenaManager().get(arenaId) != null) {
                signs.put(e.getBlock().getLocation(), arenaId);
                e.setLine(0, "§b[EnsDaire]");
                e.setLine(1, "§e" + arenaId);
                e.setLine(2, "§aBekleniyor");
                e.setLine(3, "§f0/" + plugin.getArenaManager().get(arenaId).getMaxPlayers());
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getState() instanceof Sign) {
            String arenaId = signs.get(e.getClickedBlock().getLocation());
            if (arenaId != null) {
                Arena a = plugin.getArenaManager().get(arenaId);
                if (a != null) a.addPlayer(e.getPlayer());
            }
        }
    }
    
    // Arena durum değişikliğinde tabelaları güncelleyen metot buraya eklenebilir
}
