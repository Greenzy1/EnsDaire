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

    public void broadcastArenaMessage(String message) {
        plugin.getArenaController().broadcastArenaMessage(arena, message);
    }

    public void startGame() {
        arena.setState(ArenaState.STARTING);
        broadcastArenaMessage(settings.getMessage("game.start"));
        
        this.roundDurations = arena.getRoundDurations();
        if (this.roundDurations == null || this.roundDurations.isEmpty()) {
            this.roundDurations = Arrays.asList(120, 90, 60);
        }

        startNextRound();
    }

    private void startNextRound() {
        currentRound++;
        if (currentRound > roundDurations.size()) {
            startSuddenDeath();
            return;
        }
        
        arena.setState(ArenaState.ACTIVE);
        var msg = settings.getMessage("game.round-start").replace("%round%", String.valueOf(currentRound));
        broadcastArenaMessage(msg);
        startRoundTimer();
    }

    private void startRoundTimer() {
        int duration = roundDurations.get(currentRound - 1);
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
        broadcastArenaMessage(settings.getMessage("game.round-end"));
        if (currentRound >= roundDurations.size()) {
            startSuddenDeath();
        } else {
            startNextRound();
        }
    }

    private void startSuddenDeath() {
        suddenDeath = true;
        broadcastArenaMessage(settings.getMessage("game.sudden-death"));
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
                    var msg = settings.getMessage("game.winner").replace("%player%", winner.getName());
                    Bukkit.broadcastMessage(settings.PREFIX + settings.format(msg));
                }
            }
            return true;
        }
        return false;
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
