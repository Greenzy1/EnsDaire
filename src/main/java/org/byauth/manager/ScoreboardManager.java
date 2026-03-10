package org.byauth.manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.byauth.EnsDaire;
import org.byauth.game.Arena;
import org.byauth.game.ArenaState;

import java.util.UUID;

public class ScoreboardManager {

    private final EnsDaire plugin;

    public ScoreboardManager(EnsDaire plugin) {
        this.plugin = plugin;
    }

    public void updateScoreboard(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("ensdaire", "dummy", plugin.getSettingsManager().format("&b&lᴇɴꜱᴅᴀɪʀᴇ"));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        Arena arena = plugin.getArenaController().getArenaByPlayer(player);
        
        if (arena == null) {
            setupLobbyScoreboard(player, obj);
        } else {
            setupGameScoreboard(player, arena, obj);
        }

        player.setScoreboard(board);
    }

    private void setupLobbyScoreboard(Player player, Objective obj) {
        addLine(obj, "&7&m------------------", 10);
        addLine(obj, "&fᴏʏᴜɴᴄᴜ: &e" + player.getName(), 9);
        addLine(obj, "&fꜱᴇᴠɪʏᴇ: &e" + "1", 8); // Level integration here
        addLine(obj, " ", 7);
        addLine(obj, "&fçᴏᴠɪᴍɪçɪ: &a" + Bukkit.getOnlinePlayers().size(), 6);
        addLine(obj, "  ", 5);
        addLine(obj, "&bᴇɴꜱɪꜱ.ɴᴇᴛ", 4);
        addLine(obj, "&7&m-------------------", 3);
    }

    private void setupGameScoreboard(Player player, Arena arena, Objective obj) {
        addLine(obj, "&7&m------------------", 10);
        addLine(obj, "&fᴀʀᴇɴᴀ: &e" + arena.getDisplayName(), 9);
        addLine(obj, "&fᴅᴜʀᴜᴍ: &a" + arena.getState(), 8);
        addLine(obj, " ", 7);
        addLine(obj, "&fᴏʏᴜɴᴄᴜʟᴀʀ: &e" + arena.getPlayers().size() + "/" + arena.getMaxPlayers(), 6);
        
        if (arena.getState() == ArenaState.ACTIVE) {
            addLine(obj, "&fᴛᴀᴋɪᴍ: " + arena.getGameManager().getTeamOfPlayer(player).getDisplayName(), 5);
        }
        
        addLine(obj, "  ", 4);
        addLine(obj, "&bᴇɴꜱɪꜱ.ɴᴇᴛ", 3);
        addLine(obj, "&7&m-------------------", 2);
    }

    private void addLine(Objective obj, String text, int score) {
        obj.getScore(plugin.getSettingsManager().format(text)).setScore(score);
    }
}
