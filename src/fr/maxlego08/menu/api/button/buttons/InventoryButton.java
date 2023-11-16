package fr.maxlego08.menu.api.button.buttons;

import fr.maxlego08.menu.api.button.Button;

/**
 * Represents a button that allows the player to open a new inventory.
 * The inventory to be opened is specified by its unique identifier.
 */
public interface InventoryButton extends Button {

    /**
     * Returns the unique identifier of the inventory to be opened.
     *
     * @return The identifier of the target inventory.
     */
    String getInventory();

    // Additional documentation or context can be added here if needed

}
