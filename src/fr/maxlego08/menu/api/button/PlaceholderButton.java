package fr.maxlego08.menu.api.button;

import fr.maxlego08.menu.api.requirement.permissible.PlaceholderPermissible;

import java.util.List;

/**
 * @author Maxence
 * <p>
 * The button allows you to make checks using placeholders.
 * You must use the PlaceholderAPI plugin (<a href="https://www.spigotmc.org/resources/placeholderapi.6245/">PlaceholderAPI</a>)
 * </p>
 */
public interface PlaceholderButton {

    /**
     * Returns the list of placeholders
     *
     * @return placeholders
     */
    List<PlaceholderPermissible> getPlaceholders();

    /**
     * Allows to check if the placeholder is valid
     *
     * @return boolean
     */
    boolean hasPlaceHolder();

}
