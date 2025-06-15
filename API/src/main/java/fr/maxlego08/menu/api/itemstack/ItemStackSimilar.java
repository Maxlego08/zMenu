package fr.maxlego08.menu.api.itemstack;

import org.bukkit.inventory.ItemStack;

/**
 * The ItemStackSimilar interface defines methods for comparing ItemStacks in Minecraft.
 * It is used to determine if two ItemStacks are similar based on a specific implementation.
 */
public interface ItemStackSimilar {

    /**
     * Retrieves the name of the ItemStack comparison implementation.
     * This name can be used to identify different comparison strategies.
     *
     * @return The name of this ItemStack comparison strategy.
     */
    String getName();

    /**
     * Compares two ItemStacks to determine if they are similar according to a defined rule.
     * This method is used to compare items in specific contexts, such as menus or inventories.
     *
     * @param itemStackA The first ItemStack to be compared.
     * @param itemStackB The second ItemStack to be compared.
     * @return true if the two ItemStacks are considered similar, false otherwise.
     */
    boolean isSimilar(ItemStack itemStackA, ItemStack itemStackB);

}
