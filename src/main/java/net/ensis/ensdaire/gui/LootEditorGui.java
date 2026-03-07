package net.ensis.ensdaire.gui;

import net.ensis.ensdaire.EnsDaire;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LootEditorGui {

    public static final String TITLE = "§8§l« §bʟᴏᴏᴛ ᴇᴅɪᴛöʀü §8§l»";

    public static void open(EnsDaire plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE);

        File file = new File(plugin.getDataFolder(), "loot.yml");
        if (file.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            List<String> serialized = config.getStringList("items");
            for (String s : serialized) {
                ItemStack item = net.ensis.ensdaire.utils.Base64Utils.decode(s);
                if (item != null)
                    inv.addItem(item);
            }
        }

        player.openInventory(inv);
        player.sendMessage(plugin.getLanguageManager().getMessage("prefix")
                + "§eEşyaları koyun ve menüyü kapatın. Otomatik kaydedilecektir.");
    }

    public static void save(EnsDaire plugin, Inventory inv) {
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack is : inv.getContents()) {
            if (is != null && is.getType() != Material.AIR)
                items.add(is);
        }
        plugin.getArenaManager().all().stream().findFirst().ifPresent(arena -> {
            arena.getShulkerManager().saveLoot(items);
        });
    }
}
