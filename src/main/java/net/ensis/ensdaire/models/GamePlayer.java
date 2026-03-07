package net.ensis.ensdaire.models;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GamePlayer {
    public enum Status { ALIVE, SPECTATOR, DISCONNECTED }
    private final UUID uuid;
    private final String name;
    private CircleColor color;
    private Location capsuleLocation;
    private Status status = Status.ALIVE;
    private int kills;
    private int roundsSurvived;
    private int tokensEarned;
    private boolean gotFirstBlood;
    private int placement = -1;

    public GamePlayer(Player player) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public CircleColor getColor() { return color; }
    public void setColor(CircleColor color) { this.color = color; }
    public Location getCapsuleLocation() { return capsuleLocation; }
    public void setCapsuleLocation(Location loc) { this.capsuleLocation = loc == null ? null : loc.clone(); }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public boolean isAlive() { return status == Status.ALIVE; }
    public boolean isSpectator() { return status == Status.SPECTATOR; }
    public int getKills() { return kills; }
    public void addKill() { kills++; }
    public int getRoundsSurvived() { return roundsSurvived; }
    public void addRoundSurvived() { roundsSurvived++; }
    public int getTokensEarned() { return tokensEarned; }
    public void addTokens(int amount) { tokensEarned += amount; }
    public boolean isGotFirstBlood() { return gotFirstBlood; }
    public void setGotFirstBlood(boolean gotFirstBlood) { this.gotFirstBlood = gotFirstBlood; }
    public int getPlacement() { return placement; }
    public void setPlacement(int placement) { this.placement = placement; }
    public String getColoredName() { return (color != null ? color.getChatColor() : "§f") + name; }
}
