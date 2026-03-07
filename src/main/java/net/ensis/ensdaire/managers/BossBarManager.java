package net.ensis.ensdaire.managers;

import net.ensis.ensdaire.EnsDaire;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarManager {

    private final BossBar bar;

    public BossBarManager(EnsDaire plugin) {
        BarColor color;
        BarStyle style;
        try {
            color = BarColor.valueOf(plugin.getConfig().getString("bossbar.color", "BLUE").toUpperCase());
        } catch (Exception e) {
            color = BarColor.BLUE;
        }
        try {
            style = BarStyle.valueOf(plugin.getConfig().getString("bossbar.style", "SOLID").toUpperCase());
        } catch (Exception e) {
            style = BarStyle.SOLID;
        }
        this.bar = Bukkit.createBossBar("§bEnsDaire", color, style);
    }

    public void addPlayer(Player p) {
        bar.addPlayer(p);
    }

    public void removePlayer(Player p) {
        bar.removePlayer(p);
    }

    public void update(String title, double progress) {
        bar.setTitle(title);
        bar.setProgress(Math.max(0, Math.min(1, progress)));
        bar.setVisible(true);
    }

    public void updateCountdown(int current, int total) {
        update("§eOyunun başlamasına §6" + current + "s §ekaldı...", (double) current / total);
    }

    public void updateRoundTimer(int current, int total, int round, int alive) {
        update("§6Round " + round + " §8| §eSüre: §f" + current + "s §8| §aSağ Kalan: §f" + alive,
                (double) current / total);
    }

    public void hide() {
        bar.setVisible(false);
    }
}
