package net.ensis.ensdaire.gui;

import net.ensis.ensdaire.EnsDaire;
import net.ensis.ensdaire.models.CircleColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ColorBlockGui {

    public static final String TITLE = "§8§l« §eʀᴇɴᴋ & ʙʟᴏᴋ §8§l»";

    public static void open(EnsDaire plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);

        for (CircleColor color : CircleColor.values()) {
            inv.addItem(buildColorItem(color));
        }

        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta meta = info.getItemMeta();
        meta.setDisplayName("§b§lɴᴀꜱɪʟ ᴋᴜʟʟᴀɴɪʟɪʀ?");
        List<String> lore = new ArrayList<>();
        lore.add("§7Bir renge tıkla, ardından");
        lore.add("§7envanterinden o rengin olmasını");
        lore.add("§7istediğin bloğu koy.");
        meta.setLore(lore);
        info.setItemMeta(meta);
        inv.setItem(26, info);

        player.openInventory(inv);
    }

    private static ItemStack buildColorItem(CircleColor color) {
        ItemStack item = new ItemStack(color.getMaterial());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color.getDisplayName());
        List<String> lore = new ArrayList<>();
        lore.add("§7ᴍᴇᴠᴄᴜᴛ ʙʟᴏᴋ: §f" + color.getMaterial().name());
        lore.add("");
        lore.add("§e▶ Değiştirmek için tıkla");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static void handleClick(EnsDaire plugin, Player player, ItemStack currentItem, ItemStack cursor) {
        if (currentItem == null || currentItem.getType() == Material.AIR)
            return;

        CircleColor selectedColor = null;
        for (CircleColor c : CircleColor.values()) {
            if (c.getDisplayName().equals(currentItem.getItemMeta().getDisplayName())) {
                selectedColor = c;
                break;
            }
        }

        if (selectedColor == null)
            return;

        if (cursor != null && cursor.getType() != Material.AIR && cursor.getType().isBlock()) {
            selectedColor.setMaterial(cursor.getType());
            plugin.getConfig().set("colors." + selectedColor.name(), cursor.getType().name());
            plugin.saveConfig();
            player.sendMessage(plugin.getLanguageManager().getMessage("prefix") + "§a" + selectedColor.name()
                    + " rengi güncellendi: §f" + cursor.getType().name());
            open(plugin, player);
        } else {
            player.sendMessage(
                    plugin.getLanguageManager().getMessage("prefix") + "§eLütfen imlecinize bir blok alıp tıklayın.");
        }
    }
}
