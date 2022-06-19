package fr.maxlego08.menu.api.command;

import java.io.File;
import java.util.Collection;
import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.zcore.utils.storage.Saveable;

public interface CommandManager extends Saveable{

	/**
	 * 
	 * @param command
	 */
	public void registerCommand(Command command);
	
	/**
	 * 
	 * @param plugin
	 * @return commands
	 */
	public Collection<Command> getCommands(Plugin plugin);
	
	/**
	 * 
	 * @return commands
	 */
	public Collection<Command> getCommands();
	
	/**
	 * 
	 * @param plugin
	 */
	public void unregisterCommands(Plugin plugin);
	
	/**
	 * 
	 * @param command
	 */
	public void unregisterCommands(Command command);

	/**
	 * 
	 */
	public void loadCommands();
	
	/**
	 * 
	 * @param plugin
	 * @param file
	 */
	public void loadCommand(Plugin plugin, File file);
	
	/**
	 * Return command
	 * 
	 * @param inventory
	 * @return optional
	 */
	public Optional<Command> getCommand(Inventory inventory);

	/**
	 * Allows to register a key and a value for the arguments of a command.
	 * 
	 * @param player
	 * @param value
	 */
	public void setPlayerArgument(Player player, String key, String value);
	
	/**
	 * Returns an optional of the argument with the key
	 * 
	 * @param player
	 * @param key
	 * @return optional
	 */
	public Optional<String> getPlayerArgument(Player player, String key);
	
}
