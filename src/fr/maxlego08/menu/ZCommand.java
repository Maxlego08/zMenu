package fr.maxlego08.menu;

import fr.maxlego08.menu.api.command.Command;
import fr.maxlego08.menu.api.command.CommandArgument;
import fr.maxlego08.menu.api.requirement.Action;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class ZCommand implements Command {

    private final Plugin plugin;
    private final String command;
    private final List<String> aliases;
    private final String permission;
    private final String inventory;
    private final List<CommandArgument> arguments;
    private final List<Action> actions;
    private final List<Command> subCommands;

    private final String path;
    private final File file;


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
    public ZCommand(Plugin plugin, String command, List<String> aliases, String permission, String inventory,
                    List<CommandArgument> arguments, List<Action> actions, List<Command> subCommands, String path, File file) {
        super();
        this.plugin = plugin;
        this.command = command;
        this.aliases = aliases;
        this.permission = permission;
        this.inventory = inventory;
        this.arguments = arguments;
        this.actions = actions;
        this.subCommands = subCommands;
        this.path = path;
        this.file = file;
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public String getPermission() {
        return this.permission;
    }

    @Override
    public String getInventory() {
        return this.inventory;
    }

    @Override
    public String getCommand() {
        return this.command;
    }

    @Override
    public String toString() {
        return "ZCommand{" +
                "plugin=" + plugin +
                ", command='" + command + '\'' +
                ", aliases=" + aliases +
                ", permission='" + permission + '\'' +
                ", inventory='" + inventory + '\'' +
                ", arguments=" + arguments +
                ", actions=" + actions +
                ", subCommands=" + subCommands +
                ", path='" + path + '\'' +
                ", file=" + file +
                '}';
    }

    @Override
    public List<CommandArgument> getArguments() {
        return this.arguments;
    }

    @Override
    public List<String> getCommandArguments() {
        return this.arguments.stream().map(CommandArgument::getArgument).collect(Collectors.toList());
    }

    @Override
    public boolean hasArgument() {
        return this.arguments.size() > 0;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public List<Action> getActions() {
        return this.actions;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public List<Command> getSubCommands() {
        return subCommands;
    }
}
