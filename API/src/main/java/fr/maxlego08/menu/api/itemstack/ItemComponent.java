package fr.maxlego08.menu.api.itemstack;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for an item component that can be applied to an item stack and optionally a player.
 * Typically used to attach extra behavior, metadata, or visual modifications.
 */
public interface ItemComponent {

    /**
     * Applies this component to the given ItemStack for an (optional) player.
     * Allows component to change item properties based on player or context.
     *
     * @param itemStack The item stack to modify.
     * @param player    The player context, or null if not applicable.
     */
    void apply(@NotNull ItemStack itemStack, @Nullable Player player);

}
