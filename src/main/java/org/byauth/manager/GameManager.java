package org.byauth.manager;

import org.byauth.EnsDaire;
import org.byauth.data.PlayerStats;
import org.byauth.game.Arena;
import org.byauth.game.ArenaState;
import org.byauth.game.Team;
import org.byauth.utils.SettingsManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.byauth.EnsDaire;

import java.util.*;
import java.util.stream.Collectors;

public class GameManager {

    private final EnsDaire plugin;
    private final SettingsManager settings;
    private final PlayerDataManager playerDataManager;
    private final Arena arena;
    private final Map<UUID, Team> playerTeams = new HashMap<>();
    private final Map<Team, List<UUID>> teams = new HashMap<>();
    private BukkitTask roundTimerTask;
    private int currentRound = 0;
    private List<Integer> roundDurations;
    private final Map<Location, Team> playerPlacedBlocks = new HashMap<>();
    private final Set<UUID> invinciblePlayers = new HashSet<>();
    private boolean suddenDeath = false;
    private final Map<UUID, Long> snowballCooldown = new HashMap<>();

    public GameManager(EnsDaire plugin, Arena arena) {
        this.plugin = plugin;
        this.settings = plugin.getSettingsManager();
        this.playerDataManager = plugin.getPlayerDataManager();
        this.arena = arena;
        for (Team team : Team.values()) {
            teams.put(team, new ArrayList<>());
        }
    }

    public void assignPlayerToTeam(Player player, Team newTeam) {
        unassignPlayerFromTeam(player);
        teams.get(newTeam).add(player.getUniqueId());
        playerTeams.put(player.getUniqueId(), newTeam);
    }

    public void unassignPlayerFromTeam(Player player) {
        Team oldTeam = playerTeams.get(player.getUniqueId());
        if (oldTeam != null) {
            teams.get(oldTeam).remove(player.getUniqueId());
            playerTeams.remove(player.getUniqueId());
        }
    }

    public Team getTeamOfPlayer(Player player) {
        return playerTeams.get(player.getUniqueId());
    }

    public void setPlayerTeam(Player player, Team team) {
        assignPlayerToTeam(player, team);
    }

    public void removePlayerFromTeam(Player player) {
        unassignPlayerFromTeam(player);
    }

    public List<UUID> getPlayersInTeam(Team team) {
        return teams.getOrDefault(team, new ArrayList<>());
    }

    public void startGame(Location center) {
        arena.setState(ArenaState.STARTING);
        List<Player> playersInArena = new ArrayList<>();
        for (UUID uuid : arena.getPlayers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null)
                playersInArena.add(p);
        }

        assignPlayersToTeams(playersInArena);
        this.roundDurations = arena.getRoundDurations();
        if (this.roundDurations == null || this.roundDurations.isEmpty()) {
            this.roundDurations = Arrays.asList(120, 90, 60);
        }

        startInterRoundPhase(true);
    }

    private void startInterRoundPhase(boolean isFirstRound) {
        arena.setState(ArenaState.STARTING);
        // ... Inter round logic (pods, etc.)
        startNextRound();
    }

    private void startNextRound() {
        currentRound++;
        arena.setState(ArenaState.ACTIVE);
        startRoundTimer();
    }

    private void startRoundTimer() {
        int duration = roundDurations.size() >= currentRound ? roundDurations.get(currentRound - 1) : 60;
        roundTimerTask = new BukkitRunnable() {
            int timeLeft = duration;

            @Override
            public void run() {
                if (timeLeft <= 0 || arena.getState() != ArenaState.ACTIVE) {
                    this.cancel();
                    endRound();
                    return;
                }
                timeLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void endRound() {
        if (currentRound >= roundDurations.size()) {
            checkWinCondition();
            return;
        }
        startInterRoundPhase(false);
    }

    public boolean checkWinCondition() {
        List<UUID> alivePlayers = arena.getPlayers().stream()
                .filter(uuid -> !arena.getSpectators().contains(uuid))
                .collect(Collectors.toList());

        if (alivePlayers.size() <= 1) {
            UUID winnerUuid = alivePlayers.isEmpty() ? null : alivePlayers.get(0);
            if (winnerUuid != null) {
                Player winner = Bukkit.getPlayer(winnerUuid);
                if (winner != null) {
                    plugin.getVictoryEffects().playRandomVictoryEffect(winner);
                    Bukkit.broadcastMessage(
                            settings.PREFIX + settings.format("&b" + winner.getName() + " &aoyunu kazandı!"));
                }
            }
            stopGame(true);
            return true;
        }
        return false;
    }

    public void stopGame(boolean broadcast) {
        arena.setState(ArenaState.RESETTING);
        // Reset logic
    }

    private void assignPlayersToTeams(List<Player> playersInArena) {
        // Simplified auto-assign
        List<Team> availableTeams = new ArrayList<>(Arrays.asList(Team.values()));
        Collections.shuffle(availableTeams);
        int i = 0;
        for (Player p : playersInArena) {
            if (!playerTeams.containsKey(p.getUniqueId())) {
                Team team = availableTeams.get(i % availableTeams.size());
                assignPlayerToTeam(p, team);
                i++;
            }
        }
    }

    public boolean isTeamAlive(Team team) {
        return teams.get(team).stream().anyMatch(uuid -> !arena.getSpectators().contains(uuid));
    }

    public Map<Location, Team> getPlayerPlacedBlocks() {
        return playerPlacedBlocks;
    }

    public boolean isSuddenDeath() {
        return suddenDeath;
    }

    public Set<UUID> getInvinciblePlayers() {
        return invinciblePlayers;
    }

    public Map<UUID, Long> getSnowballCooldown() {
        return snowballCooldown;
    }
}
