package fr.maxlego08.menu.api.action;

import java.util.List;

import org.bukkit.entity.Player;

import fr.maxlego08.menu.api.action.data.ActionPlayerData;
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

	/**
	 * Return a list of ActionPlayerData
	 * 
	 * @return datas
	 */
	public List<ActionPlayerData> getPlayerDatas();
	
	/**
	 * Allows to execute the actions of the click
	 * 
	 * @param player
	 */
	void execute(Player player);
}
