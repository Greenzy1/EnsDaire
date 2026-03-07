package net.ensis.ensdaire.utils;

import net.ensis.ensdaire.EnsDaire;
import org.bukkit.ChatColor;

public class Logger {
    public static void info(String msg) {
        EnsDaire.getInstance().getLogger().info(ChatColor.translateAlternateColorCodes('&', msg));
    }
    public static void warning(String msg) {
        EnsDaire.getInstance().getLogger().warning(ChatColor.translateAlternateColorCodes('&', msg));
    }
    public static void severe(String msg) {
        EnsDaire.getInstance().getLogger().severe(ChatColor.translateAlternateColorCodes('&', msg));
    }
}
