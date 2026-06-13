package fr.maxlego08.menu.common.utils;

import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public abstract class TranslationHelper {
    
    protected String getItemName(ItemStack itemStack) {

        if (itemStack == null) {
            return "";

        }
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
            return itemStack.getItemMeta().getDisplayName();
        }

        String name = itemStack.serialize().get("type").toString().replace("_", " ").toLowerCase(Locale.ROOT);
        return name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
    }

}
