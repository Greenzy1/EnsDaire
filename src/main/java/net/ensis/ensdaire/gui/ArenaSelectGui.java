package net.ensis.ensdaire.gui;

import net.ensis.ensdaire.EnsDaire;
import net.ensis.ensdaire.models.*;
import net.ensis.ensdaire.models.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ArenaSelectGui {

    public static final String TITLE = "§8§l« §bᴀʀᴇɴᴀ ꜱᴇçɪᴍɪ §8§l»";

    public static void open(EnsDaire plugin, Player player) {
        Collection<Arena> arenas = plugin.getArenaManager().all();
        int size = 27;
        Inventory inv = Bukkit.createInventory(null, size, TITLE);

        ItemStack filler = makeItem(Material.CYAN_STAINED_GLASS_PANE, " ", List.of());
        for (int i = 0; i < 9; i++)
            inv.setItem(i, filler);
        for (int i = 18; i < 27; i++)
            inv.setItem(i, filler);
        inv.setItem(9, filler);
        inv.setItem(17, filler);

        int slot = 10;
        for (Arena arena : arenas) {
            if (slot > 16)
                break;
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
            case WAITING -> {
                mat = Material.LIME_CONCRETE;
                statusLine = "§aʙᴇᴋʟᴇɴɪʏᴏʀ...";
            }
            case COUNTDOWN -> {
                mat = Material.YELLOW_CONCRETE;
                statusLine = "§eɢᴇʀɪ ꜱᴀʏɪᴍ!";
            }
            case STARTING, RUNNING, ROUND_END -> {
                mat = Material.RED_CONCRETE;
                statusLine = "§cᴏʏᴜɴ ᴅᴇᴠᴀᴍ ᴇᴅɪʏᴏʀ";
            }
            case DISABLED -> {
                mat = Material.BLACK_CONCRETE;
                statusLine = "§0ʙᴀᴋɪᴍᴅᴀ";
            }
            default -> {
                mat = Material.GRAY_CONCRETE;
                statusLine = "§7ʙɪʟɪɴᴍɪʏᴏʀ";
            }
        }

        List<String> lore = new ArrayList<>();
        lore.add("§8§m-----------------------");
        lore.add(" §7ᴅᴜʀᴜᴍ: " + statusLine);
        lore.add(" §7ᴏʏᴜɴᴄᴜ: §f" + arena.getPlayers().size() + "§8/§f" + arena.getMaxPlayers());

        if (state == GameState.RUNNING) {
            lore.add(" §7ʀᴏᴜɴᴅ: §e" + arena.getCurrentRound());
        }

        lore.add(" ");
        lore.add(" §7ᴋᴀᴘꜱüʟ: §b" + arena.getAllCapsuleSpawns().size());
        lore.add("§8§m-----------------------");

        if (state == GameState.WAITING || state == GameState.COUNTDOWN) {
            if (arena.getPlayers().size() >= arena.getMaxPlayers()) {
                lore.add(" §c✖ ᴀʀᴇɴᴀ ᴅᴏʟᴜ!");
            } else {
                lore.add(" §a▶ Katılmak için tıkla");
            }
        } else {
            lore.add(" §7Şu an katılamazsın.");
        }

        return makeItem(mat, "§b§lᴀʀᴇɴᴀ: §f" + arena.getId(), lore);
    }

    public static void handleClick(EnsDaire plugin, Player player, ItemStack item, int slot) {
        if (item == null || item.getType() == Material.CYAN_STAINED_GLASS_PANE)
            return;
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
            return;

        String displayName = item.getItemMeta().getDisplayName();
        if (!displayName.startsWith("§b§lᴀʀᴇɴᴀ: §f"))
            return;

        String arenaId = ChatColor.stripColor(displayName.replace("§b§lᴀʀᴇɴᴀ: §f", ""));
        Arena arena = plugin.getArenaManager().get(arenaId);

        if (arena == null) {
            player.closeInventory();
            return;
        }

        if (plugin.getArenaManager().getByPlayer(player.getUniqueId()) != null) {
            player.sendMessage(plugin.getLanguageManager().getMessage("prefix") + "§cZaten bir oyundasın!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            player.closeInventory();
            return;
        }

        JoinResult result = arena.addPlayer(player);
        String prefix = plugin.getLanguageManager().getMessage("prefix");

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
