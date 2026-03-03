package net.ensdaireplugin.ensdaire.gui;

import net.ensdaireplugin.ensdaire.EnsDaire;
import net.ensdaireplugin.ensdaire.arena.Arena;
import net.ensdaireplugin.ensdaire.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ArenaSelectGui {

    public static final String TITLE = "§8§l✦ §bArena Seçimi §8§l✦";

    public static void open(EnsDaire plugin, Player player) {
        Collection<Arena> arenas = plugin.getArenaManager().all();
        // 54 slot max, 9 min
        int size = 27; // Sabit bir boyut veya dinamik
        Inventory inv = Bukkit.createInventory(null, size, TITLE);

        // Kenar dolgusu
        ItemStack filler = makeItem(Material.CYAN_STAINED_GLASS_PANE, " ", List.of());
        for (int i = 0; i < 9; i++) inv.setItem(i, filler);
        for (int i = 18; i < 27; i++) inv.setItem(i, filler);
        inv.setItem(9, filler); inv.setItem(17, filler);

        int slot = 10;
        for (Arena arena : arenas) {
            if (slot > 16) break;
            inv.setItem(slot++, buildArenaItem(plugin, arena));
        }

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1f, 1.2f);
    }

    private static ItemStack buildArenaItem(EnsDaire plugin, Arena arena) {
        Material mat;
        String statusLine;
        GameState state = arena.getState();

        switch (state) {
            case WAITING    -> { mat = Material.LIME_CONCRETE;    statusLine = "§aBekleniyor..."; }
            case COUNTDOWN  -> { mat = Material.YELLOW_CONCRETE;  statusLine = "§eGeri Sayım Başladı!"; }
            case STARTING, RUNNING, ROUND_END -> { mat = Material.RED_CONCRETE;     statusLine = "§cOyun Devam Ediyor"; }
            case DISABLED   -> { mat = Material.BLACK_CONCRETE;   statusLine = "§0Bakımda"; }
            default         -> { mat = Material.GRAY_CONCRETE;    statusLine = "§7Bilinmiyor"; }
        }

        List<String> lore = new ArrayList<>();
        lore.add("§8§m-----------------------");
        lore.add(" §7Durum: " + statusLine);
        lore.add(" §7Oyuncular: §f" + arena.getPlayers().size() + "§8/§f" + arena.getMaxPlayers());
        
        if (state == GameState.RUNNING) {
            lore.add(" §7Mevcut Round: §e" + arena.getCurrentRound());
        }
        
        lore.add(" ");
        lore.add(" §7Kapsül Sayısı: §b" + arena.getCapsuleSpawns().size());
        lore.add(" §7Shulker Sayısı: §b" + arena.getShulkerSpawns().size());
        lore.add("§8§m-----------------------");
        
        if (state == GameState.WAITING || state == GameState.COUNTDOWN) {
            if (arena.getPlayers().size() >= arena.getMaxPlayers()) {
                lore.add(" §c✖ Arena Dolu!");
            } else {
                lore.add(" §a▶ Katılmak için Tıkla");
            }
        } else {
            lore.add(" §7Şu an katılamazsın.");
        }

        return makeItem(mat, "§b§lArena: §f" + arena.getId(), lore);
    }

    public static void handleClick(EnsDaire plugin, Player player, ItemStack item, int slot) {
        if (item == null || item.getType() == Material.CYAN_STAINED_GLASS_PANE) return;
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;

        String displayName = item.getItemMeta().getDisplayName();
        if (!displayName.startsWith("§b§lArena: §f")) return;
        
        String arenaId = ChatColor.stripColor(displayName.replace("§b§lArena: §f", ""));
        Arena arena = plugin.getArenaManager().get(arenaId);
        
        if (arena == null) { 
            player.closeInventory(); 
            return; 
        }

        if (plugin.getArenaManager().getByPlayer(player.getUniqueId()) != null) {
            player.sendMessage(plugin.getConfig().getString("messages.prefix","") + "§cZaten bir oyundasın!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            player.closeInventory();
            return;
        }

        Arena.JoinResult result = arena.addPlayer(player);
        String prefix = plugin.getConfig().getString("messages.prefix","");
        
        switch (result) {
            case SUCCESS -> {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                player.closeInventory();
            }
            case FULL -> {
                player.sendMessage(prefix + "§cArena dolu!");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            }
            case NOT_JOINABLE -> {
                player.sendMessage(prefix + "§cBu arena şu an katılım için uygun değil!");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            }
            case DISABLED -> {
                player.sendMessage(prefix + "§cBu arena kapalı.");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            }
        }
    }

    private static ItemStack makeItem(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
