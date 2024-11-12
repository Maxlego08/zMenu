package fr.maxlego08.menu.api.font;

/**
 * <p>
 * The {@code FontImage} interface provides the ability to replace color codes
 * in a string with a character that represents the color.
 * </p>
 * <p>
 * For example, the string "&4Hello" will be replaced with the character
 * representing the color red, followed by the string "Hello".
 * </p>
 * <p>
 * This interface is used to provide a way to replace color codes in a string
 * with a character that represents the color, when the string is displayed to
 * the user in a GUI.
 * </p>
 *
 * @author Maxlego08
 */
public interface FontImage {

    /**
     * <p>
     * Replaces the color codes in the given string with the corresponding
     * characters.
     * </p>
     * <p>
     * For example, the string "&4Hello" will be replaced with the character
     * representing the color red, followed by the string "Hello".
     * </p>
     *
     * @param string The string in which the color codes are to be replaced.
     * @return The string with the color codes replaced.
     */
    String replace(String string);

}