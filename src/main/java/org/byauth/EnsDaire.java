package org.byauth;

import org.byauth.controller.ArenaController;
import org.byauth.manager.*;
import org.byauth.command.AdminCommand;
import org.byauth.listener.*;
import org.byauth.utils.SettingsManager;
import org.byauth.utils.VictoryEffects;
import org.bukkit.plugin.java.JavaPlugin;

public final class EnsDaire extends JavaPlugin {

    private static EnsDaire instance;
    private ArenaController arenaController;
    private DatabaseManager databaseManager;
    private PlayerDataManager playerDataManager;
    private LootManager lootManager;
    private ShopManager shopManager;
    private CosmeticManager cosmeticManager;
    private ScoreboardManager scoreboardManager;
    private HologramManager hologramManager;
    private GuiManager guiManager;
    private SettingsManager settingsManager;
    private VictoryEffects victoryEffects;
    private EditorManager editorManager;

    @Override
    public void onEnable() {
        instance = this;
        this.settingsManager = new SettingsManager(this);
        this.databaseManager = new DatabaseManager(this);
        databaseManager.connect();
        this.playerDataManager = new PlayerDataManager(this, databaseManager);
        this.lootManager = new LootManager(this);
        this.shopManager = new ShopManager(this);
        this.cosmeticManager = new CosmeticManager(this);
        this.scoreboardManager = new ScoreboardManager(this);
        this.hologramManager = new HologramManager(this);
        this.guiManager = new GuiManager(this);
        this.victoryEffects = new VictoryEffects(this);
        this.arenaController = new ArenaController(this);
        this.editorManager = new EditorManager(this);

        getCommand("ensdaire").setExecutor(new AdminCommand(this));

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new LobbyListener(this), this);
        getServer().getPluginManager().registerEvents(new GuiListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new SpecialItemUseListener(this), this);
        getServer().getPluginManager().registerEvents(new ProjectileHitListener(this), this);
        getServer().getPluginManager().registerEvents(new AdminGuiListener(this), this);
        getServer().getPluginManager().registerEvents(new ArenaManagerListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerEditorListener(this), this);
        getServer().getPluginManager().registerEvents(new EditorChatListener(this), this);

        getLogger().info("EnsDaire (EnsDaire Infrastructure) aktif!");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null)
            databaseManager.closeConnection();
        getLogger().info("EnsDaire devre dışı.");
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public VictoryEffects getVictoryEffects() {
        return victoryEffects;
    }

    public LootManager getLootManager() {
        return lootManager;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public CosmeticManager getCosmeticManager() {
        return cosmeticManager;
    }

    public ArenaController getArenaController() {
        return arenaController;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public EditorManager getEditorManager() {
        return editorManager;
    }

    public static EnsDaire getInstance() {
        return instance;
    }
}
