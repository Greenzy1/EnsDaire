package net.ensdaireplugin.ensdaire.player;

import net.ensdaireplugin.ensdaire.EnsDaire;
import net.ensdaireplugin.ensdaire.utils.Msg;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerDataManager {

    private final EnsDaire plugin;
    private final Map<UUID, PlayerData> cache = new HashMap<>();
    private final SqliteDB db;

    public PlayerDataManager(EnsDaire plugin) {
        this.plugin = plugin;
        this.db = new SqliteDB(plugin);
    }

    public PlayerData getOrCreate(UUID uuid, String name) {
        if (cache.containsKey(uuid)) {
            cache.get(uuid).setName(name);
            return cache.get(uuid);
        }
        PlayerData data = db.load(uuid, name);
        cache.put(uuid, data);
        return data;
    }

    public PlayerData get(UUID uuid) {
        return cache.computeIfAbsent(uuid, k -> {
            Player p = Bukkit.getPlayer(k);
            return db.load(k, p != null ? p.getName() : "Unknown");
        });
    }

    public long getTokens(UUID uuid)   { return get(uuid).getTokens(); }
    public long getPoints(UUID uuid)   { return get(uuid).getPoints(); }
    public int getWins(UUID uuid)     { return get(uuid).getWins(); }
    public int getKills(UUID uuid)    { return get(uuid).getKills(); }
    public int getDeaths(UUID uuid)   { return get(uuid).getDeaths(); }

    public void addTokens(UUID uuid, int amount) {
        PlayerData d = get(uuid); d.addTokens(amount); db.save(d);
    }
    
    public void addPoints(UUID uuid, long amount) {
        PlayerData d = get(uuid); d.addPoints(amount); db.save(d);
    }

    public void addWin(UUID uuid)      { PlayerData d = get(uuid); d.addWin();  db.save(d); }
    public void addGame(UUID uuid)     { PlayerData d = get(uuid); d.addGame(); db.save(d); }
    public void addKills(UUID uuid, int k) { PlayerData d = get(uuid); d.addKills(k); db.save(d); }
    public void addDeath(UUID uuid)    { PlayerData d = get(uuid); d.addDeath(); db.save(d); }

    public void checkAndApplyRankUp(UUID uuid) {
        PlayerData data = get(uuid);
        Player player = Bukkit.getPlayer(uuid);
        List<Map<?, ?>> ranks = plugin.getConfig().getMapList("ranks");

        String bestRank = "Çaylak";
        String bestPrefix = "§7[Çaylak]";

        for (Map<?, ?> r : ranks) {
            long req = ((Number) r.get("token-requirement")).longValue();
            if (data.getTokens() >= req) {
                bestRank   = (String) r.get("name");
                bestPrefix = (String) r.get("prefix");
            }
        }

        if (!bestRank.equals(data.getRank())) {
            data.setRank(bestRank);
            db.save(data);
            if (player != null) {
                player.sendMessage(Msg.get(plugin, "messages.rank-up", "{rank}", bestPrefix + " §r" + bestRank));
                player.sendTitle("§6§lRÜTBE YÜKSELT!", bestPrefix + " §r" + bestRank, 10, 60, 20);
            }
        }
    }

    public List<PlayerData> getTop(int limit) { return db.getTop(limit); }

    public void save(UUID uuid)  { if (cache.containsKey(uuid)) db.save(cache.get(uuid)); }

    public void saveAll() { cache.values().forEach(db::save); }

    public void unload(UUID uuid) { save(uuid); cache.remove(uuid); }
}
