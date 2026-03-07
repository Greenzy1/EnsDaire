package net.ensis.ensdaire.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.ensis.ensdaire.EnsDaire;
import net.ensis.ensdaire.models.Arena;
import net.ensis.ensdaire.models.PlayerData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnsDaireExpansion extends PlaceholderExpansion {

    private final EnsDaire plugin;

    public EnsDaireExpansion(EnsDaire plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ensdaire";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Ensis";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.equalsIgnoreCase("toplam_oyuncu")) {
            int total = 0;
            for (Arena a : plugin.getArenaManager().all()) total += a.getPlayers().size();
            return String.valueOf(total);
        }

        if (params.equalsIgnoreCase("aktif_arena")) {
            int active = 0;
            for (Arena a : plugin.getArenaManager().all()) {
                if (a.getState() == net.ensis.ensdaire.models.GameState.RUNNING) active++;
            }
            return String.valueOf(active);
        }

        if (params.startsWith("top_isim_")) {
            try {
                int rank = Integer.parseInt(params.replace("top_isim_", ""));
                List<PlayerData> top = plugin.getPlayerDataManager().getTop(rank);
                if (top.size() >= rank) return top.get(rank - 1).getName();
                return "Boş";
            } catch (Exception e) { return "Hata"; }
        }

        if (params.startsWith("top_token_")) {
            try {
                int rank = Integer.parseInt(params.replace("top_token_", ""));
                List<PlayerData> top = plugin.getPlayerDataManager().getTop(rank);
                if (top.size() >= rank) return String.valueOf(top.get(rank - 1).getTokens());
                return "0";
            } catch (Exception e) { return "0"; }
        }

        if (player == null) return "";

        PlayerData data = plugin.getPlayerDataManager().get(player.getUniqueId());
        if (data == null) return "0";

        return switch (params.toLowerCase()) {
            case "jeton" -> String.valueOf(data.getTokens());
            case "kazanma" -> String.valueOf(data.getWins());
            case "puan" -> String.valueOf(data.getPoints());
            case "oldurme" -> String.valueOf(data.getKills());
            case "olme" -> String.valueOf(data.getDeaths());
            case "kd" -> data.getKD();
            case "winrate" -> String.format("%.1f", data.getWinRate());
            case "rutbe" -> data.getRank();
            default -> null;
        };
    }
}
