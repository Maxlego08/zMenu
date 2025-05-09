package fr.maxlego08.menu.api.button.buttons;

import fr.maxlego08.menu.api.button.Button;

/**
 * Represents a button within a user interface in a Minecraft plugin that allows the player
 * to navigate to the next page of an inventory. This interface extends the basic functionality
 * of a Button to specifically handle pagination within inventory GUIs.
 * <p>
 * The NextButton is designed to facilitate navigation in paginated inventories where content
 * exceeds the space available on a single inventory screen. Its behavior is inherently linked
 * to the structure of the inventory it is part of, automatically determining if a subsequent page
 * is available and enabling the player to move forward in the pagination sequence.
 * </p>
 * <p>
 * Implementations of this interface should handle cases where the current page is the last page
 * of the inventory. In such scenarios, the NextButton can either become inactive, disappear, or
 * be replaced with an alternative element defined by the "else" condition in the implementation,
 * ensuring a seamless and intuitive user experience.
 * </p>
 */
public interface NextButton extends Button {

}
