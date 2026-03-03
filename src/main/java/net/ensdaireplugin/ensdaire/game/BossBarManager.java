package net.ensdaireplugin.ensdaire.game;

import net.ensdaireplugin.ensdaire.EnsDaire;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarManager {
    private final EnsDaire plugin;
    private final BossBar bar;

    public BossBarManager(EnsDaire plugin) {
        this.plugin = plugin;
        this.bar = Bukkit.createBossBar("EnsDaire", BarColor.BLUE, BarStyle.SOLID);
        this.bar.setVisible(false);
    }

    public void addPlayer(Player p) {
        bar.addPlayer(p);
        bar.setVisible(true);
    }

    public void removePlayer(Player p) {
        bar.removePlayer(p);
        if (bar.getPlayers().isEmpty()) bar.setVisible(false);
    }

    public void updateCountdown(int current, int total) {
        double progress = (double) current / total;
        bar.setTitle("§eOyunun başlamasına §6" + current + " §esaniye...");
        bar.setProgress(Math.max(0, Math.min(1, progress)));
        bar.setColor(BarColor.YELLOW);
    }

    public void updateRoundTimer(int current, int total, int round, int alive) {
        double progress = (double) current / total;
        bar.setTitle("§6Round " + round + " §8| §eKalan Süre: §f" + current + "s §8| §7Sağ Kalan: §a" + alive);
        bar.setProgress(Math.max(0, Math.min(1, progress)));
        bar.setColor(current <= 10 ? BarColor.RED : BarColor.GREEN);
    }

    public void update(String title, double progress) {
        bar.setTitle(title);
        bar.setProgress(Math.max(0, Math.min(1, progress)));
    }

    public void hide() {
        bar.setVisible(false);
    }
}
