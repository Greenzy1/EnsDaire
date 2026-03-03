package net.ensdaireplugin.ensdaire.database;

import net.ensdaireplugin.ensdaire.EnsDaire;

public class DatabaseManager {
    private final EnsDaire plugin;

    public DatabaseManager(EnsDaire plugin) { this.plugin = plugin; }

    public boolean initialize() {
        plugin.getDataFolder().mkdirs();
        plugin.getLogger().info("Veritabanı (SQLite) hazır.");
        return true;
    }

    public void close() {}
}
