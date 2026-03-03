package net.ensdaireplugin.ensdaire.listeners;

import net.ensdaireplugin.ensdaire.EnsDaire;
import net.ensdaireplugin.ensdaire.arena.Arena;
import net.ensdaireplugin.ensdaire.gui.AdminGui;
import net.ensdaireplugin.ensdaire.gui.ArenaSelectGui;
import net.ensdaireplugin.ensdaire.gui.StatsGui;
import net.ensdaireplugin.ensdaire.gui.TeamSelectGui;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GuiListener implements Listener {
    private final EnsDaire plugin;
    public GuiListener(EnsDaire plugin) { this.plugin = plugin; }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        String title = e.getView().getTitle();

        if (title.equals(ArenaSelectGui.TITLE)) {
            e.setCancelled(true);
            ArenaSelectGui.handleClick(plugin, player, e.getCurrentItem(), e.getSlot());
        } else if (title.startsWith(AdminGui.TITLE_PREFIX)) {
            e.setCancelled(true);
            String arenaId = title.replace(AdminGui.TITLE_PREFIX, "");
            AdminGui.handleClick(plugin, player, arenaId, e.getCurrentItem(), e.getSlot());
        } else if (title.equals(TeamSelectGui.TITLE)) {
            e.setCancelled(true);
            Arena arena = plugin.getArenaManager().getByPlayer(player.getUniqueId());
            if (arena != null) {
                TeamSelectGui.handleClick(plugin, player, arena, e.getCurrentItem());
            }
        } else if (title.equals(StatsGui.TITLE)) {
            e.setCancelled(true);
        }
    }
}
