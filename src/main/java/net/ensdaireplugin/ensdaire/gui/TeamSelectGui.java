package net.ensdaireplugin.ensdaire.gui;

import net.ensdaireplugin.ensdaire.EnsDaire;
import net.ensdaireplugin.ensdaire.arena.Arena;
import net.ensdaireplugin.ensdaire.game.CircleColor;
import net.ensdaireplugin.ensdaire.game.GamePlayer;
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

    public static final String TITLE = "§8§l✦ §bTakım/Renk Seç §8§l✦";

    public static void open(EnsDaire plugin, Player player, Arena arena) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);
        
        Set<CircleColor> takenColors = arena.getPlayers().values().stream()
            .map(GamePlayer::getColor)
            .filter(c -> c != null)
            .collect(Collectors.toSet());

        int slot = 0;
        for (CircleColor color : CircleColor.values()) {
            if (slot >= 27) break;
            if (color == CircleColor.WHITE) continue; // Beyaz elenenler içindir

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
            lore.add(" §c✖ Bu renk dolu!");
        } else {
            lore.add(" §a▶ Seçmek için tıkla");
        }
        
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color.getDisplayName() + " Takımı");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static void handleClick(EnsDaire plugin, Player player, Arena arena, ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return;
        
        CircleColor selected = null;
        for (CircleColor c : CircleColor.values()) {
            if (c.getMaterial() == item.getType()) {
                selected = c;
                break;
            }
        }

        if (selected == null) return;

        // Rengin dolu olup olmadığını kontrol et
        boolean taken = false;
        for (GamePlayer gp : arena.getPlayers().values()) {
            if (gp.getUuid().equals(player.getUniqueId())) continue;
            if (gp.getColor() == selected) {
                taken = true;
                break;
            }
        }

        if (taken) {
            player.sendMessage("§cBu renk zaten alınmış!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return;
        }

        GamePlayer gp = arena.getGamePlayer(player.getUniqueId());
        if (gp != null) {
            gp.setColor(selected);
            player.sendMessage("§aTakımın başarıyla seçildi: " + selected.getDisplayName());
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
            player.closeInventory();
        }
    }
}
