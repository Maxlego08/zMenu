package fr.maxlego08.menu.nms.v1_20_R3;

import fr.maxlego08.menu.nms.NMSHandler;
import fr.maxlego08.menu.nms.NMSVersion;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

@NMSVersion("1.20")
public class NMSHandlerImpl implements NMSHandler {

    private net.minecraft.world.item.ItemStack asNMS(ItemStack itemStack) {
        return CraftItemStack.asNMSCopy(itemStack);
    }

    private ItemStack asBukkit(net.minecraft.world.item.ItemStack nmsItem) {
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    @Override
    public ItemStack setString(ItemStack itemStack, String key, String value) {
        net.minecraft.world.item.ItemStack nmsItem = asNMS(itemStack);
        nmsItem.getOrCreateTag().putString(key, value);
        return asBukkit(nmsItem);
    }

    @Override
    public String getString(ItemStack itemStack, String key) {
        net.minecraft.world.item.ItemStack nmsItem = asNMS(itemStack);
        CompoundTag tag = nmsItem.getTag();
        return (tag != null && tag.contains(key)) ? tag.getString(key) : null;
    }

    @Override
    public ItemStack setInt(ItemStack itemStack, String key, int value) {
        net.minecraft.world.item.ItemStack nmsItem = asNMS(itemStack);
        nmsItem.getOrCreateTag().putInt(key, value);
        return asBukkit(nmsItem);
    }

    @Override
    public int getInt(ItemStack itemStack, String key) {
        net.minecraft.world.item.ItemStack nmsItem = asNMS(itemStack);
        CompoundTag tag = nmsItem.getTag();
        return (tag != null && tag.contains(key)) ? tag.getInt(key) : 0;
    }

    @Override
    public ItemStack setDouble(ItemStack itemStack, String key, double value) {
        net.minecraft.world.item.ItemStack nmsItem = asNMS(itemStack);
        nmsItem.getOrCreateTag().putDouble(key, value);
        return asBukkit(nmsItem);
    }

    @Override
    public double getDouble(ItemStack itemStack, String key) {
        net.minecraft.world.item.ItemStack nmsItem = asNMS(itemStack);
        CompoundTag tag = nmsItem.getTag();
        return (tag != null && tag.contains(key)) ? tag.getDouble(key) : 0;
    }

    @Override
    public ItemStack setLong(ItemStack itemStack, String key, long value) {
        net.minecraft.world.item.ItemStack nmsItem = asNMS(itemStack);
        nmsItem.getOrCreateTag().putLong(key, value);
        return asBukkit(nmsItem);
    }

    @Override
    public long getLong(ItemStack itemStack, String key) {
        net.minecraft.world.item.ItemStack nmsItem = asNMS(itemStack);
        CompoundTag tag = nmsItem.getTag();
        return (tag != null && tag.contains(key)) ? tag.getLong(key) : 0;
    }

    @Override
    public ItemStack setFloat(ItemStack itemStack, String key, float value) {
        net.minecraft.world.item.ItemStack nmsItem = asNMS(itemStack);
        nmsItem.getOrCreateTag().putFloat(key, value);
        return asBukkit(nmsItem);
    }

    @Override
    public float getFloat(ItemStack itemStack, String key) {
        net.minecraft.world.item.ItemStack nmsItem = asNMS(itemStack);
        CompoundTag tag = nmsItem.getTag();
        return (tag != null && tag.contains(key)) ? tag.getFloat(key) : 0;
    }

    @Override
    public ItemStack setBoolean(ItemStack itemStack, String key, boolean value) {
        net.minecraft.world.item.ItemStack nmsItem = asNMS(itemStack);
        nmsItem.getOrCreateTag().putBoolean(key, value);
        return asBukkit(nmsItem);
    }

    @Override
    public boolean getBoolean(ItemStack itemStack, String key) {
        net.minecraft.world.item.ItemStack nmsItem = asNMS(itemStack);
        CompoundTag tag = nmsItem.getTag();
        return (tag != null && tag.contains(key)) && tag.getBoolean(key);
    }

    @Override
    public boolean hasKey(ItemStack itemStack, String key) {
        net.minecraft.world.item.ItemStack nmsItem = asNMS(itemStack);
        CompoundTag tag = nmsItem.getTag();
        return tag != null && tag.contains(key);
    }
}
