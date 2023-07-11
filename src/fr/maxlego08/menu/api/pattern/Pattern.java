package fr.maxlego08.menu.api.pattern;

import fr.maxlego08.menu.api.button.Button;

import java.util.Collection;

/**
 * <p>A pattern is a list of buttons that will always be used, you can add several patterns to an inventory</p>
 * <p>For example to put decoration, you just have to create a pattern and use it everywhere. You avoid having to copy and paste the same thing</p>
 */
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
