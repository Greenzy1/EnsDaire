package org.byauth.listener;

import org.byauth.EnsDaire;
import org.byauth.game.Arena;
import org.byauth.manager.EditorManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class EditorChatListener implements Listener {

    private final EnsDaire plugin;

    public EditorChatListener(EnsDaire plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        EditorManager.EditorState state = plugin.getEditorManager().getState(player);

        if (state == null) return;

        event.setCancelled(true);
        String input = event.getMessage();

        if (input.equalsIgnoreCase("iptal") || input.equalsIgnoreCase("cancel")) {
            plugin.getEditorManager().clearState(player);
            player.sendMessage(ChatColor.RED + "İşlem iptal edildi.");
            return;
        }

        if (state.type == EditorManager.EditorType.ARENA_SETTINGS) {
            if (state.targetId.equals("NEW_ARENA")) {
                Arena arena = plugin.getArenaController().getOrCreateArena(input);
                plugin.getEditorManager().clearState(player);
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    player.openInventory(plugin.getGuiManager().createArenaEditorInventory(arena));
                });
                player.sendMessage(ChatColor.GREEN + "Arena '" + input + "' oluşturuldu!");
            } else if (state.targetId.contains(":DISPLAY_NAME")) {
                String arenaId = state.targetId.split(":")[0];
                Arena arena = plugin.getArenaController().getOrCreateArena(arenaId);
                arena.setDisplayName(input);
                plugin.getEditorManager().clearState(player);
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    player.openInventory(plugin.getGuiManager().createArenaEditorInventory(arena));
                });
                player.sendMessage(ChatColor.GREEN + "Görünen ad güncellendi!");
            }
        }
    }
}
