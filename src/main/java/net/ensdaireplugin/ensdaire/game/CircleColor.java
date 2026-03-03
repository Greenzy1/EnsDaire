package net.ensdaireplugin.ensdaire.game;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Particle;

public enum CircleColor {
    RED      (DyeColor.RED,        Material.RED_CONCRETE,        "§c", Particle.DUST),
    BLUE     (DyeColor.BLUE,       Material.BLUE_CONCRETE,       "§9", Particle.DUST),
    GREEN    (DyeColor.GREEN,      Material.GREEN_CONCRETE,      "§2", Particle.DUST),
    YELLOW   (DyeColor.YELLOW,     Material.YELLOW_CONCRETE,     "§e", Particle.DUST),
    ORANGE   (DyeColor.ORANGE,     Material.ORANGE_CONCRETE,     "§6", Particle.DUST),
    PURPLE   (DyeColor.PURPLE,     Material.PURPLE_CONCRETE,     "§5", Particle.DUST),
    CYAN     (DyeColor.CYAN,       Material.CYAN_CONCRETE,       "§3", Particle.DUST),
    WHITE    (DyeColor.WHITE,      Material.WHITE_CONCRETE,      "§f", Particle.DUST),
    LIME     (DyeColor.LIME,       Material.LIME_CONCRETE,       "§a", Particle.DUST),
    PINK     (DyeColor.PINK,       Material.PINK_CONCRETE,       "§d", Particle.DUST),
    MAGENTA  (DyeColor.MAGENTA,    Material.MAGENTA_CONCRETE,    "§d", Particle.DUST),
    LIGHT_BLUE(DyeColor.LIGHT_BLUE,Material.LIGHT_BLUE_CONCRETE, "§b", Particle.DUST),
    BROWN    (DyeColor.BROWN,      Material.BROWN_CONCRETE,      "§8", Particle.DUST),
    GRAY     (DyeColor.GRAY,       Material.GRAY_CONCRETE,       "§8", Particle.DUST),
    LIGHT_GRAY(DyeColor.LIGHT_GRAY,Material.LIGHT_GRAY_CONCRETE, "§7", Particle.DUST),
    BLACK    (DyeColor.BLACK,      Material.BLACK_CONCRETE,      "§0", Particle.DUST);

    private final DyeColor dyeColor;
    private final Material material;
    private final String chatColor;
    private final Particle particle;

    CircleColor(DyeColor dyeColor, Material material, String chatColor, Particle particle) {
        this.dyeColor = dyeColor;
        this.material = material;
        this.chatColor = chatColor;
        this.particle = particle;
    }

    public DyeColor getDyeColor() { return dyeColor; }
    public Material getMaterial() { return material; }
    public String getChatColor() { return chatColor; }
    public Particle getParticle() { return particle; }

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
