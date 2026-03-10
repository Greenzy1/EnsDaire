package org.byauth.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.byauth.ByCircleGame;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardManager {

    private final ByCircleGame plugin;
    private final Map<UUID, Scoreboard> scoreboards = new HashMap<>();
    private final String[] scrollingTitles = {
            "&b&lENSDAIRE",
            "&f&lE&b&lNSDAIRE",
            "&b&lE&f&lN&b&lSDAIRE",
            "&b&lEN&f&lS&b&lDAIRE",
            "&b&lENS&f&lD&b&lAIRE",
            "&b&lENSD&f&lA&b&lIRE",
            "&b&lENSDA&f&lI&b&lRE",
            "&b&lENSDAI&f&lR&b&lE",
            "&b&lENSDAIR&f&lE",
            "&b&lENSDAIRE"
    };
    private int titleIndex = 0;

    public ScoreboardManager(ByCircleGame plugin) {
        this.plugin = plugin;
        startTitleAnimation();
    }

    private void startTitleAnimation() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            titleIndex = (titleIndex + 1) % scrollingTitles.length;
            String animatedTitle = plugin.getSettingsManager().format(scrollingTitles[titleIndex]);
            for (Player player : Bukkit.getOnlinePlayers()) {
                Objective obj = player.getScoreboard().getObjective("ensdaire");
                if (obj != null) {
                    obj.setDisplayName(animatedTitle);
                }
            }
        }, 0L, 5L);
    }

    public void setScoreboard(Player player, String arenaName, String state, int players, int maxPlayers) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("ensdaire", "dummy",
                plugin.getSettingsManager().format(scrollingTitles[0]));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        int line = 10;
        obj.getScore(plugin.getSettingsManager().format("&7&m------------------")).setScore(line--);
        obj.getScore(plugin.getSettingsManager().format("&fArena: &b" + arenaName)).setScore(line--);
        obj.getScore(plugin.getSettingsManager().format("&fDurum: &e" + state)).setScore(line--);
        obj.getScore(plugin.getSettingsManager().format(" ")).setScore(line--);
        obj.getScore(plugin.getSettingsManager().format("&fOyuncular: &b" + players + "&f/&b" + maxPlayers))
                .setScore(line--);
        obj.getScore(plugin.getSettingsManager().format("  ")).setScore(line--);
        obj.getScore(plugin.getSettingsManager().format("&bplay.ensis.net")).setScore(line--);
        obj.getScore(plugin.getSettingsManager().format("&7&m------------------ ")).setScore(line--);

        player.setScoreboard(board);
        scoreboards.put(player.getUniqueId(), board);
    }
}
