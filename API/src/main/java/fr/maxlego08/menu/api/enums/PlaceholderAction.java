package fr.maxlego08.menu.api.enums;

import fr.maxlego08.menu.zcore.logger.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * @author Maxence
 *
 * Action used for the Placeholder button, more information <a href="https://docs.zmenu.dev/configurations/buttons#placeholder">here</a>
 */
public enum PlaceholderAction {

    BOOLEAN("b="),

    EQUALS_STRING("s="),
    DIFFERENT_STRING("s!="),
    EQUALSIGNORECASE_STRING("s=="),
    CONTAINS_STRING("sc"),

    EQUAL_TO("=="),

    SUPERIOR(">"), SUPERIOR_OR_EQUAL(">="),

    LOWER("<"), LOWER_OR_EQUAL("<="),

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