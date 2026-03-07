package net.ensis.ensdaire.managers;

import net.ensis.ensdaire.EnsDaire;

public class DatabaseManager {

    private final EnsDaire plugin;

    public DatabaseManager(EnsDaire plugin) {
        this.plugin = plugin;
    }

    public boolean initialize() {
        plugin.getDataFolder().mkdirs();
        try {
            Class.forName("org.sqlite.JDBC");
            plugin.getLogger().info("SQLite driver loaded successfully.");
            return true;
        } catch (ClassNotFoundException e) {
            plugin.getLogger().severe("SQLite JDBC driver not found: " + e.getMessage());
            return false;
        }
    }

    public void close() {
    }
}
