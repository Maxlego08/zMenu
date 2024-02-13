package fr.maxlego08.menu.api.pattern;

import fr.maxlego08.menu.api.button.Button;

import java.util.Collection;

/**
 * <p>The Pattern interface represents a list of buttons that can be consistently used in multiple inventories.</p>
 * <p>For example, patterns can be used for decoration, allowing you to reuse the same set of buttons without duplicating code.</p>
 */
public interface Pattern {

    /**
     * Retrieves the name of the pattern, which can be used to identify it in inventories.
     *
     * @return The name of the pattern.
     */
    String getName();

    /**
     * Gets the size of the inventory associated with the pattern.
     *
     * @return The size of the inventory.
     */
    int getInventorySize();

    /**
     * Returns the collection of buttons included in the pattern.
     *
     * @return The buttons in the pattern.
     */
    Collection<Button> getButtons();

    boolean enableMultiPage();

}
