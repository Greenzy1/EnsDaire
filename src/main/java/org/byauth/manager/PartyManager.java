package org.byauth.manager;

import org.bukkit.entity.Player;
import org.byauth.EnsDaire;
import org.byauth.data.Party;
import org.byauth.service.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PartyManager implements Service {

    private final EnsDaire plugin;
    private final Map<UUID, Party> parties = new HashMap<>();
    private final Map<UUID, UUID> playerPartyMap = new HashMap<>();

    public PartyManager(EnsDaire plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() {}

    public void createParty(Player leader) {
        var party = new Party(leader.getUniqueId());
        parties.put(leader.getUniqueId(), party);
        playerPartyMap.put(leader.getUniqueId(), leader.getUniqueId());
        leader.sendMessage(plugin.getSettingsManager().format("&aParti başarıyla oluşturuldu!"));
    }

    public void joinParty(Player player, Player leader) {
        var party = parties.get(leader.getUniqueId());
        if (party != null) {
            party.members().add(player.getUniqueId());
            playerPartyMap.put(player.getUniqueId(), leader.getUniqueId());
            player.sendMessage(plugin.getSettingsManager().format("&aPartiye katıldın: &e" + leader.getName()));
        }
    }

    public Party getParty(Player player) {
        var leaderId = playerPartyMap.get(player.getUniqueId());
        return leaderId != null ? parties.get(leaderId) : null;
    }

    @Override
    public void terminate() {
        parties.clear();
        playerPartyMap.clear();
    }
}
