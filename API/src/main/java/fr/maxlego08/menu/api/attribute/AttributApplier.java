package fr.maxlego08.menu.api.attribute;

import fr.maxlego08.menu.api.MenuPlugin;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Abstraction for classes that apply attribute modifiers to items using platform-specific logic.
 * Implementations may target Vanilla, Spigot, or Paper APIs.
 */
public interface AttributApplier {

    /**
     * Applies the given attribute wrappers to an item stack, using the specified merge strategy.
     *
     * @param itemStack  The item stack to apply attributes to.
     * @param attributes List of attribute wrappers to apply.
     * @param plugin     Plugin instance for context.
     * @param strategy   Merge strategy for combining attributes (may be null).
     */
    void applyAttributesModern(@NotNull ItemStack itemStack,@NotNull List<AttributeWrapper> attributes,@NotNull MenuPlugin plugin,@Nullable AttributeMergeStrategy strategy);

}
