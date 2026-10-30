package fr.maxlego08.menu.common.utils.nms;

import org.bukkit.inventory.ItemStack;

public class ItemStackUtils {

    public static String serializeItemStack(ItemStack paramItemStack) {

        if (paramItemStack == null) {
            return "null";
        }

        return Base64ItemStack.encode(paramItemStack);
    }

    public static ItemStack deserializeItemStack(String paramString) {

        if (paramString == null || paramString.equals("null")) {
            return null;
        }

        return Base64ItemStack.decode(paramString);
    }
}
