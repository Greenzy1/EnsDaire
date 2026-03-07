package net.ensis.ensdaire.models;

import java.util.UUID;

public class PlayerData {
    private final UUID uuid;
    private String name;
    private long tokens;
    private int wins, games, kills, deaths;
    private long points;
    private String rank = "Çaylak";

    public PlayerData(UUID uuid, String name) { this.uuid = uuid; this.name = name; }

    public UUID getUuid()      { return uuid; }
    public String getName()    { return name; }
    public void setName(String n) { name = n; }
    public long getTokens()    { return tokens; }
    public void setTokens(long t) { tokens = t; }
    public void addTokens(int t) { tokens += t; }
    
    public long getPoints()    { return points; }
    public void setPoints(long p) { points = p; }
    public void addPoints(long p) { points += p; }

    public int getWins()       { return wins; }
    public void addWin()       { wins++; }
    public int getGames()      { return games; }
    public void addGame()      { games++; }
    public int getKills()      { return kills; }
    public void addKills(int k){ kills += k; }
    public int getDeaths()     { return deaths; }
    public void addDeath()     { deaths++; }
    
    public String getRank()    { return rank; }
    public void setRank(String r) { rank = r; }

    public double getWinRate() { return games == 0 ? 0 : (double) wins / games * 100; }
    public double getKpg()     { return games == 0 ? 0 : (double) kills / (double) Math.max(1, games); }
    public String getKD() {
        if (deaths == 0) return String.valueOf((double) kills);
        return String.format("%.2f", (double) kills / deaths);
    }
}
