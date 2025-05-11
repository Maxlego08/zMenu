package fr.maxlego08.menu.api.button.buttons;

import fr.maxlego08.menu.api.button.Button;

import java.util.List;

/**
 * Represents a button within a user interface in a Minecraft plugin that allows the player
 * to open a new inventory. This interface extends the basic functionality of a Button
 * to specifically handle inventory interactions, enabling the creation of complex GUIs
 * where actions can lead to different inventory screens.
 * <p>
 * The inventory to be opened by this button is identified by a unique identifier, allowing
 * for dynamic and flexible GUI navigation based on the game's logic or player's actions.
 * Additional parameters such as arguments and page numbers can be used to further customize
 * the behavior or content of the inventory to be opened.
 * </p>
 */
public abstract class InventoryButton extends Button {

    /**
     * Returns the unique identifier of the inventory to be opened when this button is interacted with.
     * This identifier is used to determine which inventory screen is to be displayed to the player,
     * facilitating the linking of multiple GUI screens within the plugin.
     *
     * @return The unique identifier of the target inventory as a {@code String}.
     */
    public abstract String getInventory();

    /**
     * Provides a list of arguments that may be required for opening the inventory.
     * These arguments can include specific details or parameters that modify the
     * inventory's appearance or functionality, allowing for a dynamic and contextual
     * user experience based on the game's state or player's input.
     *
     * @return A list of {@code String} arguments that are associated with the inventory to be opened.
     */
    public abstract List<String> getArguments();

    /**
     * Retrieves the page number of the inventory to be opened if the inventory supports pagination.
     * This is useful for inventories that display a large amount of content that is divided across
     * multiple pages, allowing users to navigate through pages of items or options.
     *
     * @return The page number as an {@code int} indicating which page of the inventory should be shown.
     */
    public abstract int getToPage();

}

