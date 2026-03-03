package net.ensdaireplugin.ensdaire.arena;

import net.ensdaireplugin.ensdaire.EnsDaire;
import net.ensdaireplugin.ensdaire.game.*;
import net.ensdaireplugin.ensdaire.player.PlayerData;
import net.ensdaireplugin.ensdaire.player.StateSnapshot;
import net.ensdaireplugin.ensdaire.utils.FX;
import net.ensdaireplugin.ensdaire.utils.Msg;
import net.ensdaireplugin.ensdaire.utils.SB;
import net.ensdaireplugin.ensdaire.utils.VersionUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

public class Arena {

    private final EnsDaire plugin;
    private final String id;
    private Location lobbySpawn;
    private Location spectatorSpawn;
    private final List<Location> capsuleSpawns = new ArrayList<>();
    private final List<Location> shulkerSpawns = new ArrayList<>();
    private GameState state = GameState.WAITING;
    private final LinkedHashMap<UUID, GamePlayer> players = new LinkedHashMap<>();
    private final Map<UUID, StateSnapshot> snapshots = new HashMap<>();
    private int currentRound = 0;
    private int eliminationOrder = 0;
    private boolean firstBloodDone = false;
    private ModifierType currentModifier = ModifierType.NONE;
    private final CircleManager circleManager;
    private final ShulkerManager shulkerManager;
    private final BossBarManager bossBar;
    private BukkitTask countdownTask;
    private BukkitTask roundTask;
    private BukkitTask circleTask;
    private BukkitTask scoreboardTask;
    private final int minPlayers;
    private final int maxPlayers;
    private final int roundDuration;
    private final int countdownTime;
    private final int releaseDelay;
    private final int slowFallDuration;
    private final int spawnProtection;
    private final int roundEndDelay;

    public Arena(EnsDaire plugin, String id) {
        this.plugin = plugin;
        this.id = id;
        this.circleManager = new CircleManager(plugin);
        this.shulkerManager = new ShulkerManager(plugin, id);
        this.bossBar = new BossBarManager(plugin);
        minPlayers = plugin.getConfig().getInt("game.min-players", 2);
        maxPlayers = plugin.getConfig().getInt("game.max-players", 16);
        roundDuration = plugin.getConfig().getInt("game.round-duration", 60);
        countdownTime = plugin.getConfig().getInt("game.countdown-time", 20);
        releaseDelay = plugin.getConfig().getInt("game.capsule-release-delay", 3);
        slowFallDuration = plugin.getConfig().getInt("game.slow-fall-duration", 60);
        spawnProtection = plugin.getConfig().getInt("game.spawn-protection", 3);
        roundEndDelay = plugin.getConfig().getInt("game.round-end-delay", 5);
    }

    public JoinResult addPlayer(Player player) {
        if (state == GameState.DISABLED) return JoinResult.DISABLED;
        if (state == GameState.RUNNING || state == GameState.ROUND_END || state == GameState.STARTING || state == GameState.ENDING) {
            return joinAsSpectator(player);
        }
        if (players.size() >= maxPlayers) return JoinResult.FULL;
        if (state != GameState.WAITING && state != GameState.COUNTDOWN) return JoinResult.NOT_JOINABLE;
        snapshots.put(player.getUniqueId(), new StateSnapshot(player));
        GamePlayer gp = new GamePlayer(player);
        players.put(player.getUniqueId(), gp);
        prepareForLobby(player);
        bossBar.addPlayer(player);
        SB.updateLobby(plugin, this, player);
        broadcast(Msg.get(plugin, "messages.join", "{current}", String.valueOf(players.size()), "{max}", String.valueOf(maxPlayers)));
        FX.play(player, plugin.getConfig().getString("sounds.countdown", "BLOCK_NOTE_BLOCK_PLING"));
        net.ensdaireplugin.ensdaire.gui.TeamSelectGui.open(plugin, player, this);
        checkAutoStart();
        return JoinResult.SUCCESS;
    }

    private JoinResult joinAsSpectator(Player player) {
        snapshots.put(player.getUniqueId(), new StateSnapshot(player));
        GamePlayer gp = new GamePlayer(player);
        gp.setStatus(GamePlayer.Status.SPECTATOR);
        players.put(player.getUniqueId(), gp);
        makeSpectator(player);
        bossBar.addPlayer(player);
        player.sendMessage(plugin.getConfig().getString("messages.prefix", "") + "§eOyun devam ettiği için izleyici olarak katıldın.");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
        return JoinResult.SUCCESS;
    }

    public void removePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if (!players.containsKey(uuid)) return;
        players.remove(uuid);
        circleManager.removeCircle(uuid);
        bossBar.removePlayer(player);
        SB.clear(player);
        restorePlayer(player);
        broadcast(Msg.get(plugin, "messages.leave"));
        if (state == GameState.RUNNING || state == GameState.ROUND_END) {
            checkAliveCount();
        }
        if (state == GameState.COUNTDOWN && players.size() < minPlayers) {
            cancelCountdown();
        }
        SB.updateLobbyAll(plugin, this);
    }

    private void checkAutoStart() {
        if (players.size() >= minPlayers && state == GameState.WAITING) {
            beginCountdown();
        }
    }

    private void beginCountdown() {
        state = GameState.COUNTDOWN;
        final int[] t = {countdownTime};
        countdownTask = new BukkitRunnable() {
            @Override public void run() {
                if (state != GameState.COUNTDOWN) { cancel(); return; }
                if (t[0] <= 0) { cancel(); launchGame(); return; }
                bossBar.updateCountdown(t[0], countdownTime);
                if (t[0] <= 5) {
                    broadcastTitle("§e" + t[0], "§7Oyun başlıyor!", 3, 22, 5);
                    FX.playAll(players.keySet(), plugin.getConfig().getString("sounds.countdown","BLOCK_NOTE_BLOCK_PLING"));
                } else if (t[0] % 5 == 0) {
                    broadcast("§eOyun §6" + t[0] + " §esaniye içinde başlıyor!");
                }
                SB.updateLobbyAll(plugin, Arena.this);
                t[0]--;
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    private void cancelCountdown() {
        state = GameState.WAITING;
        if (countdownTask != null) { countdownTask.cancel(); countdownTask = null; }
        bossBar.hide();
        broadcast(Msg.get(plugin, "messages.not-enough-players", "{min}", String.valueOf(minPlayers)));
        SB.updateLobbyAll(plugin, this);
    }

    private void launchGame() {
        state = GameState.STARTING;
        currentRound = 0;
        firstBloodDone = false;
        eliminationOrder = (int) players.values().stream().filter(GamePlayer::isAlive).count();
        Set<CircleColor> taken = players.values().stream().map(GamePlayer::getColor).filter(Objects::nonNull).collect(Collectors.toSet());
        List<CircleColor> available = new ArrayList<>(Arrays.asList(CircleColor.values()));
        available.remove(CircleColor.WHITE);
        available.removeAll(taken);
        Collections.shuffle(available);
        int ai = 0;
        for (GamePlayer gp : players.values()) {
            if (gp.isSpectator()) continue;
            gp.setStatus(GamePlayer.Status.ALIVE);
            if (gp.getColor() == null) {
                if (ai < available.size()) gp.setColor(available.get(ai++));
                else gp.setColor(CircleColor.BLACK);
            }
        }
        broadcast(Msg.get(plugin, "messages.game-start"));
        FX.playAll(players.keySet(), plugin.getConfig().getString("sounds.round-start","ENTITY_EXPERIENCE_ORB_PICKUP"));
        teleportToCapsules();
        new BukkitRunnable() {
            @Override public void run() { nextRound(); }
        }.runTaskLater(plugin, releaseDelay * 20L);
    }

    private void teleportToCapsules() {
        List<UUID> uuids = new ArrayList<>(players.keySet());
        for (int i = 0; i < uuids.size(); i++) {
            Player p = Bukkit.getPlayer(uuids.get(i));
            GamePlayer gp = players.get(uuids.get(i));
            if (p == null || gp == null || gp.isSpectator()) continue;
            Location capsuleLoc = getCapsuleFor(i);
            gp.setCapsuleLocation(capsuleLoc);
            p.teleport(capsuleLoc);
            p.setGameMode(GameMode.SURVIVAL);
            p.getInventory().clear();
            p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            p.setFoodLevel(20);
            circleManager.createCircle(uuids.get(i), capsuleLoc, gp.getColor());
            applySpawnEffects(p);
            p.sendTitle(gp.getColor().getChatColor() + "◉ RENGİN", gp.getColor().getChatColor() + "§l" + gp.getColor().name(), 5, 50, 10);
            bossBar.addPlayer(p);
        }
        circleManager.startParticleEffects(players);
    }

    private Location getCapsuleFor(int index) {
        if (!capsuleSpawns.isEmpty()) return capsuleSpawns.get(index % capsuleSpawns.size());
        return lobbySpawn;
    }

    private void applySpawnEffects(Player p) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, slowFallDuration, 0, false, true));
        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, spawnProtection * 20, 4, false, false));
    }

    private void nextRound() {
        currentRound++;
        state = GameState.RUNNING;

        // Rastgele Etkinlik Seç
        List<ModifierType> mods = new ArrayList<>(Arrays.asList(ModifierType.values()));
        mods.remove(ModifierType.NONE);
        Collections.shuffle(mods);
        currentModifier = mods.get(0);

        broadcast(Msg.get(plugin, "modifier-start", "{name}", currentModifier.getDisplayName()));
        broadcast(Msg.get(plugin, currentModifier.getDescriptionKey()));

        if (!shulkerSpawns.isEmpty()) shulkerManager.spawnAll(shulkerSpawns);
        broadcast(Msg.get(plugin, "messages.round-start", "{round}", String.valueOf(currentRound)));
        broadcastTitle("§6Round §e" + currentRound, "§7Etkinlik: §e" + currentModifier.getDisplayName(), 5, 40, 10);
        FX.playAll(players.keySet(), plugin.getConfig().getString("sounds.round-start","ENTITY_EXPERIENCE_ORB_PICKUP"));

        // Etkinlik Efektlerini Uygula
        for (GamePlayer gp : players.values()) {
            Player p = Bukkit.getPlayer(gp.getUuid());
            if (p == null || !gp.isAlive()) continue;
            if (currentModifier == ModifierType.LOW_GRAVITY) p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1200, 2));
            if (currentModifier == ModifierType.SPEED) p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 2));
        }

        startCircleCheck();
        startRoundTimer();
        startScoreboardUpdater();
    }

    private void startRoundTimer() {
        if (roundTask != null) roundTask.cancel();
        final int[] t = {roundDuration};
        roundTask = new BukkitRunnable() {
            @Override public void run() {
                if (state != GameState.RUNNING) { cancel(); return; }
                bossBar.updateRoundTimer(t[0], roundDuration, currentRound, getAlivePlayers().size());
                if (plugin.getConfig().getBoolean("actionbar.enabled", true)) {
                    String timerColor = t[0] <= 10 ? "§c" : t[0] <= 30 ? "§e" : "§a";
                    String bar = buildTimerBar(t[0], roundDuration);
                    for (GamePlayer gp : players.values()) {
                        Player p = Bukkit.getPlayer(gp.getUuid());
                        if (p != null) p.sendActionBar(timerColor + "⏱ " + t[0] + "s  " + bar + "  §7Sağ kalan: §e" + getAlivePlayers().size());
                    }
                }
                if (t[0] <= 10 && t[0] > 0) FX.playAll(players.keySet(), "BLOCK_NOTE_BLOCK_PLING");
                if (t[0] <= 0) { cancel(); endRound(); return; }
                t[0]--;
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    private String buildTimerBar(int current, int total) {
        int filled = (int) Math.round(10.0 * current / total);
        StringBuilder sb = new StringBuilder("§8[");
        for (int i = 0; i < 10; i++) {
            if (i < filled) sb.append(current <= 10 ? "§c" : current <= 30 ? "§e" : "§a").append("█");
            else sb.append("§8░");
        }
        return sb.append("§8]").toString();
    }

    private void startCircleCheck() {
        if (circleTask != null) circleTask.cancel();
        int interval = plugin.getConfig().getInt("game.circle-check-interval", 4);
        circleTask = new BukkitRunnable() {
            @Override public void run() {
                if (state != GameState.RUNNING) { cancel(); return; }
                for (GamePlayer gp : new ArrayList<>(players.values())) {
                    if (!gp.isAlive() || gp.getCapsuleLocation() == null) continue;
                    Player p = Bukkit.getPlayer(gp.getUuid());
                    if (p == null) continue;
                    if (!circleManager.isInsideCircle(gp.getUuid(), p.getLocation())) eliminate(p, null, EliminationCause.CIRCLE);
                }
            }
        }.runTaskTimer(plugin, interval, interval);
    }

    private void startScoreboardUpdater() {
        if (scoreboardTask != null) scoreboardTask.cancel();
        int interval = plugin.getConfig().getInt("scoreboard.update-interval", 10);
        scoreboardTask = new BukkitRunnable() {
            @Override public void run() {
                if (state == GameState.WAITING || state == GameState.DISABLED) { cancel(); return; }
                for (GamePlayer gp : players.values()) {
                    Player p = Bukkit.getPlayer(gp.getUuid());
                    if (p != null) SB.updateGame(plugin, Arena.this, p);
                }
            }
        }.runTaskTimer(plugin, 5L, interval);
    }

    private void endRound() {
        state = GameState.ROUND_END;
        stopCircleTask();
        shulkerManager.clearAll();
        int surviveTokens = plugin.getConfig().getInt("tokens.survive-round", 5);
        if (currentModifier == ModifierType.DOUBLE_TOKEN) surviveTokens *= 2;

        for (GamePlayer gp : getAlivePlayers()) {
            gp.addRoundSurvived();
            gp.addTokens(surviveTokens);
            plugin.getPlayerDataManager().addTokens(gp.getUuid(), surviveTokens);
            Player p = Bukkit.getPlayer(gp.getUuid());
            if (p != null) p.sendMessage(Msg.get(plugin, "messages.token-earned", "{amount}", String.valueOf(surviveTokens), "{total}", String.valueOf(plugin.getPlayerDataManager().getTokens(gp.getUuid()))));
        }
        broadcast(Msg.get(plugin, "messages.round-end", "{round}", String.valueOf(currentRound)));
        bossBar.update("§7Round §e" + currentRound + " §7tamamlandı.", 0.0);
        new BukkitRunnable() {
            @Override public void run() { returnToCapsules(); }
        }.runTaskLater(plugin, roundEndDelay * 20L);
    }

    private void returnToCapsules() {
        List<GamePlayer> alive = getAlivePlayers();
        for (GamePlayer gp : alive) {
            Player p = Bukkit.getPlayer(gp.getUuid());
            if (p == null) continue;
            p.teleport(gp.getCapsuleLocation());
            p.getInventory().clear();
            p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            applySpawnEffects(p);
        }
        new BukkitRunnable() {
            @Override public void run() {
                if (getAlivePlayers().size() <= 1) finishGame();
                else nextRound();
            }
        }.runTaskLater(plugin, 40L);
    }

    public enum EliminationCause { CIRCLE, KILLED, DISCONNECT }

    public void eliminate(Player victim, Player killer, EliminationCause cause) {
        GamePlayer gp = players.get(victim.getUniqueId());
        if (gp == null || !gp.isAlive()) return;
        gp.setStatus(GamePlayer.Status.SPECTATOR);
        gp.setPlacement(eliminationOrder--);
        plugin.getPlayerDataManager().addDeath(victim.getUniqueId());
        gp.setColor(CircleColor.WHITE);
        circleManager.updateCircleColor(victim.getUniqueId(), CircleColor.WHITE);
        switch (cause) {
            case CIRCLE -> broadcast(Msg.get(plugin, "messages.eliminated-circle") + " §8(§7" + gp.getColoredName() + "§8)");
            case KILLED -> {
                broadcast(Msg.get(plugin, "messages.eliminated-killed", "{killer}", killer != null ? killer.getName() : "?") + " §8(§7" + gp.getColoredName() + "§8)");
                if (killer != null) handleKill(killer, gp);
            }
            case DISCONNECT -> broadcast("§7" + gp.getColoredName() + " §7bağlantısı kesildi.");
        }
        victim.sendMessage(cause == EliminationCause.CIRCLE ? Msg.get(plugin, "messages.eliminated-circle") : Msg.get(plugin, "messages.eliminated-killed", "{killer}", killer != null ? killer.getName() : "?"));
        if (plugin.getConfig().getBoolean("effects.elimination-effect", true)) FX.elimination(victim.getLocation());
        FX.play(victim, plugin.getConfig().getString("sounds.elimination","ENTITY_PLAYER_DEATH"));
        makeSpectator(victim);
        circleManager.removeCircle(victim.getUniqueId());
        checkAliveCount();
    }

    private void handleKill(Player killer, GamePlayer victim) {
        GamePlayer killerGP = players.get(killer.getUniqueId());
        if (killerGP == null) return;
        killerGP.addKill();
        int killTokens = plugin.getConfig().getInt("tokens.kill", 10);
        int killPoints = plugin.getConfig().getInt("points.kill", 5);
        if (!firstBloodDone) {
            firstBloodDone = true;
            killerGP.setGotFirstBlood(true);
            int fbBonus = plugin.getConfig().getInt("tokens.first-blood", 15);
            int fbPoints = plugin.getConfig().getInt("points.first-blood", 10);
            killTokens += fbBonus;
            killPoints += fbPoints;
            broadcast(Msg.get(plugin, "messages.first-blood", "{player}", killerGP.getColoredName()));
        }
        killerGP.addTokens(killTokens);
        plugin.getPlayerDataManager().addTokens(killer.getUniqueId(), killTokens);
        plugin.getPlayerDataManager().addPoints(killer.getUniqueId(), killPoints);
        killer.sendMessage(Msg.get(plugin, "messages.token-earned", "{amount}", String.valueOf(killTokens), "{total}", String.valueOf(plugin.getPlayerDataManager().getTokens(killer.getUniqueId()))));
    }

    private void makeSpectator(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        player.getInventory().clear();
        if (spectatorSpawn != null) player.teleport(spectatorSpawn);
    }

    private void checkAliveCount() {
        List<GamePlayer> alive = getAlivePlayers();
        if (state == GameState.RUNNING && alive.size() <= 1) {
            stopRoundTask();
            stopCircleTask();
            shulkerManager.clearAll();
            new BukkitRunnable() {
                @Override public void run() { finishGame(); }
            }.runTaskLater(plugin, 40L);
        }
    }

    private void finishGame() {
        state = GameState.ENDING;
        stopAllTasks();
        circleManager.stopParticleEffects();
        bossBar.hide();
        List<GamePlayer> alive = getAlivePlayers();
        GamePlayer winner = alive.isEmpty() ? null : alive.get(0);
        if (winner != null) {
            winner.setPlacement(1);
            int winTokens = plugin.getConfig().getInt("tokens.win", 50);
            int winPoints = plugin.getConfig().getInt("points.win", 25);
            int top3Tokens = plugin.getConfig().getInt("tokens.top3-bonus", 20);
            int top3Points = plugin.getConfig().getInt("points.top3-bonus", 10);
            for (GamePlayer gp : players.values()) {
                if (gp.getPlacement() <= 3 && gp.getPlacement() > 0) {
                    gp.addTokens(top3Tokens);
                    plugin.getPlayerDataManager().addTokens(gp.getUuid(), top3Tokens);
                    plugin.getPlayerDataManager().addPoints(gp.getUuid(), top3Points);
                }
            }
            winner.addTokens(winTokens);
            plugin.getPlayerDataManager().addTokens(winner.getUuid(), winTokens);
            plugin.getPlayerDataManager().addPoints(winner.getUuid(), winPoints);
            plugin.getPlayerDataManager().addWin(winner.getUuid());
            broadcast(Msg.get(plugin, "messages.win", "{player}", winner.getColoredName()));
            broadcastTitle("§6§l🏆 KAZANAN 🏆", winner.getColoredName(), 10, 80, 20);
            Player winnerPlayer = Bukkit.getPlayer(winner.getUuid());
            if (winnerPlayer != null) {
                winnerPlayer.sendMessage("§6§l🏆 Kazandın! §e+" + winTokens + " jeton!");
                FX.play(winnerPlayer, plugin.getConfig().getString("sounds.win","UI_TOAST_CHALLENGE_COMPLETE"));
                if (plugin.getConfig().getBoolean("game.win-fireworks", true)) launchFireworks(winnerPlayer);
            }
        } else broadcast("§7Kazanan yok. Oyun berabere bitti.");
        for (GamePlayer gp : players.values()) {
            plugin.getPlayerDataManager().addGame(gp.getUuid());
            plugin.getPlayerDataManager().addKills(gp.getUuid(), gp.getKills());
            plugin.getPlayerDataManager().checkAndApplyRankUp(gp.getUuid());
        }
        new BukkitRunnable() {
            @Override public void run() { showResults(); }
        }.runTaskLater(plugin, 60L);
        new BukkitRunnable() {
            @Override public void run() { reset(); }
        }.runTaskLater(plugin, 200L);
    }

    private void showResults() {
        List<GamePlayer> sorted = players.values().stream().filter(gp -> gp.getPlacement() > 0).sorted(Comparator.comparingInt(GamePlayer::getPlacement)).collect(Collectors.toList());
        String sep = "§8§m════════════════════";
        broadcast(sep);
        broadcast("§6§l       OYUN SONUÇLARI");
        broadcast(sep);
        for (int i = 0; i < Math.min(sorted.size(), 5); i++) {
            GamePlayer gp = sorted.get(i);
            String medal = switch (i) {
                case 0 -> "§6§l🥇";
                case 1 -> "§7§l🥈";
                case 2 -> "§c§l🥉";
                default -> "§8#" + (i + 1);
            };
            broadcast(medal + " §f" + gp.getColoredName() + " §8| §eÖldürme: §f" + gp.getKills() + " §8| §eJeton: §f+" + gp.getTokensEarned());
        }
        broadcast(sep);
    }

    private void launchFireworks(Player player) {
        new BukkitRunnable() {
            int count = 0;
            @Override public void run() {
                if (count++ >= 5 || !player.isOnline()) { cancel(); return; }
                Location loc = player.getLocation().add((Math.random() - 0.5) * 4, 1, (Math.random() - 0.5) * 4);
                Firework fw = player.getWorld().spawn(loc, Firework.class);
                FireworkMeta meta = fw.getFireworkMeta();
                meta.addEffect(FireworkEffect.builder().withColor(Color.YELLOW, Color.ORANGE).withFade(Color.WHITE).with(FireworkEffect.Type.BURST).trail(true).flicker(true).build());
                meta.setPower(1);
                fw.setFireworkMeta(meta);
            }
        }.runTaskTimer(plugin, 5L, 10L);
    }

    private void reset() {
        for (UUID uuid : new ArrayList<>(players.keySet())) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                restorePlayer(p);
                bossBar.removePlayer(p);
                SB.clear(p);
            }
        }
        players.clear();
        snapshots.clear();
        circleManager.removeAllCircles();
        shulkerManager.clearAll();
        clearDroppedItems();
        currentRound = 0;
        eliminationOrder = 0;
        firstBloodDone = false;
        state = GameState.WAITING;
    }

    private void clearDroppedItems() {
        if (lobbySpawn == null) return;
        World world = lobbySpawn.getWorld();
        for (org.bukkit.entity.Entity entity : world.getEntitiesByClass(Item.class)) {
            if (entity.getLocation().distanceSquared(lobbySpawn) < 10000) entity.remove();
        }
    }

    private void prepareForLobby(Player p) {
        if (lobbySpawn != null) p.teleport(lobbySpawn);
        p.setGameMode(GameMode.ADVENTURE);
        p.getInventory().clear();
        p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        p.setFoodLevel(20);
        for (PotionEffect effect : p.getActivePotionEffects()) p.removePotionEffect(effect.getType());
    }

    private void restorePlayer(Player p) {
        StateSnapshot ss = snapshots.remove(p.getUniqueId());
        if (ss != null) ss.restore(p);
        else {
            p.setGameMode(GameMode.SURVIVAL);
            p.getInventory().clear();
            p.setAllowFlight(false);
            for (PotionEffect effect : p.getActivePotionEffects()) p.removePotionEffect(effect.getType());
        }
        if (lobbySpawn != null) p.teleport(lobbySpawn);
    }

    public void broadcast(String msg) {
        String prefix = plugin.getConfig().getString("messages.prefix", "§8[§bEnsDaire§8] §r");
        for (UUID uuid : players.keySet()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) p.sendMessage(prefix + msg);
        }
    }

    public void broadcastTitle(String title, String sub, int fi, int stay, int fo) {
        for (UUID uuid : players.keySet()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) p.sendTitle(title, sub, fi, stay, fo);
        }
    }

    private void stopRoundTask() {
        if (roundTask != null) { roundTask.cancel(); roundTask = null; }
    }

    private void stopCircleTask() {
        if (circleTask != null) { circleTask.cancel(); circleTask = null; }
    }

    public void startGame() { launchGame(); }

    public void stopAllTasks() {
        if (countdownTask != null) { countdownTask.cancel(); countdownTask = null; }
        stopRoundTask();
        stopCircleTask();
        if (scoreboardTask != null) { scoreboardTask.cancel(); scoreboardTask = null; }
    }

    public List<GamePlayer> getAlivePlayers() { return players.values().stream().filter(GamePlayer::isAlive).collect(Collectors.toList()); }
    public boolean hasPlayer(UUID uuid) { return players.containsKey(uuid); }
    public GamePlayer getGamePlayer(UUID uuid) { return players.get(uuid); }
    public Map<UUID, GamePlayer> getPlayers() { return players; }
    public String getId() { return id; }
    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }
    public int getCurrentRound() { return currentRound; }
    public int getMinPlayers() { return minPlayers; }
    public int getMaxPlayers() { return maxPlayers; }
    public void setLobbySpawn(Location l) { this.lobbySpawn = l; }
    public Location getLobbySpawn() { return lobbySpawn; }
    public void setSpectatorSpawn(Location l) { this.spectatorSpawn = l; }
    public Location getSpectatorSpawn() { return spectatorSpawn; }
    public List<Location> getCapsuleSpawns() { return capsuleSpawns; }
    public List<Location> getShulkerSpawns() { return shulkerSpawns; }
    public CircleManager getCircleManager() { return circleManager; }
    public ShulkerManager getShulkerManager() { return shulkerManager; }
    public BossBarManager getBossBar() { return bossBar; }

    public enum JoinResult { SUCCESS, FULL, NOT_JOINABLE, DISABLED }
}
