package net.ensis.ensdaire.utils;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffectType;

public class VersionUtils {

    private static final String VERSION = Bukkit.getServer().getBukkitVersion();

    public static boolean isLegacy() {
        return VERSION.contains("1.20");
    }

    public static PotionEffectType getJumpBoost() {
        try {
            return PotionEffectType.getByName("JUMP_BOOST") != null ? 
                   PotionEffectType.getByName("JUMP_BOOST") : PotionEffectType.getByName("JUMP");
        } catch (Exception e) {
            return PotionEffectType.getByName("JUMP");
        }
    }

    public static PotionEffectType getResistance() {
        try {
            return PotionEffectType.getByName("RESISTANCE") != null ? 
                   PotionEffectType.getByName("RESISTANCE") : PotionEffectType.getByName("DAMAGE_RESISTANCE");
        } catch (Exception e) {
            return PotionEffectType.getByName("DAMAGE_RESISTANCE");
        }
    }

    public static Particle getDustParticle() {
        try {
            return Particle.valueOf("DUST");
        } catch (Exception e) {
            return Particle.valueOf("REDSTONE");
        }
    }

    public static Particle getExplosionParticle() {
        try {
            return Particle.valueOf("EXPLOSION_EMITTER");
        } catch (Exception e) {
            return Particle.valueOf("EXPLOSION_HUGE");
        }
    }
}
