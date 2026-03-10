package org.byauth;

import org.bukkit.plugin.java.JavaPlugin;
import org.byauth.controller.ArenaController;
import org.byauth.manager.*;
import org.byauth.command.AdminCommand;
import org.byauth.listener.*;
import org.byauth.utils.SettingsManager;
import org.byauth.utils.VictoryEffects;
import org.byauth.service.Service;

import java.util.LinkedHashMap;
import java.util.Map;

public final class EnsDaire extends JavaPlugin {

    private static EnsDaire instance;
    private final Map<Class<? extends Service>, Service> services = new LinkedHashMap<>();

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
        
        initializeCore();
        registerServices();
        registerEvents();
        registerCommands();
        
        services.values().forEach(Service::init);
    }

    private void initializeCore() {
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
    }

    private void registerServices() {
        services.put(LevelManager.class, new LevelManager(this));
        services.put(QuestManager.class, new QuestManager(this));
        services.put(PartyManager.class, new PartyManager(this));
        services.put(AchievementManager.class, new AchievementManager(this));
        services.put(GameEventManager.class, new GameEventManager(this));
        services.put(CombatManager.class, new CombatManager(this));
        services.put(ReplayManager.class, new ReplayManager(this));
        services.put(WebHookManager.class, new WebHookManager(this));
        services.put(AnalyticsManager.class, new AnalyticsManager(this));
        services.put(TournamentManager.class, new TournamentManager(this));
    }

    private void registerEvents() {
        var pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerJoinListener(this), this);
        pm.registerEvents(new PlayerQuitListener(this), this);
        pm.registerEvents(new LobbyListener(this), this);
        pm.registerEvents(new GuiListener(this), this);
        pm.registerEvents(new BlockBreakListener(this), this);
        pm.registerEvents(new BlockPlaceListener(this), this);
        pm.registerEvents(new PlayerMoveListener(this), this);
        pm.registerEvents(new PlayerDeathListener(this), this);
        pm.registerEvents(new SpecialItemUseListener(this), this);
        pm.registerEvents(new ProjectileHitListener(this), this);
        pm.registerEvents(new AdminGuiListener(this), this);
        pm.registerEvents(new ArenaManagerListener(this), this);
        pm.registerEvents(new PlayerEditorListener(this), this);
        pm.registerEvents(new EditorChatListener(this), this);
    }

    private void registerCommands() {
        getCommand("ensdaire").setExecutor(new AdminCommand(this));
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.closeConnection();
        }
        
        services.values().forEach(Service::terminate);
    }

    public static EnsDaire getInstance() {
        return instance;
    }

    public <T extends Service> T getService(Class<T> serviceClass) {
        return serviceClass.cast(services.get(serviceClass));
    }

    public ArenaController getArenaController() { return arenaController; }
    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public PlayerDataManager getPlayerDataManager() { return playerDataManager; }
    public LootManager getLootManager() { return lootManager; }
    public ShopManager getShopManager() { return shopManager; }
    public CosmeticManager getCosmeticManager() { return cosmeticManager; }
    public ScoreboardManager getScoreboardManager() { return scoreboardManager; }
    public HologramManager getHologramManager() { return hologramManager; }
    public GuiManager getGuiManager() { return guiManager; }
    public SettingsManager getSettingsManager() { return settingsManager; }
    public VictoryEffects getVictoryEffects() { return victoryEffects; }
    public EditorManager getEditorManager() { return editorManager; }
}
