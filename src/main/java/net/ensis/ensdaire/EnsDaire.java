package net.ensis.ensdaire;

import net.ensis.ensdaire.managers.*;
import net.ensis.ensdaire.listeners.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class EnsDaire extends JavaPlugin {

    private static EnsDaire instance;
    private ArenaManager arenaManager;
    private PlayerDataManager playerDataManager;
    private DatabaseManager databaseManager;
    private LanguageManager languageManager;
    private MenuManager menuManager;
    private CosmeticManager cosmeticManager;
    private HologramManager hologramManager;
    private SignManager signManager;
    private static Economy econ = null;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        setupEconomy();

        this.languageManager = new LanguageManager(this);
        this.menuManager = new MenuManager(this);
        this.databaseManager = new DatabaseManager(this);
        this.cosmeticManager = new CosmeticManager(this);
        this.hologramManager = new HologramManager(this);
        this.signManager = new SignManager(this);

        if (!databaseManager.initialize()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.playerDataManager = new PlayerDataManager(this);
        this.arenaManager = new ArenaManager(this);
        arenaManager.loadArenas();

        registerCommands();
        registerListeners();

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new net.ensis.ensdaire.hooks.EnsDaireExpansion(this).register();
        }

        getLogger().info("  _____         ____        _          ");
        getLogger().info(" | ____|_ __  __|  _ \\ __ _(_)_ __ ___ ");
        getLogger().info(" |  _| | '_ \\/ __| | |/ _` | | '__/ _ \\");
        getLogger().info(" | |___| | | \\__ \\ |_| (_| | | | |  __/");
        getLogger().info(" |_____|_| |_|___/____/\\__,_|_|_|  \\___|");
        getLogger().info(" v" + getDescription().getVersion() + " | Developer: Ensis | ensis.net");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null)
            return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
            return false;
        econ = rsp.getProvider();
        return econ != null;
    }

    private void registerCommands() {
        net.ensis.ensdaire.commands.EnsDaireCommand cmd = new net.ensis.ensdaire.commands.EnsDaireCommand(this);
        getCommand("ensdaire").setExecutor(cmd);
        getCommand("ensdaire").setTabCompleter(cmd);
        getCommand("katil").setExecutor(cmd);
        getCommand("ayril").setExecutor(cmd);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new ShulkerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldListener(this), this);
        getServer().getPluginManager().registerEvents(new GuiListener(this), this);
    }

    @Override
    public void onDisable() {
        if (arenaManager != null)
            arenaManager.shutdownAll();
        if (playerDataManager != null)
            playerDataManager.saveAll();
        if (databaseManager != null)
            databaseManager.close();
        getLogger().info("EnsDaire disabled.");
    }

    public static EnsDaire getInstance() {
        return instance;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public CosmeticManager getCosmeticManager() {
        return cosmeticManager;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public SignManager getSignManager() {
        return signManager;
    }

    public static Economy getEconomy() {
        return econ;
    }
}
