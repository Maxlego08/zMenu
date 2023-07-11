package fr.maxlego08.menu.api.command;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.zcore.utils.storage.Savable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collection;
import java.util.Optional;

public interface CommandManager extends Savable {

    /**
     * Register a new {@link Command}
     *
     * @param command New command
     */
	void registerCommand(Command command);

    /**
     * Returns the list of commands of a plugin
     *
     * @param plugin Current plugin
     * @return commands
     */
	Collection<Command> getCommands(Plugin plugin);

    /**
     * @return commands
     */
	Collection<Command> getCommands();

    /**
     * @param plugin Current plugin
     */
	void unregisterCommands(Plugin plugin);

    /**
     * @param command Current plugin
     */
	void unregisterCommands(Command command);

    /**
     *
     */
	void loadCommands();

    /**
     * @param plugin Current plugin
     * @param file Current file
     */
	void loadCommand(Plugin plugin, File file);

    /**
     * Return command by an inventory
     *
     * @param inventory Current inventory
     * @return optional
     */
	Optional<Command> getCommand(Inventory inventory);

    /**
     * Allows to register a key and a value for the arguments of a command.
     *
     * @param player Current Player
     * @param key Argument key
     * @param value Argument value
     */
	void setPlayerArgument(Player player, String key, String value);

    /**
     * Returns an optional of the argument with the key
     *
     * @param player Current player
     * @param key Argument value
     * @return optional
     */
	Optional<String> getPlayerArgument(Player player, String key);

    /**
     * Get command by name
     *
     * @param commandName The command name
     * @return optional
     */
	Optional<Command> getCommand(String commandName);

    /**
     * Reload command
     *
     * @param command Command
     */
	boolean reload(Command command);

}
