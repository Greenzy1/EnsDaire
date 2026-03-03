package net.ensdaireplugin.ensdaire.utils;

import net.ensdaireplugin.ensdaire.EnsDaire;
import org.bukkit.ChatColor;

public class Msg {
    public static String get(EnsDaire plugin, String key, String... replace) {
        String msg = plugin.getConfig().getString(key, "§c[Missing: " + key + "]");
        for (int i = 0; i + 1 < replace.length; i += 2) {
            msg = msg.replace(replace[i], replace[i + 1]);
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String colorize(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
