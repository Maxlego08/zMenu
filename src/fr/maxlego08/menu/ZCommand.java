package fr.maxlego08.menu;

import fr.maxlego08.menu.api.command.Command;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;

public class ZCommand implements Command {

    private final Plugin plugin;
    private final String command;
    private final List<String> aliases;
    private final String permission;
    private final String inventory;
    private final List<String> arguments;

    private final String path;
    private final File file;


    /**
     * @param plugin The plugin where the command comes from
     * @param command Main command
     * @param aliases List of aliases
     * @param permission Command Permission
     * @param inventory Inventory name
     * @param arguments List of arguments
     * @param path file path
     * @param file File
     */
    public ZCommand(Plugin plugin, String command, List<String> aliases, String permission, String inventory,
                    List<String> arguments, String path, File file) {
        super();
        this.plugin = plugin;
        this.command = command;
        this.aliases = aliases;
        this.permission = permission;
        this.inventory = inventory;
        this.arguments = arguments;
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

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ZCommand [plugin=" + plugin + ", command=" + command + ", aliases=" + aliases + ", permission="
                + permission + ", inventory=" + inventory + "]";
    }

    @Override
    public List<String> getArguments() {
        return this.arguments;
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
    public File getFile() {
        return file;
    }

}
