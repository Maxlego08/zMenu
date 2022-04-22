package fr.maxlego08.menu.api.command;

import java.util.List;

import org.bukkit.plugin.Plugin;

public interface Command {

	/**
	 * 
	 * @return command
	 */
	public String getCommand();
	
	/**
	 * 
	 * @return aliases
	 */
	public List<String> getAliases();
	
	/**
	 * 
	 * @return permissions
	 */
	public String getPermission();
	
	/**
	 * 
	 * @return inventory
	 */
	public String getInventory();
	
	/**
	 * 
	 * @return plugin
	 */
	public Plugin getPlugin();
	
}
