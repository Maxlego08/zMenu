package fr.maxlego08.menu.api.command;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.zcore.utils.storage.Savable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collection;
import java.util.Optional;

/**
 * Manages the registration, loading, and execution of commands associated with inventories.
 */
public interface CommandManager extends Savable {

    /**
     * Registers a new {@link Command}.
     *
     * @param command The new command to register.
     */
    void registerCommand(Command command);

    /**
     * Returns the list of commands associated with a specific plugin.
     *
     * @param plugin The plugin for which to retrieve commands.
     * @return A collection of commands associated with the plugin.
     */
    Collection<Command> getCommands(Plugin plugin);

    /**
     * Returns all registered commands.
     *
     * @return A collection of all registered commands.
     */
    Collection<Command> getCommands();

    /**
     * Unregisters all commands associated with a specific plugin.
     *
     * @param plugin The plugin for which to unregister commands.
     */
    void unregisterCommands(Plugin plugin);

    /**
     * Unregisters a specific command.
     *
     * @param command The command to unregister.
     */
    void unregisterCommands(Command command);

    /**
     * Loads commands from the configuration files.
     */
    void loadCommands();

    /**
     * Loads a specific command from a plugin's configuration file.
     *
     * @param plugin The plugin to which the command belongs.
     * @param file   The configuration file of the command.
     */
    void loadCommand(Plugin plugin, File file);

    /**
     * Retrieves the command associated with a specific inventory.
     *
     * @param inventory The inventory for which to retrieve the command.
     * @return An optional containing the associated command, or empty if not found.
     */
    Optional<Command> getCommand(Inventory inventory);

    /**
     * Registers a key-value pair for the arguments of a command associated with a player.
     *
     * @param player The player for whom to register the arguments.
     * @param key    The argument key.
     * @param value  The argument value.
     */
    void setPlayerArgument(Player player, String key, String value);

    /**
     * Retrieves the value of an argument associated with a player and key.
     *
     * @param player The player for whom to retrieve the argument.
     * @param key    The argument key.
     * @return An optional containing the argument value, or empty if not found.
     */
    Optional<String> getPlayerArgument(Player player, String key);

    /**
     * Retrieves the command associated with a specific name.
     *
     * @param commandName The name of the command.
     * @return An optional containing the associated command, or empty if not found.
     */
    Optional<Command> getCommand(String commandName);

    /**
     * Reloads a specific command.
     *
     * @param command The command to reload.
     * @return {@code true} if the reload was successful, otherwise {@code false}.
     */
    boolean reload(Command command);
}
