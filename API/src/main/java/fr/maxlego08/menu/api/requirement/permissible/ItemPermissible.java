package fr.maxlego08.menu.api.requirement.permissible;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.requirement.Permissible;

/**
 * Represents a condition where a player is required to have a specific item.
 */
public interface ItemPermissible extends Permissible {

    /**
     * Gets the MenuItemStack that the player must have.
     *
     * @return The required MenuItemStack.
     */
    MenuItemStack getMenuItemStack();

    /**
     * Gets the number of items that the player must have at least. Put 0 to not check the amount.
     *
     * @return The required amount.
     */
    int getAmount();
}
