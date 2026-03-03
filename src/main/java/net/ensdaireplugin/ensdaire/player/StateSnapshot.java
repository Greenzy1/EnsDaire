package net.ensdaireplugin.ensdaire.player;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public class StateSnapshot {

    private final ItemStack[] contents;
    private final ItemStack[] armor;
    private final float exp;
    private final int level;
    private final double health;
    private final int food;
    private final GameMode mode;
    private final Collection<PotionEffect> effects;

    public StateSnapshot(Player player) {
        this.contents = player.getInventory().getContents().clone();
        this.armor    = player.getInventory().getArmorContents().clone();
        this.exp      = player.getExp();
        this.level    = player.getLevel();
        this.health   = player.getHealth();
        this.food     = player.getFoodLevel();
        this.mode     = player.getGameMode();
        this.effects  = player.getActivePotionEffects();
    }

    public void restore(Player player) {
        player.getInventory().setContents(contents);
        player.getInventory().setArmorContents(armor);
        player.setExp(exp);
        player.setLevel(level);
        player.setHealth(Math.min(health, player.getMaxHealth()));
        player.setFoodLevel(food);
        player.setGameMode(mode);
        
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        for (PotionEffect effect : effects) {
            player.addPotionEffect(effect);
        }
    }
}
