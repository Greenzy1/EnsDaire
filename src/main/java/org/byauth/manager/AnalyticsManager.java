package org.byauth.manager;

import org.byauth.EnsDaire;
import org.byauth.service.Service;

import java.util.HashMap;
import java.util.Map;

public class AnalyticsManager implements Service {

    private final EnsDaire plugin;
    private final Map<String, Integer> arenaPlayCount = new HashMap<>();
    private long totalGameTime = 0;

    public AnalyticsManager(EnsDaire plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() {}

    public void trackGame(String arenaId, long duration) {
        arenaPlayCount.put(arenaId, arenaPlayCount.getOrDefault(arenaId, 0) + 1);
        totalGameTime += duration;
    }

    public void logSummary() {
        plugin.getLogger().info("=== ᴇɴꜱᴅᴀɪʀᴇ ᴀɴᴀʟɪᴛɪᴋ öᴢᴇᴛ ===");
        arenaPlayCount.forEach((id, count) -> plugin.getLogger().info("ᴀʀᴇɴᴀ: " + id + " - ᴏʏɴᴀɴᴍᴀ: " + count));
    }

    @Override
    public void terminate() {
        logSummary();
    }
}
