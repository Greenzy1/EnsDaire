package net.ensdaireplugin.ensdaire.game;

import net.ensdaireplugin.ensdaire.utils.VersionUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Particle;

public enum CircleColor {
    RED      (DyeColor.RED,        Material.RED_CONCRETE,        "§c"),
    BLUE     (DyeColor.BLUE,       Material.BLUE_CONCRETE,       "§9"),
    GREEN    (DyeColor.GREEN,      Material.GREEN_CONCRETE,      "§2"),
    YELLOW   (DyeColor.YELLOW,     Material.YELLOW_CONCRETE,     "§e"),
    ORANGE   (DyeColor.ORANGE,     Material.ORANGE_CONCRETE,     "§6"),
    PURPLE   (DyeColor.PURPLE,     Material.PURPLE_CONCRETE,     "§5"),
    CYAN     (DyeColor.CYAN,       Material.CYAN_CONCRETE,       "§3"),
    WHITE    (DyeColor.WHITE,      Material.WHITE_CONCRETE,      "§f"),
    LIME     (DyeColor.LIME,       Material.LIME_CONCRETE,       "§a"),
    PINK     (DyeColor.PINK,       Material.PINK_CONCRETE,       "§d"),
    MAGENTA  (DyeColor.MAGENTA,    Material.MAGENTA_CONCRETE,    "§d"),
    LIGHT_BLUE(DyeColor.LIGHT_BLUE,Material.LIGHT_BLUE_CONCRETE, "§b"),
    BROWN    (DyeColor.BROWN,      Material.BROWN_CONCRETE,      "§8"),
    GRAY     (DyeColor.GRAY,       Material.GRAY_CONCRETE,       "§8"),
    LIGHT_GRAY(DyeColor.LIGHT_GRAY,Material.LIGHT_GRAY_CONCRETE, "§7"),
    BLACK    (DyeColor.BLACK,      Material.BLACK_CONCRETE,      "§0");

    private final DyeColor dyeColor;
    private final Material material;
    private final String chatColor;

    CircleColor(DyeColor dyeColor, Material material, String chatColor) {
        this.dyeColor = dyeColor;
        this.material = material;
        this.chatColor = chatColor;
    }

    public DyeColor getDyeColor() { return dyeColor; }
    public Material getMaterial() { return material; }
    public String getChatColor() { return chatColor; }
    public Particle getParticle() { return VersionUtils.getDustParticle(); }

    public String getDisplayName() {
        return chatColor + name();
    }

    public static CircleColor[] getShuffled() {
        CircleColor[] values = values();
        java.util.List<CircleColor> list = new java.util.ArrayList<>(java.util.Arrays.asList(values));
        java.util.Collections.shuffle(list);
        return list.toArray(new CircleColor[0]);
    }
}
