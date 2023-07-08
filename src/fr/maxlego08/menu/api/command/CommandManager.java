package fr.maxlego08.menu.api.command;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.zcore.utils.storage.Saveable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collection;
import java.util.Optional;

public interface CommandManager extends Saveable {

    /**
     * @param command
     */
	void registerCommand(Command command);

    /**
     * @param plugin
     * @return commands
     */
	Collection<Command> getCommands(Plugin plugin);

    /**
     * @return commands
     */
	Collection<Command> getCommands();

    /**
     * @param plugin
     */
	void unregisterCommands(Plugin plugin);

    /**
     * @param command
     */
	void unregisterCommands(Command command);

    /**
     *
     */
	void loadCommands();

    /**
     * @param plugin
     * @param file
     */
	void loadCommand(Plugin plugin, File file);

    /**
     * Return command
     *
     * @param inventory
     * @return optional
     */
	Optional<Command> getCommand(Inventory inventory);

    /**
     * Allows to register a key and a value for the arguments of a command.
     *
     * @param player
     * @param value
     */
	void setPlayerArgument(Player player, String key, String value);

    /**
     * Returns an optional of the argument with the key
     *
     * @param player
     * @param key
     * @return optional
     */
	Optional<String> getPlayerArgument(Player player, String key);

    /**
     * Get command by name
     *
     * @param commandName
     * @return optional
     */
	Optional<Command> getCommand(String commandName);

    /**
     * Reload command
     *
     * @param command
     */
	boolean reload(Command command);

}
