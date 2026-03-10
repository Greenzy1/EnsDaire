package org.byauth.listener;

import org.byauth.ByCircleGame;
import org.byauth.controller.ArenaController;
import org.byauth.game.Arena;
import org.byauth.game.ArenaState;
import org.byauth.game.Team;
import org.byauth.utils.SettingsManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GuiListener implements Listener {

    private final ArenaController arenaController;
    private final SettingsManager settings;

    public GuiListener(ByCircleGame plugin) {
        this.arenaController = plugin.getArenaController();
        this.settings = plugin.getSettingsManager();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(settings.getMessage("gui.team-select-title"))) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            Arena arena = arenaController.getArenaByPlayer(player);

            if (arena == null || arena.getState() == ArenaState.ACTIVE || arena.getState() == ArenaState.STARTING) {
                player.closeInventory();
                return;
            }

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR)
                return;

            if (clickedItem.getType() == Material.BARRIER) {
                arenaController.unselectTeam(player);
                return;
            }

            for (Team team : Team.values()) {
                if (clickedItem.getType() == team.getConcreteMaterial()) {
                    arenaController.selectTeam(player, team);
                    break;
                }
            }
        }
    }
}
