package fr.maxlego08.menu.nms;

import org.bukkit.inventory.ItemStack;

public interface NMSHandler {

    ItemStack setString(ItemStack itemStack, String key, String value);

    String getString(ItemStack itemStack, String key);

    ItemStack setInt(ItemStack itemStack, String key, int value);

    int getInt(ItemStack itemStack, String key);

    ItemStack setDouble(ItemStack itemStack, String key, double value);

    double getDouble(ItemStack itemStack, String key);

    ItemStack setLong(ItemStack itemStack, String key, long value);

    long getLong(ItemStack itemStack, String key);

    ItemStack setFloat(ItemStack itemStack, String key, float value);

    float getFloat(ItemStack itemStack, String key);

    ItemStack setBoolean(ItemStack itemStack, String key, boolean value);

    boolean getBoolean(ItemStack itemStack, String key);

    boolean hasKey(ItemStack itemStack, String key);

}
