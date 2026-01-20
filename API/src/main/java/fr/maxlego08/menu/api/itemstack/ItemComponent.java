package fr.maxlego08.menu.api.itemstack;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for an item component that can be applied to an item stack and optionally a player.
 * Typically used to attach extra behavior, metadata, or visual modifications.
 */
public abstract class ItemComponent {
    private ItemComponentLoader parentLoader;

    /**
     * Applies this component to the given ItemStack for an (optional) player.
     * Allows component to change item properties based on player or context.
     *
     * @param context   The build context containing relevant information.
     * @param itemStack The item stack to modify.
     * @param player    The player context, or null if not applicable.
     */
    public abstract void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player);

    /**
     * Gets the parent loader that created this component.
     *
     * @return The parent ItemComponentLoader.
     */
    @NotNull
    public ItemComponentLoader getParentLoader() {
        return this.parentLoader;
    }

    /**
     * Sets the parent loader that created this component.
     *
     * @param parentLoader The parent ItemComponentLoader.
     */
    public void setParentLoader(@NotNull ItemComponentLoader parentLoader) {
        this.parentLoader = parentLoader;
    }
}
