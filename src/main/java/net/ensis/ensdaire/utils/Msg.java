package net.ensis.ensdaire.utils;

import net.ensis.ensdaire.EnsDaire;

public class Msg {
    public static String get(EnsDaire plugin, String key, String... placeholders) {
        String msg = plugin.getLanguageManager().getMessage(key);
        if (placeholders.length >= 2) {
            for (int i = 0; i < placeholders.length; i += 2) {
                msg = msg.replace(placeholders[i], placeholders[i + 1]);
            }
        }
        return msg;
    }
}
