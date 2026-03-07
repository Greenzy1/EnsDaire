package net.ensis.ensdaire.models;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collection;

public class StateSnapshot {
    private final Location location;
    private final GameMode gameMode;
    private final ItemStack[] inventory;
    private final ItemStack[] armor;
    private final Collection<PotionEffect> effects;
    private final double health;
    private final int food;
    private final int level;
    private final float exp;

    public StateSnapshot(Player p) {
        this.location = p.getLocation();
        this.gameMode = p.getGameMode();
        this.inventory = p.getInventory().getContents().clone();
        this.armor = p.getInventory().getArmorContents().clone();
        this.effects = new ArrayList<>(p.getActivePotionEffects());
        this.health = p.getHealth();
        this.food = p.getFoodLevel();
        this.level = p.getLevel();
        this.exp = p.getExp();
    }

    public void restore(Player p) {
        p.teleport(location);
        p.setGameMode(gameMode);
        p.getInventory().setContents(inventory);
        p.getInventory().setArmorContents(armor);
        for (PotionEffect e : p.getActivePotionEffects()) p.removePotionEffect(e.getType());
        p.addPotionEffects(effects);
        p.setHealth(health);
        p.setFoodLevel(food);
        p.setLevel(level);
        p.setExp(exp);
    }
}
