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

    public List<Arena> getArenas() {
        return new ArrayList<>(arenas.values());
    }

    public Arena getOrCreateArena(String name) {
        return arenas.computeIfAbsent(name, n -> new Arena(plugin, n));
    }

    public void buildLobby(Arena arena) {
        // Logic to build a physical lobby if needed, or just prepare locations
        if (arena.getLobbyLocation() != null) {
            // Optional: placeholder for physical structure generation
        }
    }

    private final Map<UUID, Arena> playerArenas = new HashMap<>();
    private final Map<UUID, Arena> spectators = new HashMap<>();

    public ArenaController(EnsDaire plugin) {
        this.plugin = plugin;
        this.settings = plugin.getSettingsManager();
        loadArenas();
    }

    public void loadArenas() {
        if (settings.getArenaNames() == null)
            return;

        for (String arenaName : settings.getArenaNames()) {
            Location lobby = settings.getArenaLocation(arenaName, "lobby");
            Location center = settings.getArenaLocation(arenaName, "center");

            if (lobby != null && lobby.getWorld() != null) {
                Arena arena = arenas.computeIfAbsent(arenaName, n -> new Arena(plugin, n));
                arena.setLobbyLocation(lobby);
                arena.setCenterLocation(center);
                arena.setTeamSize(settings.getTeamSize(arenaName));
                arena.setRoundDurations(settings.getRoundDurations(arenaName));
                arena.setDisplayName(settings.getDisplayName(arenaName));
            }
        }
    }

    public void addPlayer(Player player, String arenaName) {
        Arena currentArena = getArenaByPlayerIncludingSpectators(player);
        if (currentArena != null) {
            removePlayer(player);
        }

        Arena arena = arenas.get(arenaName);
        if (arena == null)
            return;

        if (arena.getState() != ArenaState.WAITING && arena.getState() != ArenaState.COUNTDOWN)
            return;

        int maxPlayers = Team.values().length * arena.getTeamSize();
        if (arena.getPlayers().size() >= maxPlayers)
            return;

        arena.getPlayers().add(player.getUniqueId());
        playerArenas.put(player.getUniqueId(), arena);

        player.teleport(arena.getLobbyLocation().clone().add(0.5, 2, 0.5));
        player.setGameMode(GameMode.SURVIVAL);
        giveLobbyItems(player);
        updatePlayerVisibility(player);

        checkAndStartCountdown(arena);
    }

    public void removePlayer(Player player) {
        Arena arena = playerArenas.get(player.getUniqueId());
        if (arena == null) {
            arena = spectators.get(player.getUniqueId());
        }

        if (arena == null)
            return;

        arena.getPlayers().remove(player.getUniqueId());
        arena.getSpectators().remove(player.getUniqueId());
        playerArenas.remove(player.getUniqueId());
        spectators.remove(player.getUniqueId());

        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(settings.getMainSpawnLocation() != null ? settings.getMainSpawnLocation()
                : Bukkit.getWorlds().get(0).getSpawnLocation());
        updatePlayerVisibility(player);
    }

    public void checkAndStartCountdown(Arena arena) {
        if (arena.getState() == ArenaState.WAITING && arena.getPlayers().size() >= settings.MIN_PLAYERS_TO_START) {
            startLobbyCountdown(arena);
        }
    }

    private void startLobbyCountdown(Arena arena) {
        arena.setState(ArenaState.COUNTDOWN);
        new BukkitRunnable() {
            int countdown = settings.LOBBY_COUNTDOWN_SECONDS;

            @Override
            public void run() {
                if (arena.getState() != ArenaState.COUNTDOWN) {
                    this.cancel();
                    return;
                }
                if (arena.getPlayers().size() < settings.MIN_PLAYERS_TO_START) {
                    arena.setState(ArenaState.WAITING);
                    this.cancel();
                    return;
                }
                if (countdown > 0) {
                    countdown--;
                } else {
                    this.cancel();
                    arena.getGameManager().startGame(arena.getCenterLocation());
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void giveLobbyItems(Player player) {
        player.getInventory().clear();
        player.getInventory().setItem(settings.TEAM_SELECTOR_SLOT,
                new ItemBuilder(settings.TEAM_SELECTOR_MATERIAL).setName(settings.TEAM_SELECTOR_NAME).build());
        player.getInventory().setItem(settings.LEAVE_LOBBY_SLOT,
                new ItemBuilder(settings.LEAVE_LOBBY_MATERIAL).setName(settings.LEAVE_LOBBY_NAME).build());
    }

    public void selectTeam(Player player, Team team) {
        Arena arena = getArenaByPlayer(player);
        if (arena != null) {
            arena.getGameManager().setPlayerTeam(player, team);
            player.sendMessage(SettingsManager.PREFIX + "§aTakım seçildi: " + team.getDisplayName());
        }
    }

    public void unselectTeam(Player player) {
        Arena arena = getArenaByPlayer(player);
        if (arena != null) {
            arena.getGameManager().removePlayerFromTeam(player);
            player.sendMessage(SettingsManager.PREFIX + "§eTakım seçimi iptal edildi.");
        }
    }

    public void openTeamSelectionGUI(Player player) {
        Arena arena = getArenaByPlayer(player);
        if (arena != null) {
            plugin.getGuiManager().openTeamGUI(player, arena);
        }
    }

    public void updatePlayerVisibility(Player subject) {
        Arena subjectArena = getArenaByPlayerIncludingSpectators(subject);
        for (Player observer : Bukkit.getOnlinePlayers()) {
            if (subject.equals(observer))
                continue;
            Arena observerArena = getArenaByPlayerIncludingSpectators(observer);
            boolean shouldSee = (subjectArena == null && observerArena == null)
                    || (subjectArena != null && subjectArena.equals(observerArena));
            if (shouldSee) {
                subject.showPlayer(plugin, observer);
                observer.showPlayer(plugin, subject);
            } else {
                subject.hidePlayer(plugin, observer);
                observer.hidePlayer(plugin, subject);
            }
        }
    }

    public void addSpectator(Player player, Arena arena) {
        arena.getSpectators().add(player.getUniqueId());
        spectators.put(player.getUniqueId(), arena);
        player.setGameMode(GameMode.SPECTATOR);
    }

    public void broadcastArenaMessage(Arena arena, String message) {
        for (UUID uuid : arena.getPlayers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null)
                p.sendMessage(SettingsManager.PREFIX + message);
        }
        for (UUID uuid : arena.getSpectators()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null)
                p.sendMessage(SettingsManager.PREFIX + message);
        }
    }

    public void teleportToMainSpawn(Player player) {
        Location spawn = settings.getMainSpawnLocation();
        if (spawn != null)
            player.teleport(spawn);
        else
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
    }

    public Arena getArenaByPlayerIncludingSpectators(Player player) {
        Arena arena = playerArenas.get(player.getUniqueId());
        return arena != null ? arena : spectators.get(player.getUniqueId());
    }

    public Arena getArenaByPlayer(Player player) {
        return playerArenas.get(player.getUniqueId());
    }

    public void shutdown() {
        for (Arena arena : arenas.values()) {
            arena.getArenaManager().destroyArena();
        }
    }
}
