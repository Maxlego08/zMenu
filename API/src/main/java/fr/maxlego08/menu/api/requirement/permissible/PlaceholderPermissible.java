package fr.maxlego08.menu.api.requirement.permissible;

import fr.maxlego08.menu.api.enums.PlaceholderAction;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;

import java.util.List;

/**
 * Represents a condition based on placeholders for permissions.
 */
public abstract class PlaceholderPermissible extends Permissible {

    public PlaceholderPermissible(List<Action> denyActions, List<Action> successActions) {
        super(denyActions, successActions);
    }

    /**
     * Gets the action to be performed for the placeholder.
     *
     * @return The {@link PlaceholderAction}.
     */
    public abstract PlaceholderAction getPlaceholderAction();

    /**
     * Gets the placeholder that will be used for the condition.
     *
     * @return The placeholder string.
     */
    public abstract String getPlaceholder();

    /**
     * Gets the value that will be used for the specified action.
     *
     * @return The value string.
     */
    public abstract String getValue();
}
