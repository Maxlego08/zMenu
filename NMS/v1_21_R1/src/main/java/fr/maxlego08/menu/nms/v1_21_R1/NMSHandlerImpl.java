package fr.maxlego08.menu.nms.v1_21_R1;

import fr.maxlego08.menu.nms.NMSHandler;
import fr.maxlego08.menu.nms.NMSVersion;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

@NMSVersion("1.21")
public class NMSHandlerImpl implements NMSHandler {

    private net.minecraft.world.item.ItemStack asNMS(ItemStack itemStack) {
        return CraftItemStack.asNMSCopy(itemStack);
    }

    private ItemStack asBukkit(net.minecraft.world.item.ItemStack nmsItem) {
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    private CompoundTag getTag(net.minecraft.world.item.ItemStack nmsItem) {
        CustomData customData = nmsItem.get(DataComponents.CUSTOM_DATA);
        return customData != null ? customData.copyTag() : new CompoundTag();
    }

    private void setTag(net.minecraft.world.item.ItemStack nmsItem, CompoundTag tag) {
        nmsItem.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Override
    public ItemStack setString(ItemStack itemStack, String key, String value) {
        net.minecraft.world.item.ItemStack nmsItem = asNMS(itemStack);
        CompoundTag tag = getTag(nmsItem);
        tag.putString(key, value);
        setTag(nmsItem, tag);
        return asBukkit(nmsItem);
    }

    @Override
    public String getString(ItemStack itemStack, String key) {
        net.minecraft.world.item.ItemStack nmsItem = asNMS(itemStack);
        CustomData customData = nmsItem.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return null;
        CompoundTag tag = customData.copyTag();
        return tag.contains(key) ? tag.getString(key) : null;
    }

    @Override
    public ItemStack setInt(ItemStack itemStack, String key, int value) {
        net.minecraft.world.item.ItemStack nmsItem = asNMS(itemStack);
        CompoundTag tag = getTag(nmsItem);
        tag.putInt(key, value);
        setTag(nmsItem, tag);
        return asBukkit(nmsItem);
    }

    @Override
    public int getInt(ItemStack itemStack, String key) {
        net.minecraft.world.item.ItemStack nmsItem = asNMS(itemStack);
        CustomData customData = nmsItem.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return 0;
        CompoundTag tag = customData.copyTag();
        return tag.getInt(key);
    }

    @Override
    public ItemStack setDouble(ItemStack itemStack, String key, double value) {
        net.minecraft.world.item.ItemStack nmsItem = asNMS(itemStack);
        CompoundTag tag = getTag(nmsItem);
        tag.putDouble(key, value);
        setTag(nmsItem, tag);
        return asBukkit(nmsItem);
    }

    @Override
    public double getDouble(ItemStack itemStack, String key) {
        net.minecraft.world.item.ItemStack nmsItem = asNMS(itemStack);
        CustomData customData = nmsItem.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return 0;
        CompoundTag tag = customData.copyTag();
        return tag.getDouble(key);
    }

    @Override
    public ItemStack setLong(ItemStack itemStack, String key, long value) {
        net.minecraft.world.item.ItemStack nmsItem = asNMS(itemStack);
        CompoundTag tag = getTag(nmsItem);
        tag.putLong(key, value);
        setTag(nmsItem, tag);
        return asBukkit(nmsItem);
    }

    @Override
    public long getLong(ItemStack itemStack, String key) {
        net.minecraft.world.item.ItemStack nmsItem = asNMS(itemStack);
        CustomData customData = nmsItem.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return 0;
        CompoundTag tag = customData.copyTag();
        return tag.getLong(key);
    }

    @Override
    public ItemStack setFloat(ItemStack itemStack, String key, float value) {
        net.minecraft.world.item.ItemStack nmsItem = asNMS(itemStack);
        CompoundTag tag = getTag(nmsItem);
        tag.putFloat(key, value);
        setTag(nmsItem, tag);
        return asBukkit(nmsItem);
    }

    @Override
    public float getFloat(ItemStack itemStack, String key) {
        net.minecraft.world.item.ItemStack nmsItem = asNMS(itemStack);
        CustomData customData = nmsItem.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return 0;
        CompoundTag tag = customData.copyTag();
        return tag.getFloat(key);
    }

    @Override
    public ItemStack setBoolean(ItemStack itemStack, String key, boolean value) {
        net.minecraft.world.item.ItemStack nmsItem = asNMS(itemStack);
        CompoundTag tag = getTag(nmsItem);
        tag.putBoolean(key, value);
        setTag(nmsItem, tag);
        return asBukkit(nmsItem);
    }

    @Override
    public boolean getBoolean(ItemStack itemStack, String key) {
        net.minecraft.world.item.ItemStack nmsItem = asNMS(itemStack);
        CustomData customData = nmsItem.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return false;
        CompoundTag tag = customData.copyTag();
        return tag.getBoolean(key);
    }

    @Override
    public boolean hasKey(ItemStack itemStack, String key) {
        net.minecraft.world.item.ItemStack nmsItem = asNMS(itemStack);
        CustomData customData = nmsItem.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return false;
        CompoundTag tag = customData.copyTag();
        return tag.contains(key);
    }
}
