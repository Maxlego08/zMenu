package fr.maxlego08.menu;

import fr.maxlego08.menu.api.command.Command;
import fr.maxlego08.menu.api.command.CommandArgument;
import fr.maxlego08.menu.api.requirement.Action;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @param plugin      The plugin where the command comes from
 * @param command     Main command
 * @param aliases     List of aliases
 * @param permission  Command Permission
 * @param inventory   Inventory name
 * @param arguments   List of arguments
 * @param actions     Actions
 * @param subCommands Sub Commands
 * @param path        file path
 * @param file        File
 */
public record ZCommand(Plugin plugin, String command, List<String> aliases, String permission, String inventory, List<CommandArgument> arguments,
                       List<Action> actions, List<Command> subCommands, String denyMessage, String path, File file) implements Command {

    @Override
    public @NotNull String toString() {
        return "ZCommand{" + "plugin=" + plugin + ", command='" + command + '\'' + ", aliases=" + aliases + ", permission='" + permission + '\'' + ", inventory='" + inventory + '\'' + ", arguments=" + arguments + ", actions=" + actions + ", subCommands=" + subCommands + ", path='" + path + '\'' + ", file=" + file + '}';
    }

    @Override
    public List<String> getCommandArguments() {
        return this.arguments.stream().map(CommandArgument::getArgument).collect(Collectors.toList());
    }

    @Override
    public boolean hasArgument() {
        return !this.arguments.isEmpty();
    }
}
