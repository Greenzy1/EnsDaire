package org.byauth.utils;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.byauth.EnsDaire;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VictoryEffects {

    private final EnsDaire plugin;
    private final Random random = new Random();

    public VictoryEffects(EnsDaire plugin) {
        this.plugin = plugin;
    }

    public void playRandomVictoryEffect(Player player) {
        int r = random.nextInt(3);
        switch (r) {
            case 0 -> playLightningStorm(player);
            case 1 -> playDragonRoar(player);
            default -> playCosmicVictory(player);
        }
    }

    public void playLightningStorm(Player player) {
        new BukkitRunnable() {
            private int ticks = 0;
            private final Location center = player.getLocation();
            private final List<ArmorStand> tridents = new ArrayList<>();

            @Override
            public void run() {
                if (!player.isOnline() || ticks >= 100) {
                    tridents.forEach(Entity -> Entity.remove());
                    this.cancel();
                    return;
                }

                if (ticks == 0) {
                    center.getWorld().playSound(center, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.2f, 0.8f);
                    for (int i = 0; i < 4; i++) {
                        double angle = (Math.PI / 2) * i;
                        Location spawnLoc = center.clone().add(3 * Math.cos(angle), 0, 3 * Math.sin(angle));
                        tridents.add(center.getWorld().spawn(spawnLoc, ArmorStand.class, as -> {
                            as.setVisible(false);
                            as.setGravity(false);
                            as.getEquipment().setItemInMainHand(new ItemStack(Material.TRIDENT));
                            as.setRightArmPose(new EulerAngle(Math.toRadians(-90), 0, 0));
                        }));
                    }
                }

                double orbitAngle = (double) ticks * Math.PI / 10;
                for (int i = 0; i < tridents.size(); i++) {
                    double angle = orbitAngle + (Math.PI / 2) * i;
                    Location loc = center.clone().add(3 * Math.cos(angle), 1 + Math.sin(ticks * 0.2),
                            3 * Math.sin(angle));
                    tridents.get(i).teleport(loc);
                    center.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, loc.add(0, 1.5, 0), 2, 0.1, 0.1, 0.1,
                            0.05);
                }

                if (ticks % 10 == 0) {
                    center.getWorld().strikeLightningEffect(
                            center.clone().add(random.nextInt(10) - 5, 0, random.nextInt(10) - 5));
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void playDragonRoar(Player player) {
        Location loc = player.getLocation();
        loc.getWorld().playSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 2.0f, 1.0f);

        new BukkitRunnable() {
            int t = 0;

            @Override
            public void run() {
                if (t > 40) {
                    this.cancel();
                    return;
                }
                double radius = t * 0.2;
                for (double a = 0; a < Math.PI * 2; a += Math.PI / 8) {
                    double x = radius * Math.cos(a);
                    double z = radius * Math.sin(a);
                    loc.getWorld().spawnParticle(Particle.DRAGON_BREATH, loc.clone().add(x, 0.1, z), 1, 0, 0, 0, 0.01);
                }
                t++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void playCosmicVictory(Player player) {
        Location loc = player.getLocation();
        new BukkitRunnable() {
            double y = 0;

            @Override
            public void run() {
                if (y > 5) {
                    this.cancel();
                    return;
                }
                for (int i = 0; i < 2; i++) {
                    double angle = y * Math.PI * 2 + (i * Math.PI);
                    double x = Math.sin(angle) * 1.5;
                    double z = Math.cos(angle) * 1.5;
                    loc.getWorld().spawnParticle(Particle.REVERSE_PORTAL, loc.clone().add(x, y, z), 5, 0, 0, 0, 0);
                }
                y += 0.1;
            }
        }.runTaskTimer(plugin, 0L, 1L);
        spawnFirework(loc);
    }

    private void spawnFirework(Location loc) {
        Firework fw = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder()
                .withColor(Color.PURPLE, Color.AQUA)
                .withFade(Color.WHITE)
                .with(FireworkEffect.Type.STAR)
                .trail(true)
                .build());
        meta.setPower(1);
        fw.setFireworkMeta(meta);
    }
}
