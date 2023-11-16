package fr.maxlego08.menu.api.button;

import fr.maxlego08.menu.api.requirement.permissible.PlaceholderPermissible;

import java.util.List;

/**
 * The `PlaceholderButton` interface allows you to perform checks using placeholders. It requires the PlaceholderAPI plugin
 * (available at: <a href="https://www.spigotmc.org/resources/placeholderapi.6245/">PlaceholderAPI</a>).
 */
public interface PlaceholderButton {

    /**
     * Returns the list of placeholders used in the button.
     *
     * @return The list of placeholder permissions.
     */
    List<PlaceholderPermissible> getPlaceholders();

    /**
     * Checks if the button uses any placeholders.
     *
     * @return `true` if placeholders are used, otherwise `false`.
     */
    boolean hasPlaceHolder();

}
