package fr.maxlego08.menu.api.pattern;

import fr.maxlego08.menu.api.button.Button;

import java.util.Collection;

public interface Pattern {

    /**
     * Retrieves the name of the pattern to be used in inventories
     *
     * @return pattern name
     */
    String getName();

    /**
     * Get the inventory size for the pattern
     *
     * @return inventory size
     */
    int getInventorySize();

    /**
     * Return the list of buttons
     *
     * @return buttons
     */
    Collection<Button> getButtons();

}
