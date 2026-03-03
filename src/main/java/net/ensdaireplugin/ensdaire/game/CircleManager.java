package net.ensdaireplugin.ensdaire.game;

import net.ensdaireplugin.ensdaire.EnsDaire;
import net.ensdaireplugin.ensdaire.utils.VersionUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class CircleManager {
    private final EnsDaire plugin;
    private final int radius;
    private final String materialType;
    private final Map<UUID, List<BlockRecord>> circleMap = new HashMap<>();
    private BukkitTask particleTask;

    public CircleManager(EnsDaire plugin) {
        this.plugin = plugin;
        this.radius = plugin.getConfig().getInt("circle.radius", 4);
        this.materialType = plugin.getConfig().getString("circle.material", "CONCRETE");
    }

    public void createCircle(UUID uuid, Location center, CircleColor color) {
        removeCircle(uuid);
        List<BlockRecord> records = new ArrayList<>();
        Material mat = resolveMaterial(color);
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z <= radius * radius) {
                    Location blockLoc = findGround(center.clone().add(x, 0, z));
                    if (blockLoc == null) continue;
                    Block block = blockLoc.getBlock();
                    Material original = block.getType();
                    block.setType(mat);
                    records.add(new BlockRecord(blockLoc.clone(), original));
                }
            }
        }
        circleMap.put(uuid, records);
    }

    private Material resolveMaterial(CircleColor color) {
        return switch (materialType.toUpperCase()) {
            case "WOOL" -> Material.valueOf(color.getDyeColor().name() + "_WOOL");
            case "TERRACOTTA" -> Material.valueOf(color.getDyeColor().name() + "_TERRACOTTA");
            default -> color.getMaterial();
        };
    }

    private Location findGround(Location loc) {
        Location check = loc.clone();
        for (int i = 0; i < 8; i++) {
            Material type = check.getBlock().getType();
            if (!type.isAir() && type != Material.WATER && type != Material.LAVA) return check;
            check.subtract(0, 1, 0);
        }
        check = loc.clone();
        for (int i = 0; i < 8; i++) {
            if (!check.getBlock().getType().isAir()) return check;
            check.add(0, 1, 0);
        }
        return loc;
    }

    public boolean isInsideCircle(UUID uuid, Location playerLoc) {
        List<BlockRecord> records = circleMap.get(uuid);
        if (records == null || records.isEmpty()) return true;
        double sumX = 0, sumZ = 0;
        for (BlockRecord r : records) {
            sumX += r.location.getX();
            sumZ += r.location.getZ();
        }
        double centerX = sumX / records.size();
        double centerZ = sumZ / records.size();
        double dx = playerLoc.getX() - centerX;
        double dz = playerLoc.getZ() - centerZ;
        return (dx * dx + dz * dz) <= ((radius + 0.5) * (radius + 0.5));
    }

    public void removeCircle(UUID uuid) {
        List<BlockRecord> records = circleMap.remove(uuid);
        if (records == null) return;
        for (BlockRecord r : records) {
            try {
                if (r.location.getWorld() != null) r.location.getBlock().setType(r.original);
            } catch (Exception ignored) {}
        }
    }

    public void updateCircleColor(UUID uuid, CircleColor color) {
        List<BlockRecord> records = circleMap.get(uuid);
        if (records == null) return;
        Material mat = resolveMaterial(color);
        for (BlockRecord r : records) r.location.getBlock().setType(mat);
    }

    public void removeAllCircles() {
        new ArrayList<>(circleMap.keySet()).forEach(this::removeCircle);
        circleMap.clear();
    }

    public void startParticleEffects(Map<UUID, GamePlayer> players) {
        stopParticleEffects();
        if (!plugin.getConfig().getBoolean("effects.circle-particles", true)) return;
        particleTask = new BukkitRunnable() {
            int tick = 0;
            @Override
            public void run() {
                tick++;
                for (Map.Entry<UUID, GamePlayer> entry : players.entrySet()) {
                    GamePlayer gp = entry.getValue();
                    if (!gp.isAlive() || gp.getColor() == null || gp.getCapsuleLocation() == null) continue;
                    if (tick % 5 != 0) continue;
                    Location center = gp.getCapsuleLocation();
                    double r = radius + 0.6;
                    CircleColor color = gp.getColor();
                    for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / 12) {
                        double px = center.getX() + r * Math.cos(angle);
                        double pz = center.getZ() + r * Math.sin(angle);
                        Location pLoc = new Location(center.getWorld(), px, center.getY() + 0.1, pz);
                        Color dustColor = getDustColor(color);
                        Particle.DustOptions dust = new Particle.DustOptions(dustColor, 1.0f);
                        try {
                            center.getWorld().spawnParticle(VersionUtils.getDustParticle(), pLoc, 1, 0, 0, 0, 0, dust);
                        } catch (Exception ignored) {}
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void stopParticleEffects() {
        if (particleTask != null) {
            particleTask.cancel();
            particleTask = null;
        }
    }

    private Color getDustColor(CircleColor color) {
        return switch (color) {
            case RED -> Color.RED;
            case BLUE -> Color.BLUE;
            case GREEN -> Color.GREEN;
            case YELLOW -> Color.YELLOW;
            case ORANGE -> Color.ORANGE;
            case PURPLE, MAGENTA -> Color.PURPLE;
            case CYAN, LIGHT_BLUE -> Color.AQUA;
            case WHITE, LIGHT_GRAY -> Color.WHITE;
            case LIME -> Color.LIME;
            case PINK -> Color.fromRGB(255, 105, 180);
            case BROWN -> Color.fromRGB(139, 69, 19);
            case GRAY -> Color.GRAY;
            case BLACK -> Color.BLACK;
            default -> Color.WHITE;
        };
    }

    public record BlockRecord(Location location, Material original) {}
}
