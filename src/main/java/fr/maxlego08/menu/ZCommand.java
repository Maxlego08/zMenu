package fr.maxlego08.menu;

import fr.maxlego08.menu.api.command.Command;
import fr.maxlego08.menu.api.command.CommandArgument;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Requirement;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @param plugin        The plugin where the command comes from
 * @param command       Main command
 * @param aliases       List of aliases
 * @param consoleCanUse If console can use the command
 * @param permission    Command Permission
 * @param inventory     Inventory name
 * @param arguments     List of arguments
 * @param actions       Actions
 * @param subCommands   Sub Commands
 * @param path          file path
 * @param file          File
 */
public record ZCommand(Plugin plugin, String command, List<String> aliases, boolean consoleCanUse, String permission, String inventory, List<CommandArgument> arguments,
                       List<Action> actions, List<Command> subCommands, List<Requirement> actions_requirements, String denyMessage, String path, File file) implements Command {

    @Override
    public @NotNull String toString() {
        return "ZCommand{" + "plugin=" + this.plugin + ", command='" + this.command + '\'' + ", aliases=" + this.aliases + ", permission='" + this.permission + '\'' + ", inventory='" + this.inventory + '\'' + ", arguments=" + this.arguments + ", actions=" + this.actions + ", subCommands=" + this.subCommands + ", actions-requirements=" + this.actions_requirements + ", path='" + this.path + '\'' + ", file=" + this.file + '}';
    }

    @Override
    public List<String> getCommandArguments() {
        List<String> args = new ArrayList<>(this.arguments.size());
        for (CommandArgument argument : this.arguments) {
            args.add(argument.getArgument());
        }
        return args;
    }

    @Override
    public boolean hasArgument() {
        return !this.arguments.isEmpty();
    }
}
