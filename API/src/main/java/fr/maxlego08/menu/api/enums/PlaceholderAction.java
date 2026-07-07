package fr.maxlego08.menu.api.enums;

import fr.maxlego08.menu.zcore.logger.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * @author Maxence
 * <p>
 * Action used for the Placeholder button, more information <a href="https://docs.zmenu.dev/configurations/buttons#placeholder">here</a>
 */
public enum PlaceholderAction {

    /**
     * Action used for the Placeholder button to compare the result with a boolean
     */
    BOOLEAN("b="),

    /**
     * Action used for the Placeholder button to compare the result with a string and check if it is equal to the value
     */
    EQUALS_STRING("s="),

    /**
     * Action used for the Placeholder button to compare the result with a string and check if it is different from the value
     */
    DIFFERENT_STRING("s!="),

    /**
     * Action used for the Placeholder button to compare the result with a string and check if it is equal (case insensitive) to the value
     */
    EQUALSIGNORECASE_STRING("s=="),

    /**
     * Action used for the Placeholder button to compare the result with a string and check if it contains the value
     */
    CONTAINS_STRING("sc"),

    /**
     * Action used for the Placeholder button to compare the result with a number and check if it is equal to the value
     */
    EQUAL_TO("=="),

    /**
     * Action used for the Placeholder button to compare the result with a number and check if it is superior to the value
     */
    SUPERIOR(">"),

    /**
     * Action used for the Placeholder button to compare the result with a number and check if it is superior or equal to the value
     */
    SUPERIOR_OR_EQUAL(">="),

    /**
     * Action used for the Placeholder button to compare the result with a number and check if it is lower than the value
     */
    LOWER("<"),

    /**
     * Action used for the Placeholder button to compare the result with a number and check if it is lower or equal to the value
     */
    LOWER_OR_EQUAL("<="),

    ;

    private final List<String> aliases;

    PlaceholderAction(String... aliases) {
        this.aliases = Arrays.asList(aliases);
    }

    /**
     * Allows you to retrieve the action based on a string without triggering an error
     *
     * @param string Current string
     * @return boolean
     */
    public static PlaceholderAction from(String string) {
        if (string == null) return null;

        for (PlaceholderAction action : values()) {
            if (action.name().equalsIgnoreCase(string)) {
                return action;
            }
            for (String alias : action.aliases) {
                if (alias.equalsIgnoreCase(string)) {
                    return action;
                }
            }
        }
        Logger.info("Impossible to find the " + string + " action for placeholder", Logger.LogType.ERROR);
        return null;
    }

    /**
     * Allows to check if the condition must be on a string
     *
     * @return boolean
     */
    public boolean isString() {
        return this == EQUALS_STRING || this == EQUALSIGNORECASE_STRING || this == CONTAINS_STRING | this == DIFFERENT_STRING;
    }

}