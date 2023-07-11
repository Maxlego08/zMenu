package fr.maxlego08.menu.zcore.utils.nms;

import fr.maxlego08.menu.zcore.utils.Base64;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class ItemStackUtils {

    private static final double NMS_VERSION = NMSUtils.getNMSVersion();
    private static final Map<ItemStack, String> itemStackSerialized = new HashMap<ItemStack, String>();

    /**
     * Change {@link ItemStack} to {@link String}
     *
     * @param paramItemStack
     * @return {@link String}
     */
    public static String serializeItemStack(ItemStack paramItemStack) {

        if (paramItemStack == null)
            return "null";

        if (itemStackSerialized.containsKey(paramItemStack))
            return itemStackSerialized.get(paramItemStack);

        ByteArrayOutputStream localByteArrayOutputStream = null;
        try {
            Class<?> localClass = EnumReflectionItemStack.NBTTAGCOMPOUND.getClassz();
            Constructor<?> localConstructor = localClass.getConstructor();
            Object localObject1 = localConstructor.newInstance();
            Object localObject2 = EnumReflectionItemStack.CRAFTITEMSTACK.getClassz()
                    .getMethod("asNMSCopy", ItemStack.class)
                    .invoke(null, paramItemStack);

            if (NMSUtils.isNewNBTVersion()) {
                EnumReflectionItemStack.ITEMSTACK.getClassz().getMethod("b", localClass)
                        .invoke(localObject2, localObject1);
            } else {
                EnumReflectionItemStack.ITEMSTACK.getClassz().getMethod("save", localClass)
                        .invoke(localObject2, localObject1);
            }

            localByteArrayOutputStream = new ByteArrayOutputStream();
            EnumReflectionItemStack.NBTCOMPRESSEDSTREAMTOOLS.getClassz()
                    .getMethod("a", localClass, OutputStream.class)
                    .invoke(null, localObject1, localByteArrayOutputStream);
        } catch (Exception localException) {
            // localException.printStackTrace();
        }
        String string = Base64.encode(localByteArrayOutputStream.toByteArray());
        itemStackSerialized.put(paramItemStack, string);
        return string;
    }

    /**
     * Change {@link String} to {@link ItemStack}
     *
     * @param paramString
     * @return {@link ItemStack}
     */
    public static ItemStack deserializeItemStack(String paramString) {

        if (paramString.equals("null"))
            return null;

        ByteArrayInputStream localByteArrayInputStream = null;
        try {
            localByteArrayInputStream = new ByteArrayInputStream(Base64.decode(paramString));
        } catch (Exception localBase64DecodingException) {
        }
        Class<?> localClass1 = EnumReflectionItemStack.NBTTAGCOMPOUND.getClassz();
        Class<?> localClass2 = EnumReflectionItemStack.ITEMSTACK.getClassz();
        Object localObject1 = null;
        ItemStack localItemStack = null;
        Object localObject2 = null;
        try {
            localObject1 = EnumReflectionItemStack.NBTCOMPRESSEDSTREAMTOOLS.getClassz()
                    .getMethod("a", InputStream.class)
                    .invoke(null, localByteArrayInputStream);

            if (NMS_VERSION == 1.11D || NMS_VERSION == 1.12D) {
                Constructor<?> localConstructor = localClass2.getConstructor(localClass1);
                localObject2 = localConstructor.newInstance(localObject1);
            } else if (NMSUtils.isNewVersion()) {
                localObject2 = localClass2.getMethod("a", localClass1).invoke(null,
						localObject1);
            } else {
                localObject2 = localClass2.getMethod("createStack", localClass1).invoke(null,
						localObject1);
            }

            localItemStack = (ItemStack) EnumReflectionItemStack.CRAFTITEMSTACK.getClassz()
                    .getMethod("asBukkitCopy", localClass2).invoke(null, new Object[]{localObject2});
        } catch (Exception localException) {
            // localException.printStackTrace();
        }
        if (localItemStack != null && !itemStackSerialized.containsKey(localItemStack))
            itemStackSerialized.put(localItemStack, paramString);
        return localItemStack;

    }

    public enum EnumReflectionItemStack {

        ITEMSTACK("ItemStack", "net.minecraft.world.item.ItemStack"),

        CRAFTITEMSTACK("inventory.CraftItemStack", true),

        NBTCOMPRESSEDSTREAMTOOLS("NBTCompressedStreamTools", "net.minecraft.nbt.NBTCompressedStreamTools"),

        NBTTAGCOMPOUND("NBTTagCompound", "net.minecraft.nbt.NBTTagCompound"),

        ;

        private final String oldClassName;
        private final String newClassName;
        private final boolean isBukkit;

        /**
         * @param oldClassName
         * @param newClassName
         * @param isBukkit
         */
		EnumReflectionItemStack(String oldClassName, String newClassName, boolean isBukkit) {
            this.oldClassName = oldClassName;
            this.newClassName = newClassName;
            this.isBukkit = isBukkit;
        }

        /**
         * @param oldClassName
         * @param newClassName
         */
		EnumReflectionItemStack(String oldClassName, String newClassName) {
            this(oldClassName, newClassName, false);
        }

        /**
         * @param oldClassName
         */
		EnumReflectionItemStack(String oldClassName) {
            this(oldClassName, null, false);
        }

        /**
         * @param oldClassName
         * @param isBukkit
         */
		EnumReflectionItemStack(String oldClassName, boolean isBukkit) {
            this(oldClassName, null, isBukkit);
        }

        /**
         * Create class
         *
         * @return class
         */
        public Class<?> getClassz() {
            String nmsPackage = Bukkit.getServer().getClass().getPackage().getName();
            String nmsVersion = nmsPackage.replace(".", ",").split(",")[3];
            String var3 = NMSUtils.isNewNMSVersion()
                    ? this.isBukkit ? "org.bukkit.craftbukkit." + nmsVersion + "." + this.oldClassName
                    : this.newClassName
                    : (this.isBukkit ? "org.bukkit.craftbukkit." : "net.minecraft.server.") + nmsVersion + "."
                    + this.oldClassName;
            Class<?> localClass = null;
            try {
                localClass = Class.forName(var3);
            } catch (ClassNotFoundException localClassNotFoundException) {
                localClassNotFoundException.printStackTrace();
            }
            return localClass;
        }

    }

}
