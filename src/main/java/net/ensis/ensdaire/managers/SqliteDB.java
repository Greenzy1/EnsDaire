package net.ensis.ensdaire.managers;

import net.ensis.ensdaire.EnsDaire;
import net.ensis.ensdaire.models.PlayerData;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SqliteDB {
    private final EnsDaire plugin;
    private Connection connection;

    public SqliteDB(EnsDaire plugin) {
        this.plugin = plugin;
        init();
    }

    private void init() {
        try {
            Class.forName("org.sqlite.JDBC");
            File dbFile = new File(plugin.getDataFolder(), "database.db");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            
            try (Statement s = connection.createStatement()) {
                s.execute("CREATE TABLE IF NOT EXISTS players (" +
                        "uuid TEXT PRIMARY KEY, " +
                        "name TEXT, " +
                        "tokens INTEGER DEFAULT 0, " +
                        "points INTEGER DEFAULT 0, " +
                        "wins INTEGER DEFAULT 0, " +
                        "kills INTEGER DEFAULT 0, " +
                        "deaths INTEGER DEFAULT 0, " +
                        "games INTEGER DEFAULT 0, " +
                        "rank TEXT DEFAULT 'Çaylak', " +
                        "cosmetics TEXT DEFAULT '')");
            }
        } catch (Exception e) {
            plugin.getLogger().severe("SQLite başlatılamadı: " + e.getMessage());
        }
    }

    public PlayerData load(UUID uuid, String name) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM players WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                PlayerData data = new PlayerData(uuid, rs.getString("name"));
                data.addTokens((int) rs.getLong("tokens"));
                data.addPoints(rs.getLong("points"));
                for (int i = 0; i < rs.getInt("wins"); i++) data.addWin();
                data.addKills(rs.getInt("kills"));
                for (int i = 0; i < rs.getInt("deaths"); i++) data.addDeath();
                for (int i = 0; i < rs.getInt("games"); i++) data.addGame();
                data.setRank(rs.getString("rank"));
                return data;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return new PlayerData(uuid, name);
    }

    public void save(PlayerData data) {
        String sql = "INSERT OR REPLACE INTO players (uuid, name, tokens, points, wins, kills, deaths, games, rank) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, data.getUuid().toString());
            ps.setString(2, data.getName());
            ps.setLong(3, data.getTokens());
            ps.setLong(4, data.getPoints());
            ps.setInt(5, data.getWins());
            ps.setInt(6, data.getKills());
            ps.setInt(7, data.getDeaths());
            ps.setInt(8, data.getGames());
            ps.setString(9, data.getRank());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<PlayerData> getTop(int limit) {
        List<PlayerData> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM players ORDER BY points DESC LIMIT ?")) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PlayerData data = new PlayerData(UUID.fromString(rs.getString("uuid")), rs.getString("name"));
                data.addPoints(rs.getLong("points"));
                list.add(data);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void close() {
        try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}
