package fr.maxlego08.menu.item;

import fr.maxlego08.menu.api.MenuItemStack;

import java.util.Set;

/**
 * Record that holds data for a custom item, including its MenuItemStack and associated mechanic IDs.
 * This allows for optimized lookups when giving items or checking mechanics.
 *
 * @param menuItemStack the ItemStack configuration for this custom item
 * @param mechanicIds set of mechanic IDs that are implemented for this item
 */
public record CustomItemData(MenuItemStack menuItemStack, Set<String> mechanicIds, boolean saveOwnerInPDC) {

    /**
     * Check if this item has a specific mechanic implemented.
     *
     * @param mechanicId the mechanic ID to check
     * @return true if the mechanic is implemented for this item
     */
    public boolean hasMechanic(String mechanicId) {
        return mechanicIds.contains(mechanicId);
    }

    /**
     * Check if this item has any mechanics.
     *
     * @return true if at least one mechanic is implemented
     */
    public boolean hasMechanics() {
        return !mechanicIds.isEmpty();
    }
}

