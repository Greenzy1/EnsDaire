package net.ensis.ensdaire.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Base64Utils {

    public static String encode(ItemStack item) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataStream = new BukkitObjectOutputStream(outputStream);
            dataStream.writeObject(item);
            dataStream.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ItemStack decode(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataStream = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack) dataStream.readObject();
            dataStream.close();
            return item;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
