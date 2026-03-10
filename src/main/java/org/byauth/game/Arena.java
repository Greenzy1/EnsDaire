package org.byauth.game;

import org.byauth.EnsDaire;
import org.byauth.manager.ArenaManager;
import org.byauth.manager.GameManager;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Arena {

    private final String name;
    private String displayName;
    private final EnsDaire plugin;
    private final Set<UUID> players;
    private final Set<UUID> spectators;
    private ArenaState state;
    private long lastStateChangeTime;
    private Location lobbyLocation;
    private Location centerLocation;
    private int teamSize = 1;
    private List<Integer> roundDurations = new ArrayList<>();
    private final GameManager gameManager;
    private final ArenaManager arenaManager;
    private final String id;
    private int maxPlayers = 8;
    private String type = "Solo";

    public Arena(String name, EnsDaire plugin, Location lobbyLocation, Location centerLocation) {
        this.name = name;
        this.plugin = plugin;
        this.lobbyLocation = lobbyLocation;
        this.centerLocation = centerLocation;
        this.players = new HashSet<>();
        this.spectators = new HashSet<>();
        this.state = ArenaState.WAITING;
        this.lastStateChangeTime = System.currentTimeMillis();
        this.arenaManager = new ArenaManager(plugin, this);
        this.gameManager = new GameManager(plugin, this);
        this.displayName = name;
        // For existing constructor, generate a UUID for id or handle as needed
        this.id = UUID.randomUUID().toString();
    }

    public Arena(EnsDaire plugin, String id) {
        this.plugin = plugin;
        this.id = id;
        this.name = id; // Or some default name based on id
        this.players = new HashSet<>();
        this.spectators = new HashSet<>();
        this.state = ArenaState.WAITING;
        this.lastStateChangeTime = System.currentTimeMillis();
        this.arenaManager = new ArenaManager(plugin, this);
        this.gameManager = new GameManager(plugin, this);
        this.displayName = id; // Default display name
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return (displayName == null || displayName.isEmpty()) ? name : displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Set<UUID> getPlayers() {
        return players;
    }

    public Set<UUID> getSpectators() {
        return spectators;
    }

    public ArenaState getState() {
        return state;
    }

    public void setState(ArenaState state) {
        this.state = state;
        this.lastStateChangeTime = System.currentTimeMillis();
    }

    public long getLastStateChangeTime() {
        return lastStateChangeTime;
    }

    public Location getLobbyLocation() {
        return lobbyLocation;
    }

    public void setLobbyLocation(Location lobbyLocation) {
        this.lobbyLocation = lobbyLocation;
    }

    public Location getCenterLocation() {
        return centerLocation;
    }

    public void setCenterLocation(Location centerLocation) {
        this.centerLocation = centerLocation;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }

    public List<Integer> getRoundDurations() {
        return roundDurations;
    }

    public void setRoundDurations(List<Integer> roundDurations) {
        this.roundDurations = roundDurations;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public String getId() {
        return id;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getType() {
        return type;
    }
}
