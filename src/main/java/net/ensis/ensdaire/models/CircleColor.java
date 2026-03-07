package net.ensis.ensdaire.models;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;

public enum CircleColor {
    RED(ChatColor.RED, Color.RED, Material.RED_CONCRETE),
    BLUE(ChatColor.BLUE, Color.BLUE, Material.BLUE_CONCRETE),
    GREEN(ChatColor.GREEN, Color.GREEN, Material.GREEN_CONCRETE),
    YELLOW(ChatColor.YELLOW, Color.YELLOW, Material.YELLOW_CONCRETE),
    PURPLE(ChatColor.LIGHT_PURPLE, Color.PURPLE, Material.PURPLE_CONCRETE),
    AQUA(ChatColor.AQUA, Color.AQUA, Material.CYAN_CONCRETE),
    ORANGE(ChatColor.GOLD, Color.ORANGE, Material.ORANGE_CONCRETE),
    BLACK(ChatColor.BLACK, Color.BLACK, Material.BLACK_CONCRETE),
    WHITE(ChatColor.WHITE, Color.WHITE, Material.WHITE_CONCRETE);

    private final ChatColor chatColor;
    private final Color color;
    private Material material;

    CircleColor(ChatColor chatColor, Color color, Material material) {
        this.chatColor = chatColor;
        this.color = color;
        this.material = material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public ChatColor getChatColor() { return chatColor; }
    public Color getColor() { return color; }
    public Material getMaterial() { return material; }
    public String getDisplayName() { return chatColor + name(); }
}
