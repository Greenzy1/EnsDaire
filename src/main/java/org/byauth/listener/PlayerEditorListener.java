package org.byauth.listener;

import org.byauth.ByCircleGame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PlayerEditorListener implements Listener {

    private final ByCircleGame plugin;

    public PlayerEditorListener(ByCircleGame plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = plugin.getSettingsManager()
                .format(plugin.getSettingsManager().getMenuConfig("player_editor").getString("title", ""));
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

        if (itemName.equals("Oyuncu Ara")) {
            p.closeInventory();
            p.sendMessage(org.byauth.utils.SettingsManager.PREFIX + plugin.getSettingsManager()
                    .format("&7Lütfen sohbet kısmına düzenlemek istediğiniz oyuncunun adını yazın."));
            // Here we would typically use a chat input listener, but for now we can list
            // online players
            p.openInventory(plugin.getGuiManager().createOnlinePlayersInventory());
        }
    }
}
