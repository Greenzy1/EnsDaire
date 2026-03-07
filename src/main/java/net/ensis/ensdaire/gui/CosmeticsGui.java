package net.ensis.ensdaire.gui;

import net.ensis.ensdaire.EnsDaire;
import net.ensis.ensdaire.models.CosmeticType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CosmeticsGui {

    public static final String MAIN_TITLE = "§8§l« §dᴋᴏᴢᴍᴇᴛɪᴋ ᴍᴇɴüꜱü §8§l»";
    public static final String CAT_TITLE_PREFIX = "§8§l« §dᴋᴀᴛᴇɢᴏʀɪ: §f";

    public static void openMain(EnsDaire plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, MAIN_TITLE);

        inv.setItem(10, buildCategoryItem(CosmeticType.Category.TRAIL, Material.BLAZE_POWDER));
        inv.setItem(12, buildCategoryItem(CosmeticType.Category.KILL, Material.TNT));
        inv.setItem(14, buildCategoryItem(CosmeticType.Category.PROJECTILE, Material.ARROW));
        inv.setItem(16, buildCategoryItem(CosmeticType.Category.WIN, Material.NETHER_STAR));

        ItemStack filler = buildItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 27; i++) {
            if (inv.getItem(i) == null)
                inv.setItem(i, filler);
        }

        player.openInventory(inv);
    }

    public static void openCategory(EnsDaire plugin, Player player, CosmeticType.Category category) {
        Inventory inv = Bukkit.createInventory(null, 45, CAT_TITLE_PREFIX + category.getDisplayName());

        for (CosmeticType type : CosmeticType.values()) {
            if (type.getCategory() == category) {
                boolean owned = plugin.getCosmeticManager().hasCosmetic(player.getUniqueId(), type);
                inv.addItem(buildCosmeticItem(type, owned));
            }
        }

        inv.setItem(40, buildItem(Material.ARROW, "§eɢᴇʀɪ ᴅöɴ", "§7Ana menüye döner."));
        player.openInventory(inv);
    }

    private static ItemStack buildCategoryItem(CosmeticType.Category cat, Material mat) {
        return buildItem(mat, "§d" + cat.getDisplayName(), "§7Bu kategoriye ait tüm",
                "§7kozmetikleri görmek için tıkla.");
    }

    private static ItemStack buildCosmeticItem(CosmeticType type, boolean owned) {
        ItemStack item = new ItemStack(type.getIcon());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName((owned ? "§a" : "§c") + type.getName());
        List<String> lore = new ArrayList<>();
        lore.add("§7" + type.getDesc());
        lore.add("");
        if (owned) {
            lore.add("§a✔ ꜱᴀʜɪᴘꜱɪɴ");
            lore.add("§e▶ Kuşanmak için tıkla");
        } else {
            lore.add("§c✖ ꜱᴀʜɪᴘ ᴅᴇĞɪʟꜱɪɴ");
            lore.add("§eꜰɪʏᴀᴛ: §6" + type.getCost() + " ᴊᴇᴛᴏɴ");
            lore.add("§b▶ Satın almak için tıkla");
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
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

    public static void handleClick(EnsDaire plugin, Player player, ItemStack item, String title) {
        if (item == null || item.getType() == Material.AIR || item.getType() == Material.GRAY_STAINED_GLASS_PANE)
            return;
        String name = item.getItemMeta().getDisplayName();

        if (title.equals(MAIN_TITLE)) {
            for (CosmeticType.Category cat : CosmeticType.Category.values()) {
                if (name.contains(cat.getDisplayName())) {
                    openCategory(plugin, player, cat);
                    return;
                }
            }
        } else if (title.startsWith(CAT_TITLE_PREFIX)) {
            if (name.contains("ɢᴇʀɪ ᴅöɴ")) {
                openMain(plugin, player);
                return;
            }

            CosmeticType selected = null;
            for (CosmeticType t : CosmeticType.values()) {
                if (name.contains(t.getName())) {
                    selected = t;
                    break;
                }
            }

            if (selected == null)
                return;

            if (plugin.getCosmeticManager().hasCosmetic(player.getUniqueId(), selected)) {
                plugin.getCosmeticManager().equip(player, selected);
                player.closeInventory();
            } else {
                long tokens = plugin.getPlayerDataManager().getTokens(player.getUniqueId());
                if (tokens >= selected.getCost()) {
                    plugin.getPlayerDataManager().addTokens(player.getUniqueId(), -selected.getCost());
                    plugin.getCosmeticManager().addCosmetic(player.getUniqueId(), selected);
                    player.sendMessage(
                            plugin.getLanguageManager().getMessage("prefix") + "§aSatın alındı: " + selected.getName());
                    openCategory(plugin, player, selected.getCategory());
                } else {
                    player.sendMessage(plugin.getLanguageManager().getMessage("prefix")
                            + "§cYetersiz jeton! Gereken: §e" + selected.getCost());
                }
            }
        }
    }
}
