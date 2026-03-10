package org.byauth.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum Team {
    RED("Kırmızı", ChatColor.RED, Material.RED_CONCRETE),
    BLUE("Mavi", ChatColor.BLUE, Material.BLUE_CONCRETE),
    GREEN("Yeşil", ChatColor.GREEN, Material.GREEN_CONCRETE),
    YELLOW("Sarı", ChatColor.YELLOW, Material.YELLOW_CONCRETE),
    ORANGE("Turuncu", ChatColor.GOLD, Material.ORANGE_CONCRETE),
    PURPLE("Mor", ChatColor.DARK_PURPLE, Material.PURPLE_CONCRETE),
    CYAN("Turkuaz", ChatColor.AQUA, Material.CYAN_CONCRETE),
    WHITE("Beyaz", ChatColor.WHITE, Material.WHITE_CONCRETE),
    GRAY("Gri", ChatColor.GRAY, Material.GRAY_CONCRETE);

    private final String displayName;
    private final ChatColor chatColor;
    private final Material concreteMaterial;

    Team(String displayName, ChatColor chatColor, Material concreteMaterial) {
        this.displayName = displayName;
        this.chatColor = chatColor;
        this.concreteMaterial = concreteMaterial;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public Material getConcreteMaterial() {
        return concreteMaterial;
    }
}
