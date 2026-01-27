package fr.maxlego08.menu.api.dupe;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface DupeManager {

    /**
     * the key to determining where the item comes from
     */
    String KEY = "ZMENU-ITEM";

    /**
     * protect an item from dupe
     *
     * @param itemStack
     * @return protected itemStack
     */
    @NotNull
    ItemStack protectItem(@NotNull ItemStack itemStack);

    /**
     * check if an item is a result of a dupe
     *
     * @param itemStack the item to check
     * @return true if the item is a dupe item
     */
    boolean isDupeItem(@NotNull ItemStack itemStack);

}
