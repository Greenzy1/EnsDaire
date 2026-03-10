package org.byauth.listener;

import org.byauth.EnsDaire;
import org.byauth.game.Arena;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ArenaManagerListener implements Listener {

    private final EnsDaire plugin;

    public ArenaManagerListener(EnsDaire plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = plugin.getSettingsManager()
                .format(plugin.getSettingsManager().getMenuConfig("arena_manager").getString("title", ""));
        if (!event.getView().getTitle().equals(title))
            return;

        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player))
            return;
        Player p = (Player) event.getWhoClicked();

        if (event.getCurrentItem() == null)
            return;
        String itemName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());

        if (itemName.equals("Geri Dön")) {
            p.openInventory(plugin.getGuiManager().createInventoryFromConfig("admin_main"));
            return;
        }

        // Logic for handling specific arena clicks
        for (Arena arena : plugin.getArenaController().getArenas()) {
            if (arena.getId().equals(itemName)) {
                // Open specific Arena Editor GUI (to be implemented)
                p.sendMessage(plugin.getSettingsManager().PREFIX + "&e" + arena.getId()
                        + " &7başarıyla seçildi (Düzenleme menüsü yakında!)");
                break;
            }
        }
    }
}
