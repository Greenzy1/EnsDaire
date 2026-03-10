package org.byauth.listener;

import org.byauth.ByCircleGame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;

public class AdminGuiListener implements Listener {

    private final ByCircleGame plugin;

    public AdminGuiListener(ByCircleGame plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryView view = event.getView();
        String title = plugin.getSettingsManager()
                .format(plugin.getSettingsManager().getMenuConfig("admin_main").getString("title", ""));

        if (!view.getTitle().equals(title))
            return;

        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player))
            return;
        Player p = (Player) event.getWhoClicked();

        int slot = event.getRawSlot();
        if (slot == 11) { // Arena Manager
            p.openInventory(plugin.getGuiManager().createArenaManagerInventory());
        } else if (slot == 13) { // Player Editor
            p.openInventory(plugin.getGuiManager().createInventoryFromConfig("player_editor"));
        } else if (slot == 15) { // System Settings/Reload
            plugin.getSettingsManager().reload();
            p.sendMessage(plugin.getSettingsManager().getMessage("admin.reload"));
            p.closeInventory();
        }
    }
}
