package net.ensis.ensdaire.utils;

import net.ensis.ensdaire.EnsDaire;
import net.ensis.ensdaire.models.Arena;
import net.ensis.ensdaire.models.GamePlayer;
import net.ensis.ensdaire.models.GameState;
import net.ensis.ensdaire.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.UUID;

public class SB {

    public static void updateLobby(EnsDaire plugin, Arena arena, Player player) {
        if (!plugin.getConfig().getBoolean("scoreboard.enabled", true))
            return;
        PlayerData data = plugin.getPlayerDataManager().get(player.getUniqueId());
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("ed_lobby", Criteria.DUMMY,
                ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfig().getString("scoreboard.title", "&b&l✦ EnsDaire ✦")));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        int l = 15;
        set(obj, "&7 ", l--);
        set(obj, "&eᴀʀᴇɴᴀ &8» &f" + arena.getId(), l--);
        set(obj, "&eᴏʏᴜɴᴄᴜ &8» &f" + arena.getPlayers().size() + "&7/&f" + arena.getMaxPlayers(), l--);
        set(obj, "&7  ", l--);
        String stateStr = arena.getState() == GameState.COUNTDOWN ? "&aɢᴇʀɪ ꜱᴀʏɪᴍ..." : "&7ʙᴇᴋʟᴇɴɪʏᴏʀ";
        set(obj, "&eᴅᴜʀᴜᴍ &8» " + stateStr, l--);
        set(obj, "&7   ", l--);
        set(obj, "&6ᴊᴇᴛᴏɴ &8» &f" + data.getTokens(), l--);
        set(obj, "&6ɢᴀʟɪʙɪʏᴇᴛ &8» &f" + data.getWins(), l--);
        set(obj, "&6ᴘᴜᴀɴ &8» &f" + data.getPoints(), l--);
        set(obj, "&6ʀüᴛʙᴇ &8» &f" + data.getRank(), l--);
        set(obj, "&7    ", l--);
        set(obj, plugin.getConfig().getString("scoreboard.footer", "&7ensis.net"), l);
        player.setScoreboard(board);
    }

    public static void updateLobbyAll(EnsDaire plugin, Arena arena) {
        for (UUID uuid : arena.getPlayers().keySet()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null)
                updateLobby(plugin, arena, p);
        }
    }

    public static void updateGame(EnsDaire plugin, Arena arena, Player player) {
        if (!plugin.getConfig().getBoolean("scoreboard.enabled", true))
            return;
        GamePlayer gp = arena.getGamePlayer(player.getUniqueId());
        if (gp == null)
            return;
        PlayerData data = plugin.getPlayerDataManager().get(player.getUniqueId());
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("ed_game", Criteria.DUMMY,
                ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfig().getString("scoreboard.title", "&b&l✦ EnsDaire ✦")));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        int l = 15;
        set(obj, "&7 ", l--);
        set(obj, "&eʀᴏᴜɴᴅ &8» &f" + arena.getCurrentRound(), l--);
        set(obj, "&eꜱᴀĞ ᴋᴀʟᴀɴ &8» &f" + arena.getAlivePlayers().size(), l--);
        set(obj, "&7  ", l--);
        if (gp.getColor() != null)
            set(obj, "&eʀᴇɴɢɪɴ &8» " + gp.getColor().getDisplayName(), l--);
        set(obj, "&eöʟᴅüʀᴍᴇ &8» &f" + gp.getKills(), l--);
        set(obj, "&eᴊᴇᴛᴏɴ &8» &f" + data.getTokens(), l--);
        set(obj, "&7   ", l--);
        String statusStr = gp.isAlive() ? "&a⬤ ʜᴀʏᴀᴛᴛᴀ" : "&c⬤ ɪᴢʟᴇʏɪᴄɪ";
        set(obj, statusStr, l--);
        set(obj, "&7    ", l--);
        set(obj, plugin.getConfig().getString("scoreboard.footer", "&7ensis.net"), l);
        player.setScoreboard(board);
    }

    public static void clear(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    private static void set(Objective obj, String text, int score) {
        String coloredText = ChatColor.translateAlternateColorCodes('&', text);
        if (coloredText.length() > 40)
            coloredText = coloredText.substring(0, 40);
        obj.getScore(coloredText).setScore(score);
    }
}
