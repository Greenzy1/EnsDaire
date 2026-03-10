package org.byauth.manager;

import org.bukkit.entity.Player;
import org.byauth.EnsDaire;
import org.byauth.game.combat.CombatData;
import org.byauth.service.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatManager implements Service {

    private final EnsDaire plugin;
    private final Map<UUID, CombatData> combatCache = new HashMap<>();

    public CombatManager(EnsDaire plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() {}

    public CombatData getCombatData(Player player) {
        return combatCache.computeIfAbsent(player.getUniqueId(), CombatData::new);
    }

    public void handleHit(Player damager, Player victim) {
        var data = getCombatData(damager);
        data.incrementCombo();
        
        if (data.getCombo() >= 3) {
            damager.sendActionBar(plugin.getSettingsManager().format("&6&lᴄᴏᴍʙᴏ! &e" + data.getCombo()));
        }
    }

    @Override
    public void terminate() {
        combatCache.clear();
    }
}
