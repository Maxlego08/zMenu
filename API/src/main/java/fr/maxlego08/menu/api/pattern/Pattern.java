package fr.maxlego08.menu.api.pattern;

import fr.maxlego08.menu.api.button.Button;

import java.util.Collection;

/**
 * <p>The Pattern interface represents a list of buttons that can be consistently used in multiple inventories.</p>
 * <p>For example, patterns can be used for decoration, allowing you to reuse the same set of buttons without duplicating code.</p>
 */
public interface Pattern {

    /**
     * Gets the name of this pattern.
     *
     * <p>This is the name that will be used to identify the pattern in the configuration.</p>
     *
     * @return The name of this pattern.
     */
    String name();

    /**
     * Gets the size of the inventory that this pattern is intended for.
     *
     * <p>If the pattern does not support multi-page inventories (i.e. {@link #enableMultiPage()} returns false), this method must return the size of the inventory that the pattern is intended for.</p>
     *
     * <p>If the pattern does support multi-page inventories, this method can return any positive number, and the pattern will be distributed across as many pages as necessary.</p>
     *
     * @return The size of the inventory that this pattern is intended for.
     */
    int inventorySize();

    /**
     * Gets the buttons that are a part of this pattern.
     *
     * <p>This method should return all the buttons that make up this pattern.</p>
     *
     * @return The buttons that make up this pattern.
     */
    Collection<Button> buttons();

    /**
     * Indicates whether the pattern supports multi-page inventories.
     * <p>
     * If this method returns true, the pattern will be able to be used in inventories of any size, and the buttons will be distributed across as many pages as necessary.
     * <p>
     * Otherwise, the pattern can only be used in inventories of the size returned by {@link #inventorySize()}.
     *
     * @return Whether the pattern supports multi-page inventories.
     */
    boolean enableMultiPage();

}
