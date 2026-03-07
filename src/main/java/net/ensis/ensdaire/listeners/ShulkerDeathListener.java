package net.ensis.ensdaire.listeners;

import net.ensis.ensdaire.EnsDaire;
import net.ensis.ensdaire.models.Arena;
import net.ensis.ensdaire.models.GameState;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ShulkerDeathListener implements Listener {
    private final EnsDaire plugin;
    public ShulkerDeathListener(EnsDaire plugin) { this.plugin = plugin; }

    @EventHandler
    public void onShulkerDeath(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof Shulker shulker)) return;
        for (Arena arena : plugin.getArenaManager().all()) {
            if (arena.getState() != GameState.RUNNING) continue;
            if (!arena.getShulkerManager().isGameShulker(shulker)) continue;
            
            e.getDrops().clear();
            e.setDroppedExp(0);
            
            List<ItemStack> loot = arena.getShulkerManager().getLootTable();
            for (ItemStack item : loot) {
                shulker.getWorld().dropItemNaturally(shulker.getLocation(), item);
            }
            
            Player killer = shulker.getKiller();
            if (killer != null) {
                killer.sendMessage(plugin.getLanguageManager().getMessage("prefix") + "§a✦ Sandık açıldı! §e" + loot.size() + " §aitem düştü.");
                try {
                    killer.playSound(killer.getLocation(), Sound.valueOf(plugin.getConfig().getString("sounds.shulker-open","ENTITY_SHULKER_OPEN")), 1f, 1f);
                } catch (Exception ignored) {}
            }
            return;
        }
    }
}
