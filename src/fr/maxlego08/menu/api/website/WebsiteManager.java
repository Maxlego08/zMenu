package fr.maxlego08.menu.api.website;

import org.bukkit.command.CommandSender;

public interface WebsiteManager {

	/**
	 * Allows you to connect to the site 
	 * 
	 * @param sender
	 * @param email
	 * @param password
	 */
	public void login(CommandSender sender, String token);

	/**
	 * Disconnect to the site
	 * 
	 * @param sender
	 */
	public void disconnect(CommandSender sender);
	
}
