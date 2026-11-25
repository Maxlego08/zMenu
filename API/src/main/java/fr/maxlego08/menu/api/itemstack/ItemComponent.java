package fr.maxlego08.menu.api.itemstack;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

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
     * Whether this component gives the same result for every player and every build. zMenu's components answer from
     * their fields ({@link StaticCheck}: their resolvables, the items they hold); a component of another plugin is
     * dynamic unless it overrides this, and so is one that reads the player in another way.
     *
     * @return true when applying it never changes
     */
    public boolean isStatic() {
        return StaticCheck.fieldsStatic(this);
    }

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

    /**
     * Returns this component in the form its loader reads, to be written under
     * {@code components.<name>} of the item: a plain value for a component set with one value
     * ({@code max-stack-size: 16}), or a map for a component with its own section.
     *
     * @return A plain value, a list or a map, ready to be written in YAML.
     * @throws UnsupportedOperationException If this component cannot be serialized.
     */
    public @Nullable Object serialize() {
        throw new UnsupportedOperationException("The item component " + this.getClass().getName() + " cannot be serialized");
    }

    protected <T> void applyResolvable(@NotNull BuildContext context, @NotNull Consumer<T> consumer, @Nullable Resolvable<T> resolvable) {
        Resolvable.applyResolvable(context, resolvable, consumer);
    }
}
