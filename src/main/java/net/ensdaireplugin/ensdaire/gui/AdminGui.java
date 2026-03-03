package net.ensdaireplugin.ensdaire.gui;

import net.ensdaireplugin.ensdaire.EnsDaire;
import net.ensdaireplugin.ensdaire.arena.Arena;
import net.ensdaireplugin.ensdaire.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class AdminGui {

    public static final String TITLE_PREFIX = "§4§lAdmin: §8";

    public static void open(EnsDaire plugin, Player player, String arenaId) {
        Arena arena = plugin.getArenaManager().get(arenaId);
        if (arena == null) {
            player.sendMessage("§cArena bulunamadı: " + arenaId);
            return;
        }

        Inventory inv = Bukkit.createInventory(null, 27, TITLE_PREFIX + arenaId);

        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);
        for (int i = 0; i < 27; i++) inv.setItem(i, filler);

        // 1. Lobi Ayarla
        inv.setItem(10, makeItem(Material.BEACON, "§b§lLobi Belirle", List.of(
            "§7Bulunduğun konumu lobi",
            "§7spawn noktası olarak ayarlar.",
            " ",
            "§e▶ Ayarlamak için tıkla"
        )));

        // 2. Spectator Ayarla
        inv.setItem(11, makeItem(Material.ENDER_EYE, "§f§lİzleyici Spawnı", List.of(
            "§7Bulunduğun konumu izleyici",
            "§7spawn noktası olarak ayarlar.",
            " ",
            "§e▶ Ayarlamak için tıkla"
        )));

        // 3. Kapsül Ekle
        inv.setItem(12, makeItem(Material.GLASS, "§a§lKapsül Ekle", List.of(
            "§7Bulunduğun konumu bir kapsül",
            "§7noktası olarak listeye ekler.",
            " ",
            "§7Mevcut: §b" + arena.getCapsuleSpawns().size(),
            " ",
            "§e▶ Eklemek için tıkla"
        )));

        // 4. Shulker Ekle
        inv.setItem(13, makeItem(Material.SHULKER_BOX, "§d§lShulker Ekle", List.of(
            "§7Bulunduğun konumu bir shulker",
            "§7spawn noktası olarak listeye ekler.",
            " ",
            "§7Mevcut: §b" + arena.getShulkerSpawns().size(),
            " ",
            "§e▶ Eklemek için tıkla"
        )));

        // 5. Durum Değiştir
        boolean isDisabled = arena.getState() == GameState.DISABLED;
        inv.setItem(15, makeItem(isDisabled ? Material.REDSTONE_BLOCK : Material.EMERALD_BLOCK, 
            isDisabled ? "§c§lArena Kapalı" : "§a§lArena Açık", List.of(
            "§7Arenayı aktif/pasif hale getirir.",
            " ",
            "§7Şu an: " + (isDisabled ? "§cKAPALI" : "§aAÇIK"),
            " ",
            "§e▶ Değiştirmek için tıkla"
        )));

        // 6. Temizle
        inv.setItem(16, makeItem(Material.BARRIER, "§4§lListeleri Temizle", List.of(
            "§7Kapsül ve Shulker listelerini",
            "§7tamamen sıfırlar.",
            " ",
            "§e▶ Sıfırlamak için tıkla"
        )));

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 0.5f, 1f);
    }

    public static void handleClick(EnsDaire plugin, Player player, String arenaId, ItemStack item, int slot) {
        Arena arena = plugin.getArenaManager().get(arenaId);
        if (arena == null) return;

        if (item == null || item.getType() == Material.GRAY_STAINED_GLASS_PANE) return;

        switch (slot) {
            case 10 -> {
                arena.setLobbySpawn(player.getLocation());
                player.sendMessage("§a[+] Lobi noktası ayarlandı.");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.5f);
            }
            case 11 -> {
                arena.setSpectatorSpawn(player.getLocation());
                player.sendMessage("§a[+] İzleyici spawnı ayarlandı.");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.5f);
            }
            case 12 -> {
                arena.getCapsuleSpawns().add(player.getLocation());
                player.sendMessage("§a[+] Kapsül eklendi. (§e" + arena.getCapsuleSpawns().size() + "§a)");
                player.playSound(player.getLocation(), Sound.BLOCK_METAL_PLACE, 1f, 1f);
            }
            case 13 -> {
                arena.getShulkerSpawns().add(player.getLocation());
                player.sendMessage("§a[+] Shulker spawnı eklendi. (§e" + arena.getShulkerSpawns().size() + "§a)");
                player.playSound(player.getLocation(), Sound.BLOCK_WOOD_PLACE, 1f, 1f);
            }
            case 15 -> {
                if (arena.getState() == GameState.DISABLED) {
                    arena.setState(GameState.WAITING);
                    player.sendMessage("§aArena artık aktif!");
                } else {
                    arena.setState(GameState.DISABLED);
                    player.sendMessage("§cArena devre dışı bırakıldı.");
                }
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            }
            case 16 -> {
                arena.getCapsuleSpawns().clear();
                arena.getShulkerSpawns().clear();
                player.sendMessage("§eListeler temizlendi.");
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
            }
        }
        
        // Arenaları kaydet
        plugin.getArenaManager().saveArenas();
        // Menüyü tazele
        open(plugin, player, arenaId);
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
