package fr.maxlego08.menu.api.button;

import fr.maxlego08.menu.api.enums.PlaceholderAction;

/**
 * 
 * @author Maxence
 * 
 * The button allows you to make checks using placeholders.
 * You must use the PlaceholderAPI plugin (https://www.spigotmc.org/resources/placeholderapi.6245/)
 */
public interface PlaceholderButton extends PermissibleButton {

	/**
	 * Returns the placeholder used
	 * 
	 * @return placeholder
	 */
	public String getPlaceHolder();
	
	/**
	 * Returns the action to perform with the placeholder
	 * 
	 * @return action
	 */
	public PlaceholderAction getAction();
	
	/**
	 * Allows to check if the placeholder is valid
	 * 
	 * @return boolean
	 */
	public boolean hasPlaceHolder();

	/**
	 * Returns the value to be checked
	 * 
	 * @return value
	 */
	public String getValue();
	
}
