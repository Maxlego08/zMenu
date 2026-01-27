package fr.maxlego08.menu.api.command;

import fr.maxlego08.menu.api.requirement.Action;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;

/**
 * Represents a command that opens an {@link fr.maxlego08.menu.api.Inventory}.
 */
public interface Command {

    /**
     * Gets the primary command name.
     *
     * @return The primary command name.
     */
    String command();

    /**
     * Gets the aliases for the command.
     *
     * @return The list of command aliases.
     */
    List<String> aliases();

    /**
     * Gets whether the console can use the command.
     *
     * @return {@code true} if the console can use the command, otherwise {@code false}.
     */
    boolean consoleCanUse();

    /**
     * Gets the permission required to execute the command.
     *
     * @return The permission node required executing the command.
     */
    String permission();

    /**
     * Gets the name of the inventory to open when the command is executed.
     *
     * @return The name of the inventory.
     */
    String inventory();

    /**
     * Gets the plugin associated with the command.
     *
     * @return The plugin associated with the command.
     */
    Plugin plugin();

    /**
     * Gets the list of command arguments.
     *
     * @return The list of command arguments.
     */
    List<CommandArgument> arguments();

    /**
     * Gets the list of command arguments as strings.
     *
     * @return The list of command arguments as strings.
     */
    List<String> getCommandArguments();

    /**
     * Checks if the command has any arguments.
     *
     * @return {@code true} if the command has arguments, otherwise {@code false}.
     */
    boolean hasArgument();

    /**
     * Gets the file associated with the command.
     *
     * @return The file associated with the command.
     */
    File file();

    /**
     * Gets the path of the command in the configuration file.
     *
     * @return The path of the command in the configuration file.
     */
    String path();

    /**
     * Gets the list of actions associated with the command.
     *
     * @return The list of actions associated with the command.
     */
    List<Action> actions();

    /**
     * Gets the list of sub-commands associated with the command.
     *
     * @return The list of sub-commands associated with the command.
     */
    List<Command> subCommands();

    /**
     * Gets the message to display when the player does not have the required permission.
     *
     * @return The message to display when the player does not have the required permission.
     */
    String denyMessage();
}
