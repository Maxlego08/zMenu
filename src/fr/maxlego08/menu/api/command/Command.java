package fr.maxlego08.menu.api.command;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;

/**
 * <p>A command opens an {@link fr.maxlego08.menu.api.Inventory}</p>
 */
public interface Command {

    /**
     * @return command
     */
	String getCommand();

    /**
     * @return aliases
     */
	List<String> getAliases();

    /**
     * @return permissions
     */
	String getPermission();

    /**
     * @return inventory
     */
	String getInventory();

    /**
     * @return plugin
     */
	Plugin getPlugin();

    /**
     * @return argument
     */
	List<String> getArguments();

    /**
     * @return boolean
     */
	boolean hasArgument();

    /**
     * @return file
     */
	File getFile();

    /**
     * @return path
     */
	String getPath();

}
