package fr.maxlego08.menu.api.button.buttons;

import fr.maxlego08.menu.api.button.Button;

/**
 * Allows the player to open a new {@link fr.maxlego08.menu.api.Inventory}.
 */
public interface InventoryButton extends Button {

    /**
     * Returns the inventory that will be used
     *
     * @return inventory
     */
	String getInventory();

}
