package org.byauth.listener;

import org.byauth.EnsDaire;
import org.byauth.game.Arena;
import org.byauth.game.ArenaState;
import org.byauth.game.Team;
import org.byauth.manager.EditorManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class AdminGuiListener implements Listener {

    private final EnsDaire plugin;

    public AdminGuiListener(EnsDaire plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryView view = event.getView();
        String title = view.getTitle();
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player p = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        if (item == null || !item.hasItemMeta()) return;

        // Main Admin Menu
        if (title.equals(plugin.getSettingsManager().format(plugin.getSettingsManager().getMenuConfig("admin_main").getString("title", "")))) {
            event.setCancelled(true);
            int slot = event.getRawSlot();
            if (slot == 11) p.openInventory(plugin.getGuiManager().createArenaManagerInventory());
            else if (slot == 13) p.openInventory(plugin.getGuiManager().createInventoryFromConfig("player_editor"));
            else if (slot == 15) {
                plugin.getSettingsManager().reload();
                p.sendMessage(ChatColor.GREEN + "Ayarlar yenilendi!");
                p.closeInventory();
            }
            return;
        }

        // Arena Manager Menu
        if (title.contains("Arena Yönetimi")) {
            event.setCancelled(true);
            if (item.getItemMeta().getDisplayName().contains("Geri Dön")) {
                p.openInventory(plugin.getGuiManager().createInventoryFromConfig("admin_main"));
                return;
            }
            if (item.getItemMeta().getDisplayName().contains("Yeni Arena Oluştur")) {
                p.closeInventory();
                p.sendMessage(ChatColor.YELLOW + "Lütfen chat kısmına arena adını yazın.");
                plugin.getEditorManager().setState(p, EditorManager.EditorType.ARENA_SETTINGS, "NEW_ARENA");
                return;
            }
            
            String arenaId = ChatColor.stripColor(item.getItemMeta().getDisplayName());
            Arena arena = plugin.getArenaController().getOrCreateArena(arenaId);
            p.openInventory(plugin.getGuiManager().createArenaEditorInventory(arena));
            return;
        }

        // Arena Editor Menu
        if (title.contains("Düzenle:")) {
            event.setCancelled(true);
            String arenaId = title.split(": ")[1].replace(ChatColor.COLOR_CHAR + "b", "");
            arenaId = ChatColor.stripColor(arenaId);
            Arena arena = plugin.getArenaController().getOrCreateArena(arenaId);
            
            int slot = event.getRawSlot();
            switch (slot) {
                case 10: // Display Name
                    p.closeInventory();
                    p.sendMessage(ChatColor.YELLOW + "Lütfen yeni görünen adı chat kısmına yazın.");
                    plugin.getEditorManager().setState(p, EditorManager.EditorType.ARENA_SETTINGS, arenaId + ":DISPLAY_NAME");
                    break;
                case 11: // Lobby
                    arena.setLobbyLocation(p.getLocation());
                    plugin.getSettingsManager().saveArenaLocation(arenaId, "lobby", p.getLocation());
                    p.sendMessage(ChatColor.GREEN + "Lobi konumu ayarlandı!");
                    break;
                case 12: // Center
                    arena.setCenterLocation(p.getLocation());
                    plugin.getSettingsManager().saveArenaLocation(arenaId, "center", p.getLocation());
                    arena.getArenaManager().generateArena(p.getLocation(), Arrays.asList(Team.values()));
                    p.sendMessage(ChatColor.GREEN + "Merkez konumu ayarlandı ve arena oluşturuldu!");
                    break;
                case 13: // Team Size
                    if (event.isLeftClick()) arena.setTeamSize(arena.getTeamSize() + 1);
                    else if (event.isRightClick() && arena.getTeamSize() > 1) arena.setTeamSize(arena.getTeamSize() - 1);
                    p.openInventory(plugin.getGuiManager().createArenaEditorInventory(arena));
                    break;
                case 15: // State
                    if (arena.getState() == ArenaState.WAITING) arena.setState(ArenaState.DISABLED);
                    else arena.setState(ArenaState.WAITING);
                    p.openInventory(plugin.getGuiManager().createArenaEditorInventory(arena));
                    break;
                case 31: // Back
                    p.openInventory(plugin.getGuiManager().createArenaManagerInventory());
                    break;
            }
        }
    }
}
