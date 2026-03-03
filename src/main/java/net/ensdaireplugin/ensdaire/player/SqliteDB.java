package net.ensdaireplugin.ensdaire.player;

import net.ensdaireplugin.ensdaire.EnsDaire;

import java.io.File;
import java.sql.*;
import java.util.*;

public class SqliteDB {

    private final EnsDaire plugin;
    private Connection conn;

    public SqliteDB(EnsDaire plugin) {
        this.plugin = plugin;
        connect();
        migrate();
    }

    private void connect() {
        try {
            plugin.getDataFolder().mkdirs();
            String url = "jdbc:sqlite:" + new File(plugin.getDataFolder(), "data.db").getAbsolutePath();
            conn = DriverManager.getConnection(url);
            try (Statement s = conn.createStatement()) {
                s.execute("PRAGMA journal_mode=WAL;");
                s.execute("PRAGMA synchronous=NORMAL;");
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("SQLite bağlantı hatası: " + e.getMessage());
        }
    }

    private void migrate() {
        String sql = """
            CREATE TABLE IF NOT EXISTS players (
                uuid     TEXT PRIMARY KEY,
                name     TEXT NOT NULL,
                tokens   INTEGER DEFAULT 0,
                wins     INTEGER DEFAULT 0,
                games    INTEGER DEFAULT 0,
                kills    INTEGER DEFAULT 0,
                deaths   INTEGER DEFAULT 0,
                points   INTEGER DEFAULT 0,
                rank     TEXT DEFAULT 'Çaylak',
                last_seen INTEGER DEFAULT 0
            )""";
        try (Statement s = conn.createStatement()) {
            s.execute(sql);
            // Sütun kontrolü (Eski DB'ler için)
            addColumnIfNotExists(s, "deaths", "INTEGER DEFAULT 0");
            addColumnIfNotExists(s, "points", "INTEGER DEFAULT 0");
        } catch (SQLException e) {
            plugin.getLogger().severe("Tablo oluşturma hatası: " + e.getMessage());
        }
    }

    private void addColumnIfNotExists(Statement s, String column, String type) {
        try {
            s.execute("ALTER TABLE players ADD COLUMN " + column + " " + type);
        } catch (SQLException ignored) {}
    }

    public PlayerData load(UUID uuid, String name) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM players WHERE uuid=?")) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                PlayerData d = new PlayerData(uuid, rs.getString("name"));
                d.setTokens(rs.getLong("tokens"));
                d.setPoints(rs.getLong("points"));
                for (int i = 0; i < rs.getInt("wins"); i++) d.addWin();
                for (int i = 0; i < rs.getInt("games"); i++) d.addGame();
                d.addKills(rs.getInt("kills"));
                for (int i = 0; i < rs.getInt("deaths"); i++) d.addDeath();
                d.setRank(rs.getString("rank"));
                return d;
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Oyuncu yükleme hatası: " + e.getMessage());
        }
        PlayerData fresh = new PlayerData(uuid, name);
        save(fresh);
        return fresh;
    }

    public void save(PlayerData d) {
        String sql = """
            INSERT INTO players (uuid,name,tokens,wins,games,kills,deaths,points,rank,last_seen)
            VALUES (?,?,?,?,?,?,?,?,?,?)
            ON CONFLICT(uuid) DO UPDATE SET
              name=excluded.name, tokens=excluded.tokens, wins=excluded.wins,
              games=excluded.games, kills=excluded.kills, deaths=excluded.deaths,
              points=excluded.points, rank=excluded.rank,
              last_seen=excluded.last_seen""";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getUuid().toString());
            ps.setString(2, d.getName());
            ps.setLong(3, d.getTokens());
            ps.setInt(4, d.getWins());
            ps.setInt(5, d.getGames());
            ps.setInt(6, d.getKills());
            ps.setInt(7, d.getDeaths());
            ps.setLong(8, d.getPoints());
            ps.setString(9, d.getRank());
            ps.setLong(10, System.currentTimeMillis());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("Oyuncu kayıt hatası: " + e.getMessage());
        }
    }

    public List<PlayerData> getTop(int limit) {
        List<PlayerData> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
            "SELECT * FROM players ORDER BY tokens DESC LIMIT ?")) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PlayerData d = new PlayerData(UUID.fromString(rs.getString("uuid")), rs.getString("name"));
                d.setTokens(rs.getLong("tokens"));
                d.setPoints(rs.getLong("points"));
                for (int i = 0; i < rs.getInt("wins"); i++) d.addWin();
                for (int i = 0; i < rs.getInt("games"); i++) d.addGame();
                d.addKills(rs.getInt("kills"));
                for (int i = 0; i < rs.getInt("deaths"); i++) d.addDeath();
                d.setRank(rs.getString("rank"));
                list.add(d);
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Top liste hatası: " + e.getMessage());
        }
        return list;
    }

    public void close() {
        try { if (conn != null && !conn.isClosed()) conn.close(); }
        catch (SQLException ignored) {}
    }
}
