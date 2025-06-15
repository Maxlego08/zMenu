package fr.maxlego08.menu.zcore.utils.nms;

import fr.maxlego08.menu.zcore.utils.nms.ItemStackUtils.EnumReflectionItemStack;
import org.bukkit.inventory.ItemStack;

public class ItemStackCompound {

    /**
     * Singleton instance of ItemStackCompound based on the current NmsVersion.
     */
    public static ItemStackCompound itemStackCompound;

    // Static block to initialize the itemStackCompound based on the NmsVersion
    static {
        NmsVersion nmsVersion = NmsVersion.nmsVersion;
        if (nmsVersion == NmsVersion.V_1_18_2) {
            itemStackCompound = new ItemStackCompound(EnumReflectionCompound.V1_18_2);
        } else if (nmsVersion.getVersion() >= 1200) {
            itemStackCompound = new ItemStackCompound(EnumReflectionCompound.V1_12);
        } else if (nmsVersion.getVersion() >= 1190) {
            itemStackCompound = new ItemStackCompound(EnumReflectionCompound.V1_19);
        } else if (nmsVersion.getVersion() >= 1170) {
            itemStackCompound = new ItemStackCompound(EnumReflectionCompound.V1_17);
        } else itemStackCompound = new ItemStackCompound(EnumReflectionCompound.V1_8_8);
    }

    private final EnumReflectionCompound reflection;

    /**
     * Constructs an ItemStackCompound instance based on the given EnumReflectionCompound.
     *
     * @param reflection The EnumReflectionCompound representing the NBT tag reflection version.
     */
    public ItemStackCompound(EnumReflectionCompound reflection) {
        super();
        this.reflection = reflection;
    }

    /**
     * Retrieves the NBT tag compound from the given ItemStack.
     *
     * @param itemStack The ItemStack to retrieve the NBT tag from.
     * @return The NBT tag compound object.
     * @throws Exception If an error occurs during the process.
     */
    public Object getCompound(ItemStack itemStack) throws Exception {
        if (itemStack == null) return null;
        Object localItemStackObject = EnumReflectionItemStack.CRAFTITEMSTACK.getClassz().getMethod("asNMSCopy", ItemStack.class).invoke(null, itemStack);
        Object localCompoundObject = localItemStackObject.getClass().getMethod(this.reflection.getMethodGetTag()).invoke(localItemStackObject);
        if (localCompoundObject != null) {
            return localCompoundObject;
        }
        return EnumReflectionItemStack.NBTTAGCOMPOUND.getClassz().newInstance();
    }


    /**
     * Applies the given NBT tag compound to the ItemStack.
     *
     * @param itemStack      The ItemStack to apply the NBT tag to.
     * @param compoundObject The NBT tag compound to apply.
     * @return The modified ItemStack.
     * @throws Exception If an error occurs during the process.
     */
    public ItemStack applyCompound(ItemStack itemStack, Object compoundObject) throws Exception {
        Object localItemStackObject = EnumReflectionItemStack.CRAFTITEMSTACK.getClassz().getMethod("asNMSCopy", ItemStack.class).invoke(null, itemStack);
        localItemStackObject.getClass().getMethod(this.reflection.getMethodSetTag(), EnumReflectionItemStack.NBTTAGCOMPOUND.getClassz()).invoke(localItemStackObject, compoundObject);
        return (ItemStack) EnumReflectionItemStack.CRAFTITEMSTACK.getClassz().getMethod("asBukkitCopy", EnumReflectionItemStack.ITEMSTACK.getClassz()).invoke(null, new Object[]{localItemStackObject});
    }

    /**
     * Sets a string value in the NBT tag compound.
     *
     * @param itemStack The ItemStack to set the value in.
     * @param key       The key of the value.
     * @param value     The string value to set.
     * @return The modified ItemStack.
     */
    public ItemStack setString(ItemStack itemStack, String key, String value) {
        try {
            Object compoundObject = this.getCompound(itemStack);
            if (compoundObject == null) return null;
            compoundObject.getClass().getMethod(this.reflection.getMethodSetString(), String.class, String.class).invoke(compoundObject, key, value);
            return this.applyCompound(itemStack, compoundObject);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;

    }

    /**
     * Retrieves a string value from the NBT tag compound.
     *
     * @param itemStack The ItemStack to retrieve the value from.
     * @param key       The key of the value.
     * @return The string value.
     */
    public String getString(ItemStack itemStack, String key) {
        try {
            Object compoundObject = this.getCompound(itemStack);
            if (compoundObject == null) return null;
            return (String) compoundObject.getClass().getMethod(this.reflection.getMethodGetString(), String.class).invoke(compoundObject, new Object[]{key});
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;

    }


    /**
     * Retrieves a double value from the NBT tag compound.
     *
     * @param itemStack The ItemStack to retrieve the value from.
     * @param key       The key of the value.
     * @return The double value.
     */
    public double getDouble(ItemStack itemStack, String key) {
        try {
            Object compoundObject = this.getCompound(itemStack);
            if (compoundObject == null) return 0;
            return (double) compoundObject.getClass().getMethod(this.reflection.getMethodGetDouble(), String.class).invoke(compoundObject, new Object[]{key});
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;

    }

    /**
     * Retrieves a long value from the NBT tag compound.
     *
     * @param itemStack The ItemStack to retrieve the value from.
     * @param key       The key of the value.
     * @return The long value.
     */
    public long getLong(ItemStack itemStack, String key) {
        try {
            Object compoundObject = this.getCompound(itemStack);
            if (compoundObject == null) return 0;
            return (long) compoundObject.getClass().getMethod(this.reflection.getMethodGetLong(), String.class).invoke(compoundObject, new Object[]{key});
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    /**
     * Retrieves an integer value from the NBT tag compound.
     *
     * @param itemStack The ItemStack to retrieve the value from.
     * @param key       The key of the value.
     * @return The integer value.
     */
    public int getInt(ItemStack itemStack, String key) {
        try {
            Object compoundObject = this.getCompound(itemStack);
            if (compoundObject == null) return 0;
            return (int) compoundObject.getClass().getMethod(this.reflection.getMethodGetInt(), String.class).invoke(compoundObject, new Object[]{key});
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;

    }

    /**
     * Retrieves a float value from the NBT tag compound.
     *
     * @param itemStack The ItemStack to retrieve the value from.
     * @param key       The key of the value.
     * @return The float value.
     */
    public float getFloat(ItemStack itemStack, String key) {
        try {
            Object compoundObject = this.getCompound(itemStack);
            return (float) compoundObject.getClass().getMethod(this.reflection.getMethodGetFloat(), String.class).invoke(compoundObject, new Object[]{key});
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;

    }

    /**
     * Retrieves a boolean value from the NBT tag compound.
     *
     * @param itemStack The ItemStack to retrieve the value from.
     * @param key       The key of the value.
     * @return The boolean value.
     */
    public boolean getBoolean(ItemStack itemStack, String key) {
        try {
            Object compoundObject = this.getCompound(itemStack);
            return (boolean) compoundObject.getClass().getMethod(this.reflection.getMethodGetBoolean(), String.class).invoke(compoundObject, new Object[]{key});
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;

    }

    /**
     * Sets an integer value in the NBT tag compound.
     *
     * @param itemStack The ItemStack to set the value in.
     * @param key       The key of the value.
     * @param value     The integer value to set.
     * @return The modified ItemStack.
     */
    public ItemStack setInt(ItemStack itemStack, String key, int value) {
        try {
            Object compoundObject = this.getCompound(itemStack);
            compoundObject.getClass().getMethod(this.reflection.getMethodSetInt(), String.class, int.class).invoke(compoundObject, key, value);
            return this.applyCompound(itemStack, compoundObject);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;

    }

    /**
     * Sets a long value in the NBT tag compound.
     *
     * @param itemStack The ItemStack to set the value in.
     * @param key       The key of the value.
     * @param value     The long value to set.
     * @return The modified ItemStack.
     */
    public ItemStack setLong(ItemStack itemStack, String key, long value) {
        try {
            Object compoundObject = this.getCompound(itemStack);
            compoundObject.getClass().getMethod(this.reflection.getMethodSetLong(), String.class, long.class).invoke(compoundObject, key, value);
            return this.applyCompound(itemStack, compoundObject);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;

    }

    /**
     * Sets a float value in the NBT tag compound.
     *
     * @param itemStack The ItemStack to set the value in.
     * @param key       The key of the value.
     * @param value     The float value to set.
     * @return The modified ItemStack.
     */
    public ItemStack setFloat(ItemStack itemStack, String key, float value) {
        try {
            Object compoundObject = this.getCompound(itemStack);
            compoundObject.getClass().getMethod(this.reflection.getMethodSetFloat(), String.class, float.class).invoke(compoundObject, key, value);
            return this.applyCompound(itemStack, compoundObject);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;

    }

    /**
     * Sets a boolean value in the NBT tag compound.
     *
     * @param itemStack The ItemStack to set the value in.
     * @param key       The key of the value.
     * @param value     The boolean value to set.
     * @return The modified ItemStack.
     */
    public ItemStack setBoolean(ItemStack itemStack, String key, boolean value) {

        try {
            Object compoundObject = this.getCompound(itemStack);
            compoundObject.getClass().getMethod(this.reflection.getMethodSetBoolean(), String.class, boolean.class).invoke(compoundObject, key, value);
            return this.applyCompound(itemStack, compoundObject);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;

    }

    /**
     * Sets a double value in the NBT tag compound.
     *
     * @param itemStack The ItemStack to set the value in.
     * @param key       The key of the value.
     * @param value     The double value to set.
     * @return The modified ItemStack.
     */
    public ItemStack setDouble(ItemStack itemStack, String key, double value) {
        try {
            Object compoundObject = this.getCompound(itemStack);
            compoundObject.getClass().getMethod(this.reflection.getMethodSetDouble(), String.class, double.class).invoke(compoundObject, key, value);
            return this.applyCompound(itemStack, compoundObject);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;

    }

    /**
     * Checks if a specified key is present in the NBT tag compound.
     *
     * @param itemStack The ItemStack to check.
     * @param key       The key to check for.
     * @return True if the key is present, false otherwise.
     */
    public boolean isKey(ItemStack itemStack, String key) {
        try {
            Object nbttagCompound = this.getCompound(itemStack);
            if (nbttagCompound == null) return false;
            return (boolean) nbttagCompound.getClass().getMethod(this.reflection.getMethodHasKey(), String.class).invoke(nbttagCompound, new Object[]{key});
        } catch (Exception ignored) {
        }
        return false;
    }
}
