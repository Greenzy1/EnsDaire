package net.ensis.ensdaire.gui;

import net.ensis.ensdaire.EnsDaire;
import net.ensis.ensdaire.models.Arena;
import net.ensis.ensdaire.models.CircleColor;
import net.ensis.ensdaire.models.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ArenaEditorGui {

    public static final String TITLE_PREFIX = "§8§l« §bᴀʀᴇɴᴀ ᴇᴅɪᴛöʀü §8· §f";

    public static void open(EnsDaire plugin, Player player, String arenaId) {
        Arena arena = plugin.getArenaManager().get(arenaId);
        if (arena == null)
            return;

        Inventory inv = Bukkit.createInventory(null, 36, TITLE_PREFIX + arenaId);

        inv.setItem(10, buildItem(arena.getState() == GameState.DISABLED ? Material.RED_DYE : Material.LIME_DYE,
                "§eᴀʀᴇɴᴀ ᴅᴜʀᴜᴍᴜ",
                "§7Şu an: " + (arena.getState() == GameState.DISABLED ? "§cᴋᴀᴘᴀʟɪ" : "§aᴀçɪᴋ"),
                "", "§b▶ Tıkla ve Değiştir"));

        inv.setItem(12, buildItem(Material.BEACON,
                "§eʟᴏʙɪ ᴋᴏɴᴜᴍᴜ",
                "§7Konum: §f" + (arena.getLobbySpawn() != null ? "§a✔ ᴀʏᴀʀʟᴀɴᴅɪ" : "§c✖ ᴀʏᴀʀʟᴀɴᴍᴀᴅɪ"),
                "", "§b▶ Bulunduğun yeri lobi yap"));

        inv.setItem(13, buildItem(Material.ENDER_EYE,
                "§eɪᴢʟᴇʏɪᴄɪ ᴋᴏɴᴜᴍᴜ",
                "§7Konum: §f" + (arena.getSpectatorSpawn() != null ? "§a✔ ᴀʏᴀʀʟᴀɴᴅɪ" : "§c✖ ᴀʏᴀʀʟᴀɴᴍᴀᴅɪ"),
                "", "§b▶ Bulunduğun yeri izleyici spawnı yap"));

        int colorSlot = 18;
        for (CircleColor color : CircleColor.values()) {
            if (colorSlot > 26)
                break;
            inv.setItem(colorSlot++, buildItem(color.getMaterial(),
                    color.getDisplayName() + " §7ꜱᴘᴀᴡɴ",
                    "§7Konum: §f" + (arena.getCapsuleSpawn(color) != null ? "§a✔ ᴀʏᴀʀʟᴀɴᴅɪ" : "§c✖ ᴀʏᴀʀʟᴀɴᴍᴀᴅɪ"),
                    "", "§b▶ Bulunduğun yeri " + color.name() + " spawnı yap"));
        }

        inv.setItem(16, buildItem(Material.CHEST,
                "§6ꜱʜᴜʟᴋᴇʀ ʟᴏᴏᴛ ᴇᴅɪᴛöʀü",
                "§7Shulkerlardan çıkacak eşyaları",
                "§7buradan görsel olarak düzenle.",
                "", "§b▶ Tıkla ve Düzenle"));

        inv.setItem(22, buildItem(Material.NETHER_STAR,
                "§a§lᴏʏᴜɴᴜ ʙᴀꜱʟᴀᴛ",
                "§7Geri sayımı atla ve",
                "§7oyunu anında başlat.",
                "", "§b▶ Tıkla ve Başlat"));

        inv.setItem(31, buildItem(Material.BARRIER, "§cᴍᴇɴüʏü ᴋᴀᴘᴀᴛ", "§7Değişiklikler anlık kaydedilir."));

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

    public static void handleClick(EnsDaire plugin, Player player, String arenaId, int slot,
            org.bukkit.event.inventory.ClickType clickType) {
        Arena arena = plugin.getArenaManager().get(arenaId);
        if (arena == null)
            return;

        if (slot >= 18 && slot <= 26) {
            CircleColor[] colors = CircleColor.values();
            int colorIndex = slot - 18;
            if (colorIndex < colors.length) {
                CircleColor color = colors[colorIndex];
                arena.setCapsuleSpawn(color, player.getLocation());
                player.sendMessage(plugin.getLanguageManager().getMessage("prefix") + "§a" + color.name()
                        + " takımı için spawn noktası ayarlandı.");
                open(plugin, player, arenaId);
            }
        }

        switch (slot) {
            case 10 -> {
                arena.setState(arena.getState() == GameState.DISABLED ? GameState.WAITING : GameState.DISABLED);
                player.sendMessage(plugin.getLanguageManager().getMessage("prefix") + "§aArena durumu güncellendi.");
                open(plugin, player, arenaId);
            }
            case 12 -> {
                arena.setLobbySpawn(player.getLocation());
                player.sendMessage(plugin.getLanguageManager().getMessage("prefix") + "§aLobi konumu kaydedildi.");
                open(plugin, player, arenaId);
            }
            case 13 -> {
                arena.setSpectatorSpawn(player.getLocation());
                player.sendMessage(plugin.getLanguageManager().getMessage("prefix") + "§aİzleyici konumu kaydedildi.");
                open(plugin, player, arenaId);
            }
            case 16 -> LootEditorGui.open(plugin, player);
            case 22 -> {
                arena.startGame();
                player.closeInventory();
            }
            case 31 -> player.closeInventory();
        }
        plugin.getArenaManager().saveArenas();
    }
}
