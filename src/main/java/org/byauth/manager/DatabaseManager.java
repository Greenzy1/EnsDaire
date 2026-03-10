package org.byauth.manager;

import org.byauth.EnsDaire;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.locks.ReentrantLock;

public class DatabaseManager {

    private final EnsDaire plugin;
    private Connection connection;
    private final String dbUrl;
    private final ReentrantLock lock = new ReentrantLock();

    public DatabaseManager(EnsDaire plugin) {
        this.plugin = plugin;
        File dbFile = new File(plugin.getDataFolder(), "playerdata.db");
        if (!dbFile.getParentFile().exists()) {
            dbFile.getParentFile().mkdirs();
        }
        this.dbUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();
    }

    public void connect() {
        try {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(dbUrl);
            plugin.getLogger().info("SQLite veritabanı bağlantısı başarılı.");
            createTable();
        } catch (SQLException | ClassNotFoundException e) {
            plugin.getLogger().severe("SQLite veritabanı bağlantısı kurulamadı!");
            e.printStackTrace();
        }
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS player_stats (" +
                "uuid VARCHAR(36) PRIMARY KEY," +
                "name VARCHAR(16)," +
                "points INTEGER DEFAULT 0," +
                "kills INTEGER DEFAULT 0," +
                "deaths INTEGER DEFAULT 0," +
                "wins INTEGER DEFAULT 0," +
                "losses INTEGER DEFAULT 0," +
                "bcoin INTEGER DEFAULT 0," +
                "owned_cosmetics TEXT," +
                "selected_cosmetic VARCHAR(64)," +
                "selected_kill_effect VARCHAR(64)," +
                "selected_arrow_effect VARCHAR(64)" +
                ");";
        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            plugin.getLogger().severe("player_stats tablosu oluşturulamadı!");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
