package net.ensis.ensdaire.gui;

import net.ensis.ensdaire.EnsDaire;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class AdminPanelGui {

    public static final String TITLE = "§8§l« §cᴀᴅᴍɪɴ ᴘᴀɴᴇʟɪ §8§l»";

    public static void open(EnsDaire plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);

        inv.setItem(10, buildItem(Material.IRON_SWORD, "§e§lᴀʀᴇɴᴀʟᴀʀ",
                "§7Sunucudaki arenaları oluştur,", "§7düzenle veya sil."));

        inv.setItem(12, buildItem(Material.RED_DYE, "§c§lʀᴇɴᴋ & ʙʟᴏᴋʟᴀʀ",
                "§7Hangi rengin hangi blokta", "§7yürüyeceğini buradan ayarla."));

        inv.setItem(14, buildItem(Material.CHEST, "§6§lʟᴏᴏᴛ ᴇᴅɪᴛöʀü",
                "§7Shulker kutularından çıkacak", "§7eşyaları belirle."));

        inv.setItem(16, buildItem(Material.COMPARATOR, "§a§lꜱɪꜱᴛᴇᴍ",
                "§7Dosyaları yenile ve", "§7eklenti durumunu kontrol et."));

        ItemStack filler = buildItem(Material.GRAY_STAINED_GLASS_PANE, " ", "");
        for (int i = 0; i < 27; i++) {
            if (inv.getItem(i) == null)
                inv.setItem(i, filler);
        }

        player.openInventory(inv);
    }

    private static ItemStack buildItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        List<String> l = new ArrayList<>();
        for (String s : lore)
            l.add(s);
        meta.setLore(l);
        item.setItemMeta(meta);
        return item;
    }

    public static void handleClick(EnsDaire plugin, Player player, ItemStack item) {
        if (item == null || item.getType() == Material.AIR)
            return;
        String name = item.getItemMeta().getDisplayName();

        if (name.contains("ᴀʀᴇɴᴀʟᴀʀ")) {
            ArenaListGui.open(plugin, player);
        } else if (name.contains("ʀᴇɴᴋ")) {
            ColorBlockGui.open(plugin, player);
        } else if (name.contains("ʟᴏᴏᴛ")) {
            LootEditorGui.open(plugin, player);
        } else if (name.contains("ꜱɪꜱᴛᴇᴍ")) {
            plugin.reloadConfig();
            plugin.getLanguageManager().loadLanguages();
            player.sendMessage(
                    plugin.getLanguageManager().getMessage("prefix") + "§aTüm dosyalar başarıyla yenilendi.");
            player.closeInventory();
        }
    }
}
