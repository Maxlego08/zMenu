package fr.maxlego08.menu.api.command;

import java.io.File;
import java.util.Collection;
import java.util.Optional;

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
	public void unregistetCommands(Plugin plugin);
	
	/**
	 * 
	 * @param command
	 */
	public void unregistetCommands(Command command);

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
	
}
