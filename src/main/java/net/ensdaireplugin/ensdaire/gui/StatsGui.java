package net.ensdaireplugin.ensdaire.gui;

import net.ensdaireplugin.ensdaire.EnsDaire;
import net.ensdaireplugin.ensdaire.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class StatsGui {

    public static final String TITLE = "§8§l✦ §bİstatistiklerim §8§l✦";

    public static void open(EnsDaire plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);
        PlayerData data = plugin.getPlayerDataManager().get(player.getUniqueId());

        // Kafa
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta sm = (SkullMeta) skull.getItemMeta();
        sm.setOwningPlayer(player);
        sm.setDisplayName("§b§l" + player.getName());
        sm.setLore(List.of("§7Rütbe: §f" + data.getRank()));
        skull.setItemMeta(sm);
        inv.setItem(4, skull);

        // Jeton
        inv.setItem(10, makeItem(Material.GOLD_NUGGET, "§6Jetonlar",
            List.of("§7Toplam jeton:", "§e" + data.getTokens() + " §6✦")));

        // Kazanma
        inv.setItem(11, makeItem(Material.GOLDEN_SWORD, "§eKazanmalar",
            List.of("§7Toplam kazanma:", "§e" + data.getWins(),
                    "§7Oran: §e" + String.format("%.1f%%", data.getWinRate()))));

        // Oyun sayısı
        inv.setItem(12, makeItem(Material.COMPASS, "§7Oyunlar",
            List.of("§7Toplam oyun:", "§e" + data.getGames())));

        // Öldürmeler
        inv.setItem(13, makeItem(Material.IRON_SWORD, "§cÖldürmeler",
            List.of("§7Toplam öldürme:", "§e" + data.getKills(),
                    "§7Oyun başı: §e" + String.format("%.2f", data.getKpg()))));

        // Rütbe
        inv.setItem(14, makeItem(Material.NETHER_STAR, "§5Rütbe",
            List.of("§7Mevcut rütbe:", "§f" + data.getRank(),
                    "", "§8Bir sonraki rütbe için daha fazla jeton kazan!")));

        // Dolgu
        ItemStack fill = makeItem(Material.BLACK_STAINED_GLASS_PANE, " ", List.of());
        for (int i = 0; i < 27; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, fill);
        }

        player.openInventory(inv);
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
