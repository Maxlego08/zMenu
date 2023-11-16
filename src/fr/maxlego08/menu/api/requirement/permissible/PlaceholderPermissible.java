package fr.maxlego08.menu.api.requirement.permissible;

import fr.maxlego08.menu.api.enums.PlaceholderAction;
import fr.maxlego08.menu.api.requirement.Permissible;

public interface PlaceholderPermissible extends Permissible {

    /**
     * Returns the action to be performed
     *
     * @return {@link PlaceholderAction}
     */
    PlaceholderAction getPlaceholderAction();

    /**
     * The placeholder that will be used
     *
     * @return string
     */
    String getPlaceholder();

    /**
     * The value that will be used for the action
     *
     * @return string
     */
    String getValue();

}
