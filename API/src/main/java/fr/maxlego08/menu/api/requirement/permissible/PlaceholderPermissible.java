package fr.maxlego08.menu.api.requirement.permissible;

import fr.maxlego08.menu.api.enums.PlaceholderAction;

/**
 * Represents a condition based on placeholders for permissions.
 */
public interface PlaceholderPermissible {

    /**
     * Gets the action to be performed for the placeholder.
     *
     * @return The {@link PlaceholderAction}.
     */
    PlaceholderAction getPlaceholderAction();

    /**
     * Gets the placeholder that will be used for the condition.
     *
     * @return The placeholder string.
     */
    String getPlaceholder();

    /**
     * Gets the value that will be used for the specified action.
     *
     * @return The value string.
     */
    String getValue();
}
