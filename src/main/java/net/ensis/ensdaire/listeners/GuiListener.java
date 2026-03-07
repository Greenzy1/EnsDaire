package net.ensis.ensdaire.listeners;

import net.ensis.ensdaire.EnsDaire;
import net.ensis.ensdaire.models.Arena;
import net.ensis.ensdaire.gui.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GuiListener implements Listener {

    private final EnsDaire plugin;

    public GuiListener(EnsDaire plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player))
            return;
        String title = e.getView().getTitle();

        if (title.equals(ArenaSelectGui.TITLE)) {
            e.setCancelled(true);
            ArenaSelectGui.handleClick(plugin, player, e.getCurrentItem(), e.getSlot());
        } else if (title.equals(ArenaListGui.TITLE)) {
            e.setCancelled(true);
            ArenaListGui.handleClick(plugin, player, e.getCurrentItem());
        } else if (title.startsWith(ArenaEditorGui.TITLE_PREFIX)) {
            e.setCancelled(true);
            String arenaId = title.replace(ArenaEditorGui.TITLE_PREFIX, "");
            ArenaEditorGui.handleClick(plugin, player, arenaId, e.getSlot(), e.getClick());
        } else if (title.equals(TeamSelectGui.TITLE)) {
            e.setCancelled(true);
            Arena arena = plugin.getArenaManager().getByPlayer(player.getUniqueId());
            if (arena != null)
                TeamSelectGui.handleClick(plugin, player, arena, e.getCurrentItem());
        } else if (title.equals(StatsGui.TITLE)) {
            e.setCancelled(true);
        } else if (title.equals(CosmeticsGui.MAIN_TITLE) || title.startsWith(CosmeticsGui.CAT_TITLE_PREFIX)) {
            e.setCancelled(true);
            CosmeticsGui.handleClick(plugin, player, e.getCurrentItem(), title);
        } else if (title.equals(AdminPanelGui.TITLE)) {
            e.setCancelled(true);
            AdminPanelGui.handleClick(plugin, player, e.getCurrentItem());
        } else if (title.equals(ColorBlockGui.TITLE)) {
            e.setCancelled(true);
            ColorBlockGui.handleClick(plugin, player, e.getCurrentItem(), e.getCursor());
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getView().getTitle().equals(LootEditorGui.TITLE)) {
            LootEditorGui.save(plugin, e.getInventory());
            if (e.getPlayer() instanceof Player p) {
                p.sendMessage(plugin.getLanguageManager().getMessage("prefix")
                        + "§aShulker loot listesi başarıyla güncellendi.");
            }
        }
    }
}
