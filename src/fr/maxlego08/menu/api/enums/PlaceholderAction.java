package fr.maxlego08.menu.api.enums;

/**
 *
 * @author Maxence
 *
 * Action used for the Placeholder button, more information <a href="https://docs.zmenu.dev/configurations/buttons#placeholder">here</a>
 */
public enum PlaceholderAction {

	BOOLEAN,

	EQUALS_STRING, EQUALSIGNORECASE_STRING, CONTAINS_STRING,

	EQUAL_TO,
	
	SUPERIOR, SUPERIOR_OR_EQUAL,

	LOWER, LOWER_OR_EQUAL,;

	/**
	 * Allows you to retrieve the action based on a string without triggering an error
	 * 
	 * @param string Current string
	 * @return boolean
	 */
	public static PlaceholderAction from(String string) {
		if (string == null) {
			return null;
		}
		for (PlaceholderAction action : values()) {
			if (action.name().equalsIgnoreCase(string)) {
				return action;
			}
		}
		return null;
	}

	/**
	 * Allows to check if the condition must be on a string
	 * 
	 * @return boolean
	 */
	public boolean isString() {
		return this == EQUALS_STRING || this == EQUALSIGNORECASE_STRING;
	}

}