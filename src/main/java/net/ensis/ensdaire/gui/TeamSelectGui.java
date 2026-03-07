package net.ensis.ensdaire.gui;

import net.ensis.ensdaire.EnsDaire;
import net.ensis.ensdaire.models.Arena;
import net.ensis.ensdaire.models.CircleColor;
import net.ensis.ensdaire.models.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TeamSelectGui {

    public static final String TITLE = "§8§l« §bʀᴇɴᴋ ꜱᴇç §8§l»";

    public static void open(EnsDaire plugin, Player player, Arena arena) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);

        Set<CircleColor> takenColors = arena.getPlayers().values().stream()
                .map(GamePlayer::getColor)
                .filter(c -> c != null)
                .collect(Collectors.toSet());

        int slot = 0;
        for (CircleColor color : CircleColor.values()) {
            if (slot >= 27)
                break;
            if (color == CircleColor.WHITE)
                continue;
            boolean taken = takenColors.contains(color);
            inv.setItem(slot++, buildColorItem(color, taken));
        }

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1f, 1f);
    }

    private static ItemStack buildColorItem(CircleColor color, boolean taken) {
        Material mat = color.getMaterial();
        List<String> lore = new ArrayList<>();
        lore.add(" ");
        if (taken) {
            lore.add(" §c✖ ʙᴜ ʀᴇɴᴋ ᴅᴏʟᴜ!");
        } else {
            lore.add(" §a▶ Seçmek için tıkla");
        }

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color.getDisplayName() + " §7ᴛᴀᴋɪᴍɪ");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static void handleClick(EnsDaire plugin, Player player, Arena arena, ItemStack item) {
        if (item == null || item.getType() == Material.AIR)
            return;

        CircleColor selected = null;
        for (CircleColor c : CircleColor.values()) {
            if (c.getMaterial() == item.getType()) {
                selected = c;
                break;
            }
        }

        if (selected == null)
            return;

        boolean taken = false;
        for (GamePlayer gp : arena.getPlayers().values()) {
            if (gp.getUuid().equals(player.getUniqueId()))
                continue;
            if (gp.getColor() == selected) {
                taken = true;
                break;
            }
        }

        if (taken) {
            player.sendMessage(plugin.getLanguageManager().getMessage("prefix") + "§cBu renk zaten alınmış!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return;
        }

        GamePlayer gp = arena.getGamePlayer(player.getUniqueId());
        if (gp != null) {
            gp.setColor(selected);
            player.sendMessage(plugin.getLanguageManager().getMessage("prefix") + "§aTakımın başarıyla seçildi: "
                    + selected.getDisplayName());
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
            player.closeInventory();
        }
    }
}
