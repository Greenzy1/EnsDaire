package org.byauth.manager;

import org.byauth.EnsDaire;
import org.byauth.game.Arena;
import org.byauth.game.ArenaState;
import org.byauth.game.Team;
import org.byauth.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GuiManager {

    private final EnsDaire plugin;

    public GuiManager(EnsDaire plugin) {
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
            inv = Bukkit.createInventory(null, 54, "Arena Yönetimi");

        int slot = 0;
        for (Arena arena : plugin.getArenaController().getArenas()) {
            if (slot >= 45)
                break;

            String color = arena.getState() == ArenaState.WAITING ? "&a"
                    : arena.getState() == ArenaState.STARTING ? "&e" : "&c";

            ItemStack item = new ItemBuilder(Material.PAPER)
                    .setName(plugin.getSettingsManager().format(color + arena.getId()))
                    .setLore(List.of(
                            plugin.getSettingsManager().format("&7Durum: " + color + arena.getState()),
                            plugin.getSettingsManager().format(
                                    "&7Oyuncular: &f" + arena.getPlayers().size() + "/" + arena.getMaxPlayers()),
                            plugin.getSettingsManager().format("&7Mod: &f" + arena.getType()),
                            "",
                            plugin.getSettingsManager().format("&eDüzenlemek için tıkla!")))
                    .build();
            inv.setItem(slot++, item);
        }
        
        inv.setItem(49, new ItemBuilder(Material.NETHER_STAR).setName("&bYeni Arena Oluştur").build());
        inv.setItem(53, new ItemBuilder(Material.ARROW).setName("&cGeri Dön").build());
        
        return inv;
    }

    public Inventory createArenaEditorInventory(Arena arena) {
        Inventory inv = Bukkit.createInventory(null, 36, plugin.getSettingsManager().format("&8Düzenle: &b" + arena.getId()));
        
        // Fill background
        ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build();
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, filler);

        inv.setItem(10, new ItemBuilder(Material.NAME_TAG).setName("&eGörünen Ad")
                .setLore(List.of("&7Şu anki: &f" + arena.getDisplayName(), "", "&eDeğiştirmek için tıkla.")).build());
        
        inv.setItem(11, new ItemBuilder(Material.COMPASS).setName("&eLobi Konumu")
                .setLore(List.of("&7Bulunduğunuz yeri lobi yapar.", "", "&eAyarlamak için tıkla.")).build());
        
        inv.setItem(12, new ItemBuilder(Material.BEACON).setName("&eMerkez Konumu")
                .setLore(List.of("&7Bulunduğunuz yeri merkez yapar.", "", "&eAyarlamak için tıkla.")).build());
        
        inv.setItem(13, new ItemBuilder(Material.IRON_INGOT).setName("&eTakım Boyutu")
                .setLore(List.of("&7Şu anki: &f" + arena.getTeamSize(), "", "&eArtırmak için Sol, Azaltmak için Sağ tık.")).build());

        inv.setItem(14, new ItemBuilder(Material.CLOCK).setName("&eTur Süreleri")
                .setLore(List.of("&7Tur sürelerini yapılandır.", "", "&eDüzenlemek için tıkla.")).build());

        inv.setItem(15, new ItemBuilder(Material.REPEATER).setName("&eDurum")
                .setLore(List.of("&7Şu anki: &f" + arena.getState(), "", "&eDeğiştirmek için tıkla.")).build());

        inv.setItem(31, new ItemBuilder(Material.ARROW).setName("&cGeri Dön").build());
        inv.setItem(35, new ItemBuilder(Material.BARRIER).setName("&4ARENAYI SİL").build());

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

    public void openTeamGUI(Player player, Arena arena) {
        String title = plugin.getSettingsManager()
                .format(plugin.getSettingsManager().getMessage("gui.team-select-title"));
        Inventory inv = Bukkit.createInventory(null, 27, title);

        int slot = 10;
        for (Team team : Team.values()) {
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
