package org.byauth.manager;

import org.byauth.ByCircleGame;
import org.byauth.game.Arena;
import org.byauth.game.Team;
import org.byauth.utils.SettingsManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArenaManager {

    private final ByCircleGame plugin;
    private final SettingsManager settings;
    private final Arena arena;
    private Location center;
    private final List<Location> borderBlockLocations = new ArrayList<>();

    private final int radius;
    private final int arenaY;
    private final int lavaY;

    public ArenaManager(ByCircleGame plugin, Arena arena) {
        this.plugin = plugin;
        this.settings = plugin.getSettingsManager();
        this.arena = arena;
        this.radius = settings.ARENA_RADIUS;
        this.arenaY = settings.ARENA_Y_LEVEL;
        this.lavaY = settings.LAVA_Y_LEVEL;
    }

    public void generateArena(Location startLocation, List<Team> activeTeams) {
        this.center = startLocation.clone();
        this.center.setY(arenaY);
        borderBlockLocations.clear();
        paintArenaFloor(activeTeams);
        generateBarrierWalls();
    }

    private void generateBarrierWalls() {
        if (center == null)
            return;
        World world = center.getWorld();
        int wallRadius = radius + 1;

        for (int x = -wallRadius; x <= wallRadius; x++) {
            for (int z = -wallRadius; z <= wallRadius; z++) {
                double distanceSq = x * x + z * z;
                if (distanceSq >= radius * radius && distanceSq <= wallRadius * wallRadius) {
                    for (int y = lavaY; y < arenaY + 50; y++) {
                        world.getBlockAt(center.getBlockX() + x, y, center.getBlockZ() + z).setType(Material.BARRIER);
                    }
                }
            }
        }
    }

    public void paintArenaFloor(List<Team> activeTeams) {
        if (center == null)
            return;
        World world = center.getWorld();
        int teamCount = Team.values().length;
        List<Team> allTeams = List.of(Team.values());
        double sliceAngle = 2 * Math.PI / teamCount;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double distanceSq = x * x + z * z;
                if (distanceSq > (radius + 0.5) * (radius + 0.5))
                    continue;

                Block floorBlock = world.getBlockAt(center.getBlockX() + x, arenaY - 1, center.getBlockZ() + z);
                double angle = Math.atan2(z, x);
                if (angle < 0)
                    angle += 2 * Math.PI;
                int teamIndex = (int) (angle / sliceAngle);

                if (teamIndex >= 0 && teamIndex < teamCount) {
                    Team sliceTeam = allTeams.get(teamIndex);
                    if (activeTeams.contains(sliceTeam)) {
                        floorBlock.setType(sliceTeam.getConcreteMaterial());
                    } else {
                        floorBlock.setType(Material.WHITE_CONCRETE);
                    }
                }

                Block lavaBlock = world.getBlockAt(center.getBlockX() + x, lavaY, center.getBlockZ() + z);
                lavaBlock.setType(Material.LAVA);
            }
        }
    }

    public void destroyArena() {
        if (center == null)
            return;
        World world = center.getWorld();
        int clearRadius = radius + 5;
        for (int x = -clearRadius; x <= clearRadius; x++) {
            for (int z = -clearRadius; z <= clearRadius; z++) {
                for (int y = lavaY - 1; y < arenaY + 55; y++) {
                    world.getBlockAt(center.getBlockX() + x, y, center.getBlockZ() + z).setType(Material.AIR);
                }
            }
        }
    }

    public void clearEntities() {
        if (center == null)
            return;
        World world = center.getWorld();
        double radiusSq = Math.pow(radius + 10, 2);
        for (Entity entity : world.getEntities()) {
            if (!(entity instanceof Player)) {
                if (entity.getLocation().distanceSquared(center) < radiusSq) {
                    entity.remove();
                }
            }
        }
    }

    public void convertTeamSliceToWhite(Team team) {
        if (center == null)
            return;
        World world = center.getWorld();
        int teamCount = Team.values().length;
        List<Team> allTeams = List.of(Team.values());
        double sliceAngle = 2 * Math.PI / teamCount;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double distanceSq = x * x + z * z;
                if (distanceSq > (radius + 0.5) * (radius + 0.5))
                    continue;

                double angle = Math.atan2(z, x);
                if (angle < 0)
                    angle += 2 * Math.PI;
                int teamIndex = (int) (angle / sliceAngle);

                if (teamIndex >= 0 && teamIndex < teamCount && allTeams.get(teamIndex) == team) {
                    world.getBlockAt(center.getBlockX() + x, arenaY - 1, center.getBlockZ() + z)
                            .setType(Material.WHITE_CONCRETE);
                }
            }
        }
    }

    public Location getCenter() {
        return center;
    }

    public Team getTeamFromLocation(Location location) {
        if (center == null)
            return null;
        int teamCount = Team.values().length;
        List<Team> allTeams = List.of(Team.values());

        int relativeX = location.getBlockX() - center.getBlockX();
        int relativeZ = location.getBlockZ() - center.getBlockZ();

        double angle = Math.atan2(relativeZ, relativeX);
        if (angle < 0)
            angle += 2 * Math.PI;

        double sliceAngle = 2 * Math.PI / teamCount;
        int teamIndex = (int) (angle / sliceAngle);

        if (teamIndex >= 0 && teamIndex < teamCount) {
            return allTeams.get(teamIndex);
        }
        return null;
    }

    public int getLavaY() {
        return lavaY;
    }

    public int getArenaY() {
        return arenaY;
    }
}
