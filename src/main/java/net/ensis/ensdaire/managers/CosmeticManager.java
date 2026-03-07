package net.ensis.ensdaire.managers;

import net.ensis.ensdaire.EnsDaire;
import net.ensis.ensdaire.models.CosmeticType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CosmeticManager {

    private final EnsDaire plugin;
    private final Map<UUID, Set<CosmeticType>> ownedCosmetics = new ConcurrentHashMap<>();
    private final Map<UUID, CosmeticType> activeTrail = new ConcurrentHashMap<>();
    private final Map<UUID, CosmeticType> activeProjectile = new ConcurrentHashMap<>();
    private final Map<UUID, CosmeticType> activeKillEffect = new ConcurrentHashMap<>();
    private final Map<UUID, CosmeticType> activeWinEffect = new ConcurrentHashMap<>();

    public CosmeticManager(EnsDaire plugin) {
        this.plugin = plugin;
        startEffectsTask();
    }

    public void addCosmetic(UUID uuid, CosmeticType type) {
        ownedCosmetics.computeIfAbsent(uuid, k -> Collections.newSetFromMap(new ConcurrentHashMap<>())).add(type);
    }

    public boolean hasCosmetic(UUID uuid, CosmeticType type) {
        return ownedCosmetics.containsKey(uuid) && ownedCosmetics.get(uuid).contains(type);
    }

    public void equip(Player player, CosmeticType type) {
        UUID uuid = player.getUniqueId();
        switch (type.getCategory()) {
            case TRAIL -> activeTrail.put(uuid, type);
            case PROJECTILE -> activeProjectile.put(uuid, type);
            case KILL -> activeKillEffect.put(uuid, type);
            case WIN -> activeWinEffect.put(uuid, type);
        }
        player.sendMessage(plugin.getLanguageManager().getMessage("prefix") + "§aKozmetik başarıyla kuşanıldı: §e"
                + type.getName());
    }

    public void playKillEffect(Player victim, Player killer) {
        if (killer == null)
            return;
        CosmeticType effect = activeKillEffect.get(killer.getUniqueId());
        if (effect == null)
            return;

        Location loc = victim.getLocation().add(0, 1, 0);
        switch (effect) {
            case KILL_EXPLOSION -> {
                loc.getWorld().spawnParticle(net.ensis.ensdaire.utils.VersionUtils.getExplosionParticle(), loc, 1);
                loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
            }
            case KILL_LIGHTNING -> loc.getWorld().strikeLightningEffect(loc);
            case KILL_BLOOD -> loc.getWorld().spawnParticle(Particle.BLOCK_CRACK, loc, 50, 0.3, 0.5, 0.3,
                    org.bukkit.Material.REDSTONE_BLOCK.createBlockData());
            case KILL_GHOST -> {
                loc.getWorld().spawnParticle(Particle.CLOUD, loc, 30, 0.2, 1.0, 0.2, 0.05);
                loc.getWorld().playSound(loc, Sound.ENTITY_GHAST_AMBIENT, 1f, 1.5f);
            }
            default -> {
            }
        }
    }

    public void handleProjectile(Projectile projectile, Player shooter) {
        CosmeticType effect = activeProjectile.get(shooter.getUniqueId());
        if (effect == null)
            return;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (projectile.isDead() || !projectile.isValid() || projectile.isOnGround()) {
                    cancel();
                    return;
                }
                projectile.getWorld().spawnParticle(effect.getParticle(), projectile.getLocation(), 2, 0.05, 0.05, 0.05,
                        0.01);
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void startEffectsTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, CosmeticType> entry : activeTrail.entrySet()) {
                    Player p = Bukkit.getPlayer(entry.getKey());
                    if (p != null && p.isOnline() && !p.isDead()) {
                        if (p.getVelocity().lengthSquared() > 0.001) {
                            p.getWorld().spawnParticle(entry.getValue().getParticle(), p.getLocation().add(0, 0.1, 0),
                                    3, 0.1, 0, 0.1, 0.02);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    public CosmeticType getActiveWinEffect(UUID uuid) {
        return activeWinEffect.get(uuid);
    }
}
