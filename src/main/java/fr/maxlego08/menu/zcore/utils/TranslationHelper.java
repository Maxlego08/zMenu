package fr.maxlego08.menu.zcore.utils;

import org.bukkit.inventory.ItemStack;

public abstract class TranslationHelper {

    /**
     * Allows to translate the item name, if the zTranslator plugin is active, then the translated name will be retrieved
     *
     * @param itemStack
     * @return item name
     */
    protected String getItemName(ItemStack itemStack) {

        if (itemStack == null) {
            return "";

        }
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
            return itemStack.getItemMeta().getDisplayName();
        }

        String name = itemStack.serialize().get("type").toString().replace("_", " ").toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

}
