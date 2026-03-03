package net.ensdaireplugin.ensdaire.utils;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Collection;
import java.util.UUID;

public class FX {

    public static void play(Player p, String soundName) {
        if (soundName == null || soundName.isEmpty()) return;
        try {
            p.playSound(p.getLocation(), Sound.valueOf(soundName.toUpperCase()), 1f, 1f);
        } catch (Exception ignored) {}
    }

    public static void playAll(Collection<UUID> uuids, String soundName) {
        if (soundName == null || soundName.isEmpty()) return;
        for (UUID uuid : uuids) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) play(p, soundName);
        }
    }

    public static void elimination(Location loc) {
        loc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, loc.add(0, 1, 0), 1);
        loc.getWorld().spawnParticle(Particle.FLAME, loc, 30, 0.5, 0.5, 0.5, 0.1);
        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
    }

    public static void spawnFirework(Location loc) {
        Firework fw = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder()
                .withColor(Color.YELLOW, Color.ORANGE, Color.RED)
                .withFade(Color.WHITE)
                .with(FireworkEffect.Type.BALL_LARGE)
                .trail(true)
                .flicker(true)
                .build());
        meta.setPower(1);
        fw.setFireworkMeta(meta);
    }
}
