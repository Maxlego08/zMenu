package fr.maxlego08.menu.api.animation;

import fr.maxlego08.menu.api.MenuPlugin;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for objects that perform title animations in inventories.
 * Animations typically play when opening certain GUI elements.
 */
public interface TitleAnimation {

    /**
     * Plays a title animation for a specific inventory container.
     *
     * @param plugin      The plugin instance.
     * @param containerId The ID of the inventory container.
     * @param type        The type of the inventory.
     * @param size        The size of the inventory.
     * @param args        Additional arguments for animation context.
     * @return A PlayerTitleAnimation if started, otherwise null.
     */
    @Nullable
    PlayerTitleAnimation playTitleAnimation(@NotNull MenuPlugin plugin, int containerId, @NotNull InventoryType type, int size, Object... args);
}
