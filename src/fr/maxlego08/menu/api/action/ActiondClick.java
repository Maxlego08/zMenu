package fr.maxlego08.menu.api.action;

import java.util.List;

import fr.maxlego08.menu.api.sound.SoundOption;
import fr.maxlego08.menu.api.utils.OpenLink;

public interface ActiondClick {

	/**
	 * Returns the messages that will be sent to the player
	 * 
	 * @return messages
	 */
	List<String> getMessages();
	
	/**
	 * Returns the commands that the player will execute
	 * 
	 * @return commands
	 */
	List<String> getPlayerCommands();
	
	/**
	 * Returns the commands that the console will execute
	 * 
	 * @return commands
	 */
	List<String> getConsoleCommands();
	
	/**
	 * Allows you to open a link in a message
	 * 
	 * @return openLink
	 */ 
	public OpenLink getOpenLink();
	
	/**
	 * Sound that will be played when the player clicks
	 * 
	 * @return sound
	 */
	public SoundOption getSound();
}
