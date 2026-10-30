package fr.maxlego08.menu.api.itemstack;

import fr.maxlego08.menu.api.ZMenuKeys;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * The ItemStackSimilar interface defines methods for comparing ItemStacks in Minecraft.
 * It is used to determine if two ItemStacks are similar based on a specific implementation.
 */
public interface ItemStackSimilar extends Keyed {

    /**
     * Retrieves the name of the ItemStack comparison implementation.
     * This name can be used to identify different comparison strategies.
     *
     * @return The name of this ItemStack comparison strategy.
     */
    @NotNull
    String getName();

    /**
     * The key of this comparison in {@link fr.maxlego08.menu.api.registry.Registries#ITEM_VERIFICATION},
     * read from {@link #getName()} like a name written in a file.
     *
     * @return the key
     */
    @Override
    default @NotNull Key key() {
        return ZMenuKeys.parse(this.getName());
    }

    /**
     * Compares two ItemStacks to determine if they are similar according to a defined rule.
     * This method is used to compare items in specific contexts, such as menus or inventories.
     *
     * @param itemStackA The first ItemStack to be compared.
     * @param itemStackB The second ItemStack to be compared.
     * @return true if the two ItemStacks are considered similar, false otherwise.
     */
    boolean isSimilar(@NotNull ItemStack itemStackA,@NotNull ItemStack itemStackB);

}
