package net.ensis.ensdaire.gui;

import net.ensis.ensdaire.EnsDaire;
import net.ensis.ensdaire.models.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ArenaListGui {

    public static final String TITLE = "§8§l« §bᴀʀᴇɴᴀ ʏöɴᴇᴛɪᴍɪ §8§l»";
    private static final NamespacedKey ARENA_KEY = new NamespacedKey(EnsDaire.getInstance(), "arena_id");
    private static final NamespacedKey ACTION_KEY = new NamespacedKey(EnsDaire.getInstance(), "gui_action");

    public static void open(EnsDaire plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE);

        ItemStack filler = buildItem(Material.GRAY_STAINED_GLASS_PANE, " ", "");
        for (int i = 0; i < 54; i++)
            inv.setItem(i, filler);

        inv.setItem(4, buildActionItem(Material.NETHER_STAR, "§b§l✚ ʏᴇɴɪ ᴀʀᴇɴᴀ", "create_new",
                "§7Tıkla ve yeni bir arena başlat.", "§7Oluşturduktan sonra listeden",
                "§7ayarlarını yapabilirsin."));

        int slot = 10;
        for (Arena arena : plugin.getArenaManager().all()) {
            if (slot > 43)
                break;
            if (slot % 9 == 8)
                slot += 2;
            inv.setItem(slot++, buildArenaItem(arena));
        }

        player.openInventory(inv);
    }

    private static ItemStack buildArenaItem(Arena arena) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§eᴀʀᴇɴᴀ: §b" + arena.getId());

        List<String> lore = new ArrayList<>();
        lore.add("§8§m-----------------------");
        lore.add(" §fᴅᴜʀᴜᴍ: " + arena.getState().name());
        lore.add(" §fᴏʏᴜɴᴄᴜ: §e" + arena.getPlayers().size() + "/" + arena.getMaxPlayers());
        lore.add(" §fʟᴏʙɪ: " + (arena.getLobbySpawn() != null ? "§a✔" : "§c✖"));
        lore.add(" §fᴋᴀᴘꜱüʟ: §e" + arena.getAllCapsuleSpawns().size());
        lore.add("§8§m-----------------------");
        lore.add("§b▶ Düzenlemek için tıkla");

        meta.setLore(lore);
        meta.getPersistentDataContainer().set(ARENA_KEY, PersistentDataType.STRING, arena.getId());
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack buildActionItem(Material mat, String name, String action, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        List<String> l = new ArrayList<>();
        for (String s : lore)
            l.add(s);
        meta.setLore(l);
        meta.getPersistentDataContainer().set(ACTION_KEY, PersistentDataType.STRING, action);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack buildItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            List<String> l = new ArrayList<>();
            for (String s : lore)
                l.add(s);
            meta.setLore(l);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static void handleClick(EnsDaire plugin, Player player, ItemStack item) {
        if (item == null || item.getItemMeta() == null)
            return;
        ItemMeta meta = item.getItemMeta();

        String action = meta.getPersistentDataContainer().get(ACTION_KEY, PersistentDataType.STRING);
        if (action != null && action.equals("create_new")) {
            player.closeInventory();
            player.sendMessage(plugin.getLanguageManager().getMessage("prefix")
                    + "§eLütfen sohbete arena ismini yazın veya §f/ed admin create <isim> §eyazın.");
            return;
        }

        String arenaId = meta.getPersistentDataContainer().get(ARENA_KEY, PersistentDataType.STRING);
        if (arenaId != null) {
            ArenaEditorGui.open(plugin, player, arenaId);
        }
    }
}
