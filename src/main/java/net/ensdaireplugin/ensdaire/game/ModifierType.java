package net.ensdaireplugin.ensdaire.game;

import org.bukkit.potion.PotionEffectType;

public enum ModifierType {
    NONE("Normal", ""),
    LOW_GRAVITY("Düşük Yerçekimi", "modifier-desc-low-gravity"),
    SPEED("Hız Patlaması", "modifier-desc-speed"),
    DOUBLE_TOKEN("Çift Jeton", "modifier-desc-double-token");

    private final String displayName;
    private final String descriptionKey;

    ModifierType(String displayName, String descriptionKey) {
        this.displayName = displayName;
        this.descriptionKey = descriptionKey;
    }

    public String getDisplayName() { return displayName; }
    public String getDescriptionKey() { return descriptionKey; }
}
