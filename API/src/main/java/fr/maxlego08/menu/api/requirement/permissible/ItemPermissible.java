package fr.maxlego08.menu.api.requirement.permissible;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;

import java.util.List;

/**
 * Represents a condition where a player is required to have a specific item.
 */
public abstract class ItemPermissible extends Permissible {

    public ItemPermissible(List<Action> denyActions, List<Action> successActions) {
        super(denyActions, successActions);
    }

    /**
     * Gets the MenuItemStack that the player must have.
     *
     * @return The required MenuItemStack.
     */
    public abstract MenuItemStack getMenuItemStack();

    /**
     * Gets the number of items that the player must have at least. Put 0 to not check the amount.
     *
     * @return The required amount.
     */
    public abstract int getAmount();
}
