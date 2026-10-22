package fr.maxlego08.menu.api.command;

import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.SectionSerializable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a command that opens an {@link fr.maxlego08.menu.api.Inventory}.
 */
public interface Command extends SectionSerializable {

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
     * Gets the list of requirements that must be met for the command's actions to be executed.
     * @return The list of requirements that must be met for the command's actions to be executed.
     */
    List<Requirement> actions_requirements();

    /**
     * Gets the message to display when the player does not have the required permission.
     *
     * @return The message to display when the player does not have the required permission.
     */
    String denyMessage();

    /**
     * Writes this command in the format the command loader reads. Values equal to their default are left out.
     *
     * @param section The section of the command.
     */
    @Override
    default void serialize(@NotNull ConfigurationSection section) {
        section.set("command", this.command());
        if (this.permission() != null) section.set("permission", this.permission());
        if (this.inventory() != null) section.set("inventory", this.inventory());
        if (this.aliases() != null && !this.aliases().isEmpty()) section.set("aliases", this.aliases());
        if (this.consoleCanUse()) section.set("console-can-use", true);
        if (this.denyMessage() != null) section.set("deny-message", this.denyMessage());

        if (this.arguments() != null && !this.arguments().isEmpty()) {
            List<Map<String, Object>> arguments = new ArrayList<>(this.arguments().size());
            for (CommandArgument argument : this.arguments()) {
                arguments.add(argument.serialize());
            }
            section.set("arguments", arguments);
        }

        if (this.actions() != null && !this.actions().isEmpty()) section.set("actions", Permissible.serializeActions(this.actions()));
        if (this.actions_requirements() != null && !this.actions_requirements().isEmpty()) {
            ConfigurationSection requirementsSection = section.createSection("actions-requirements");
            int index = 0;
            for (Requirement requirement : this.actions_requirements()) {
                requirement.serialize(requirementsSection.createSection("requirement-" + index++));
            }
        }

        if (this.subCommands() != null && !this.subCommands().isEmpty()) {
            ConfigurationSection subCommandsSection = section.createSection("sub-commands");
            for (Command subCommand : this.subCommands()) {
                subCommand.serialize(subCommandsSection.createSection(subCommand.command()));
            }
        }
    }
}
