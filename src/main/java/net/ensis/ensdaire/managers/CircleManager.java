package net.ensis.ensdaire.managers;

import net.ensis.ensdaire.EnsDaire;
import net.ensis.ensdaire.models.CircleColor;
import net.ensis.ensdaire.models.GamePlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class CircleManager {

    private final EnsDaire plugin;
    private final Set<Material> safeMaterials = new HashSet<>();
    private final Map<UUID, CircleColor> activeCircles = new HashMap<>();
    private BukkitTask particleTask;

    public CircleManager(EnsDaire plugin) {
        this.plugin = plugin;
        loadSafeBlocks();
    }

    public void loadSafeBlocks() {
        safeMaterials.clear();
        List<String> list = plugin.getConfig().getStringList("territory.safe-blocks");
        for (String s : list) {
            try {
                safeMaterials.add(Material.valueOf(s.toUpperCase()));
            } catch (Exception ignored) {
            }
        }
    }

    public boolean isSafe(UUID uuid, Location loc, CircleColor playerColor) {
        Block block = loc.getBlock().getRelative(BlockFace.DOWN);
        Material mat = block.getType();

        if (mat == Material.AIR) {
            mat = block.getRelative(BlockFace.DOWN).getType();
        }

        if (safeMaterials.contains(mat))
            return true;
        if (mat == playerColor.getMaterial())
            return true;

        for (CircleColor color : CircleColor.values()) {
            if (mat == color.getMaterial())
                return false;
        }

        return false;
    }

    public void createCircle(UUID uuid, Location loc, CircleColor color) {
        activeCircles.put(uuid, color);
    }

    public void removeCircle(UUID uuid) {
        activeCircles.remove(uuid);
    }

    public void removeAllCircles() {
        activeCircles.clear();
    }

    public void updateCircleColor(UUID uuid, CircleColor color) {
        if (activeCircles.containsKey(uuid)) {
            activeCircles.put(uuid, color);
        }
    }

    public void startParticleEffects(Map<UUID, GamePlayer> players) {
        if (particleTask != null)
            particleTask.cancel();
        particleTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, GamePlayer> entry : players.entrySet()) {
                    GamePlayer gp = entry.getValue();
                    if (!gp.isAlive())
                        continue;
                    Player p = org.bukkit.Bukkit.getPlayer(entry.getKey());
                    if (p == null || !p.isOnline())
                        continue;
                    CircleColor color = activeCircles.get(entry.getKey());
                    if (color == null)
                        continue;
                    spawnColorRing(p.getLocation().add(0, 0.1, 0), color);
                }
            }
        }.runTaskTimer(plugin, 0L, 4L);
    }

    private void spawnColorRing(Location center, CircleColor color) {
        int points = 12;
        double radius = 0.6;
        Particle.DustOptions dust = new Particle.DustOptions(
                org.bukkit.Color.fromRGB(
                        color.getColor().getRed(),
                        color.getColor().getGreen(),
                        color.getColor().getBlue()),
                1.2f);
        for (int i = 0; i < points; i++) {
            double angle = (2 * Math.PI / points) * i;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            center.getWorld().spawnParticle(Particle.REDSTONE, center.clone().add(x, 0, z), 1, dust);
        }
    }

    public void stopParticleEffects() {
        if (particleTask != null) {
            particleTask.cancel();
            particleTask = null;
        }
    }
}
