package org.byauth.game.event.impl;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;
import org.byauth.EnsDaire;
import org.byauth.game.Arena;
import org.byauth.game.event.GameEvent;

import java.util.Random;

public class MeteorRainEvent implements GameEvent {

    @Override
    public String getName() {
        return "Meteor Yağmuru";
    }

    @Override
    public void trigger(Arena arena) {
        Location center = arena.getCenterLocation();
        Random random = new Random();
        
        new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if (count++ > 10) {
                    this.cancel();
                    return;
                }
                
                double x = center.getX() + (random.nextDouble() * 20 - 10);
                double z = center.getZ() + (random.nextDouble() * 20 - 10);
                Location strike = new Location(center.getWorld(), x, arena.getArenaManager().getArenaY() + 20, z);
                
                center.getWorld().spawnEntity(strike, EntityType.FIREBALL);
            }
        }.runTaskTimer(EnsDaire.getInstance(), 0L, 20L);
    }
}
