package net.ensdaireplugin.ensdaire;

import net.ensdaireplugin.ensdaire.arena.ArenaManager;
import net.ensdaireplugin.ensdaire.commands.EnsDaireCommand;
import net.ensdaireplugin.ensdaire.database.DatabaseManager;
import net.ensdaireplugin.ensdaire.listeners.*;
import net.ensdaireplugin.ensdaire.player.PlayerDataManager;
import net.ensdaireplugin.ensdaire.utils.LanguageManager;
import net.ensdaireplugin.ensdaire.utils.MenuManager;
import org.bukkit.plugin.java.JavaPlugin;

public class EnsDaire extends JavaPlugin {
    private static EnsDaire instance;
    private ArenaManager arenaManager;
    private PlayerDataManager playerDataManager;
    private DatabaseManager databaseManager;
    private LanguageManager languageManager;
    private MenuManager menuManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        this.languageManager = new LanguageManager(this);
        this.menuManager = new MenuManager(this);
        this.databaseManager = new DatabaseManager(this);
        
        if (!databaseManager.initialize()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        this.playerDataManager = new PlayerDataManager(this);
        this.arenaManager = new ArenaManager(this);
        arenaManager.loadArenas();
        
        EnsDaireCommand cmd = new EnsDaireCommand(this);
        getCommand("ensdaire").setExecutor(cmd);
        getCommand("ensdaire").setTabCompleter(cmd);
        
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new ShulkerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldListener(this), this);
        getServer().getPluginManager().registerEvents(new GuiListener(this), this);
        
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new net.ensdaireplugin.ensdaire.hooks.EnsDaireExpansion(this).register();
        }
    }

    @Override
    public void onDisable() {
        if (arenaManager != null) arenaManager.shutdownAll();
        if (playerDataManager != null) playerDataManager.saveAll();
        if (databaseManager != null) databaseManager.close();
    }

    public static EnsDaire getInstance() { return instance; }
    public ArenaManager getArenaManager() { return arenaManager; }
    public PlayerDataManager getPlayerDataManager() { return playerDataManager; }
    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public LanguageManager getLanguageManager() { return languageManager; }
    public MenuManager getMenuManager() { return menuManager; }
}
