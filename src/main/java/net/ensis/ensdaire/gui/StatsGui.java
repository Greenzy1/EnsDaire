package net.ensis.ensdaire.gui;

import net.ensis.ensdaire.EnsDaire;
import net.ensis.ensdaire.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class StatsGui {

    public static final String TITLE = "§8§l« §bɪꜱᴛᴀᴛɪꜱᴛɪᴋʟᴇʀɪᴍ §8§l»";

    public static void open(EnsDaire plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);
        PlayerData data = plugin.getPlayerDataManager().get(player.getUniqueId());

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta sm = (SkullMeta) skull.getItemMeta();
        sm.setOwningPlayer(player);
        sm.setDisplayName("§b§l" + player.getName());
        sm.setLore(List.of("§7ʀüᴛʙᴇ: §f" + data.getRank()));
        skull.setItemMeta(sm);
        inv.setItem(4, skull);

        inv.setItem(10, makeItem(Material.GOLD_NUGGET, "§6ᴊᴇᴛᴏɴʟᴀʀ",
                List.of("§7Toplam jeton:", "§e" + data.getTokens() + " §6✦")));

        inv.setItem(11, makeItem(Material.GOLDEN_SWORD, "§eᴋᴀᴢᴀɴᴍᴀʟᴀʀ",
                List.of("§7Toplam kazanma:", "§e" + data.getWins(),
                        "§7Oran: §e" + String.format("%.1f%%", data.getWinRate()))));

        inv.setItem(12, makeItem(Material.COMPASS, "§7ᴏʏᴜɴʟᴀʀ",
                List.of("§7Toplam oyun:", "§e" + data.getGames())));

        inv.setItem(13, makeItem(Material.IRON_SWORD, "§cöʟᴅüʀᴍᴇʟᴇʀ",
                List.of("§7Toplam öldürme:", "§e" + data.getKills(),
                        "§7K/D: §e" + data.getKD())));

        inv.setItem(14, makeItem(Material.NETHER_STAR, "§5ʀüᴛʙᴇ",
                List.of("§7Mevcut rütbe:", "§f" + data.getRank(),
                        "", "§8Bir sonraki rütbe için daha fazla jeton kazan!")));

        ItemStack fill = makeItem(Material.BLACK_STAINED_GLASS_PANE, " ", List.of());
        for (int i = 0; i < 27; i++) {
            if (inv.getItem(i) == null)
                inv.setItem(i, fill);
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
