package fr.maxlego08.menu.api.button;

import fr.maxlego08.menu.api.enums.PlaceholderAction;

/**
 * @author Maxence
 * <p>
 * The button allows you to make checks using placeholders.
 * You must use the PlaceholderAPI plugin (https://www.spigotmc.org/resources/placeholderapi.6245/)
 */
public interface PlaceholderButton {

    /**
     * Returns the placeholder used
     *
     * @return placeholder
     */
	String getPlaceHolder();

    /**
     * Returns the action to perform with the placeholder
     *
     * @return action
     */
	PlaceholderAction getAction();

    /**
     * Allows to check if the placeholder is valid
     *
     * @return boolean
     */
	boolean hasPlaceHolder();

    /**
     * Returns the value to be checked
     *
     * @return value
     */
	String getValue();

}
