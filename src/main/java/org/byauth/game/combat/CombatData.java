package org.byauth.game.combat;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatData {
    private int combo = 0;
    private long lastHit = 0;
    private final UUID playerId;

    public CombatData(UUID playerId) {
        this.playerId = playerId;
    }

    public void incrementCombo() {
        long now = System.currentTimeMillis();
        if (now - lastHit > 2000) combo = 0;
        combo++;
        lastHit = now;
    }

    public int getCombo() { return combo; }
    public void reset() { combo = 0; }
}
