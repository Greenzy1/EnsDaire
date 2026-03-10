package org.byauth.manager;

import org.byauth.ByCircleGame;
import org.byauth.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GuiManager {

    private final ByCircleGame plugin;

    public GuiManager(ByCircleGame plugin) {
        this.plugin = plugin;
    }

    public Inventory createInventoryFromConfig(String menuName) {
        FileConfiguration config = plugin.getSettingsManager().getMenuConfig(menuName);
        if (config == null)
            return null;

        String title = plugin.getSettingsManager().format(config.getString("title", "Menu"));
        int rows = config.getInt("rows", 3);
        Inventory inv = Bukkit.createInventory(null, rows * 9, title);

        // Fill background
        if (config.contains("filler")) {
            ItemStack filler = new ItemBuilder(
                    Material.valueOf(config.getString("filler.material", "GRAY_STAINED_GLASS_PANE")))
                    .setName(plugin.getSettingsManager().format(config.getString("filler.name", " ")))
                    .build();
            for (int i = 0; i < inv.getSize(); i++) {
                inv.setItem(i, filler);
            }
        }

        // Fill items
        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemKey = itemsSection.getConfigurationSection(key);
                Material material = Material.valueOf(itemKey.getString("material", "STONE"));
                int slot = itemKey.getInt("slot", 0);
                String name = plugin.getSettingsManager().format(itemKey.getString("name", "Item"));
                List<String> lore = itemKey.getStringList("lore");

                ItemStack item = new ItemBuilder(material)
                        .setName(name)
                        .setLore(lore.stream().map(plugin.getSettingsManager()::format).toList())
                        .build();

                inv.setItem(slot, item);
            }
        }

        return inv;
    }

    public Inventory createArenaManagerInventory() {
        Inventory inv = createInventoryFromConfig("arena_manager");
        if (inv == null)
            return null;

        int slot = 0;
        for (org.byauth.game.Arena arena : plugin.getArenaController().getArenas()) {
            if (slot >= 45)
                break; // Avoid overflow

            String color = arena.getState() == org.byauth.game.ArenaState.WAITING ? "&a"
                    : arena.getState() == org.byauth.game.ArenaState.STARTING ? "&e" : "&c";

            ItemStack item = new ItemBuilder(Material.PAPER)
                    .setName(plugin.getSettingsManager().format(color + arena.getId()))
                    .setLore(List.of(
                            plugin.getSettingsManager().format("&7Durum: " + color + arena.getState()),
                            plugin.getSettingsManager().format(
                                    "&7Oyuncular: &f" + arena.getPlayers().size() + "/" + arena.getMaxPlayers()),
                            plugin.getSettingsManager().format("&7Mod: &f" + arena.getType()),
                            "",
                            plugin.getSettingsManager().format("&eYönetmek için tıkla!")))
                    .build();
            inv.setItem(slot++, item);
        }
        return inv;
    }

    public Inventory createOnlinePlayersInventory() {
        Inventory inv = Bukkit.createInventory(null, 54, plugin.getSettingsManager().format("&8Oyuncu Seç"));

        int slot = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (slot >= 54)
                break;
            ItemStack skull = new ItemBuilder(Material.PLAYER_HEAD)
                    .setName("&e" + p.getName())
                    .setLore(List.of("&7İstatistiklerini düzenlemek için tıkla."))
                    .build();
            inv.setItem(slot++, skull);
        }
        return inv;
    }

    public void openTeamGUI(Player player, org.byauth.game.Arena arena) {
        String title = plugin.getSettingsManager()
                .format(plugin.getSettingsManager().getMessage("gui.team-select-title"));
        Inventory inv = Bukkit.createInventory(null, 27, title);

        int slot = 10;
        for (org.byauth.game.Team team : org.byauth.game.Team.values()) {
            if (slot > 16)
                break;
            inv.setItem(slot++, new ItemBuilder(team.getConcreteMaterial())
                    .setName("&f" + team.getDisplayName())
                    .setLore(List.of("&7Bu takıma katılmak için tıkla."))
                    .build());
        }

        inv.setItem(22, new ItemBuilder(Material.BARRIER).setName("&cTakımdan Ayrıl").build());
        player.openInventory(inv);
    }
}
