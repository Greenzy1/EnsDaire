package org.byauth.controller;

import org.byauth.EnsDaire;
import org.byauth.game.Arena;
import org.byauth.game.ArenaState;
import org.byauth.game.Team;
import org.byauth.utils.ItemBuilder;
import org.byauth.utils.SettingsManager;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class ArenaController {

    private final EnsDaire plugin;
    private final SettingsManager settings;
    private final Map<String, Arena> arenas = new HashMap<>();
    private final Map<UUID, Arena> playerArenas = new HashMap<>();
    private final Map<UUID, Arena> spectators = new HashMap<>();

    public ArenaController(EnsDaire plugin) {
        this.plugin = plugin;
        this.settings = plugin.getSettingsManager();
        loadArenas();
    }

    public void loadArenas() {
        if (settings.getArenaNames() == null) return;
        for (String arenaName : settings.getArenaNames()) {
            getOrCreateArena(arenaName);
        }
    }

    public List<Arena> getArenas() {
        return new ArrayList<>(arenas.values());
    }

    public Arena getOrCreateArena(String name) {
        return arenas.computeIfAbsent(name, n -> new Arena(plugin, n));
    }

    public void addPlayer(Player player, Arena arena) {
        if (arena.getState() != ArenaState.WAITING && arena.getState() != ArenaState.STARTING) {
            player.sendMessage(settings.format(settings.getMessage("error.arena-not-found")));
            return;
        }
        
        if (arena.getPlayers().size() >= arena.getMaxPlayers()) {
            player.sendMessage(settings.format(settings.getMessage("error.arena-full")));
            return;
        }

        playerArenas.put(player.getUniqueId(), arena);
        arena.getPlayers().add(player.getUniqueId());
        
        var msg = settings.getMessage("game.player-join").replace("%player%", player.getName());
        broadcastArenaMessage(arena, msg);
        
        if (arena.getLobbyLocation() != null) {
            player.teleport(arena.getLobbyLocation());
        }
        
        giveLobbyItems(player);
    }

    public void removePlayer(Player player) {
        Arena arena = playerArenas.remove(player.getUniqueId());
        if (arena != null) {
            arena.getPlayers().remove(player.getUniqueId());
            arena.getSpectators().remove(player.getUniqueId());
            
            var msg = settings.getMessage("game.player-quit").replace("%player%", player.getName());
            broadcastArenaMessage(arena, msg);
            
            player.getInventory().clear();
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        }
    }

    private void giveLobbyItems(Player player) {
        player.getInventory().clear();
        player.getInventory().setItem(0, new ItemBuilder(settings.TEAM_SELECTOR_MATERIAL)
                .setName(settings.format("&e&lᴛᴀᴋɪᴍ ꜱᴇçɪᴍɪ &7(ꜱᴀğ ᴛɪᴋ)")).build());
        player.getInventory().setItem(8, new ItemBuilder(settings.LEAVE_LOBBY_MATERIAL)
                .setName(settings.format("&ᴄ&ʟʟᴏʙɪᴅᴇɴ ᴀʏʀɪʟ &7(ꜱᴀğ ᴛɪᴋ)")).build());
    }

    public void broadcastArenaMessage(Arena arena, String message) {
        var formatted = settings.PREFIX + settings.format(message);
        arena.getPlayers().forEach(uuid -> {
            var p = Bukkit.getPlayer(uuid);
            if (p != null) p.sendMessage(formatted);
        });
    }

    public void updatePlayerVisibility(Player player) {
        Arena arena = playerArenas.get(player.getUniqueId());
        for (Player online : Bukkit.getOnlinePlayers()) {
            Arena onlineArena = playerArenas.get(online.getUniqueId());
            if (arena == null || onlineArena == null || !arena.equals(onlineArena)) {
                player.hidePlayer(plugin, online);
                online.hidePlayer(plugin, player);
            } else {
                player.showPlayer(plugin, online);
                online.showPlayer(plugin, player);
            }
        }
    }

    public Arena getArenaByPlayer(Player player) {
        return playerArenas.get(player.getUniqueId());
    }

    public void addSpectator(Player player, Arena arena) {
        arena.getSpectators().add(player.getUniqueId());
        player.setGameMode(GameMode.SPECTATOR);
        player.getInventory().clear();
        player.sendMessage(settings.format(settings.PREFIX + "&eɪᴢʟᴇʏɪᴄɪ ᴍᴏᴅᴜɴᴀ ɢᴇçᴛɪɴɪᴢ."));
    }

    public void selectTeam(Player player, Team team) {
        Arena arena = getArenaByPlayer(player);
        if (arena == null) return;
        arena.getGameManager().setPlayerTeam(player, team);
        var msg = settings.getMessage("game.team-select").replace("%team%", team.getDisplayName());
        player.sendMessage(settings.format(settings.PREFIX + msg));
    }

    public void unselectTeam(Player player) {
        Arena arena = getArenaByPlayer(player);
        if (arena == null) return;
        arena.getGameManager().removePlayerFromTeam(player);
        player.sendMessage(settings.format(settings.PREFIX + "&7ᴛᴀᴋɪᴍ ꜱᴇçɪᴍɪ ɪᴘᴛᴀʟ ᴇᴅɪʟᴅɪ."));
    }

    public void openTeamSelectionGUI(Player player) {
        plugin.getGuiManager().openTeamGUI(player, getArenaByPlayer(player));
    }
}
