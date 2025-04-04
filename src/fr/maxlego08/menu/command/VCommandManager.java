package fr.maxlego08.menu.command;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.command.commands.CommandInventory;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.logger.Logger.LogType;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class VCommandManager extends ZUtils implements CommandExecutor, TabCompleter {

    private static CommandMap commandMap;
    private static Constructor<? extends PluginCommand> constructor;

    static {
        try {
            Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
        } catch (Exception ignored) {
        }
    }

    private final MenuPlugin plugin;
    private final List<VCommand> commands = new ArrayList<>();

    /**
     * F
     *
     * @param template
     */
    public VCommandManager(MenuPlugin template) {
        this.plugin = template;
    }

    /**
     * Valid commands
     */
    public void validCommands() {
        this.plugin.getLog().log("Loading " + getUniqueCommand() + " commands", LogType.SUCCESS);
        this.commandChecking();
    }

    /**
     * @param command
     * @return
     */
    public VCommand registerCommand(VCommand command) {
        this.commands.add(command);
        return command;
    }

    /**
     * @param string
     * @param command
     * @return
     */
    public VCommand registerCommand(String string, VCommand command) {
        commands.add(command.addSubCommand(string));
        this.plugin.getCommand(string).setExecutor(this);
        this.plugin.getCommand(string).setTabCompleter(this);
        return command;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        for (VCommand command : this.commands) {
            if (command.getSubCommands().contains(cmd.getName().toLowerCase())) {
                if ((args.length == 0 || command.isIgnoreParent()) && command.getParent() == null) {
                    CommandType type = processRequirements(command, sender, args);
                    if (!type.equals(CommandType.CONTINUE)) return true;
                }
            } else if (args.length >= 1 && command.getParent() != null && canExecute(args, cmd.getName().toLowerCase(), command)) {
                CommandType type = processRequirements(command, sender, args);
                if (!type.equals(CommandType.CONTINUE)) return true;
            }
        }
        message(sender, Message.COMMAND_NO_ARG);
        return true;
    }

    /**
     * @param args
     * @param cmd
     * @param command
     * @return true if can execute
     */
    private boolean canExecute(String[] args, String cmd, VCommand command) {
        for (int index = args.length - 1; index > -1; index--) {
            if (command.getSubCommands().contains(args[index].toLowerCase())) {
                if (command.isIgnoreArgs() && (command.getParent() == null || canExecute(args, cmd, command.getParent(), index - 1)))
                    return true;
                if (index < args.length - 1) return false;
                return canExecute(args, cmd, command.getParent(), index - 1);
            }
        }
        return false;
    }

    /**
     * @param args    - All arguments
     * @param cmd     - Current command
     * @param command - Current {@link VCommand}
     * @param index   - Index of the arguments
     * @return - true if all subcommands are valid
     */
    private boolean canExecute(String[] args, String cmd, VCommand command, int index) {
        if (index < 0 && command.getSubCommands().contains(cmd.toLowerCase())) return true;
        else if (index < 0) return false;
        else if (command.getSubCommands().contains(args[index].toLowerCase()))
            return canExecute(args, cmd, command.getParent(), index - 1);
        else return false;
    }

    /**
     * Allows you to process an order. First we check if the sender has the
     * permission or if the command has a permission. If yes then we execute the
     * command otherwise we send the message for the permission
     *
     * @param command - Object that contains the command
     * @param sender  - Person who executes the command
     * @param strings - Argument of the command
     * @return CommandType - Return of the command
     */
    private CommandType processRequirements(VCommand command, CommandSender sender, String[] strings) {

        if (!(sender instanceof Player) && !command.isConsoleCanUse()) {
            message(sender, Message.COMMAND_NO_CONSOLE);
            return CommandType.DEFAULT;
        }
        if (command.getPermission() == null || hasPermission(sender, command.getPermission())) {

            if (command.runAsync) {
                super.runAsync(this.plugin, () -> {
                    CommandType returnType = command.prePerform(this.plugin, sender, strings);
                    if (returnType == CommandType.SYNTAX_ERROR)
                        message(sender, Message.COMMAND_SYNTAX_ERROR, "%syntax%", command.getSyntax());
                });
                return CommandType.DEFAULT;
            }

            CommandType returnType = command.prePerform(this.plugin, sender, strings);
            if (returnType == CommandType.SYNTAX_ERROR)
                message(sender, Message.COMMAND_SYNTAX_ERROR, "%syntax%", command.getSyntax());
            return returnType;
        }

        if (command.getDenyMessage() != null) {
            messageWO(sender, command.getDenyMessage());
        } else {
            message(sender, Message.COMMAND_NO_PERMISSION);
        }
        return CommandType.DEFAULT;
    }

    public List<VCommand> getCommands() {
        return this.commands;
    }

    private int getUniqueCommand() {
        return (int) this.commands.stream().filter(command -> command.getParent() == null).count();
    }

    /**
     * @param commandString
     * @param sender
     */
    public void sendHelp(String commandString, CommandSender sender) {
        this.commands.forEach(command -> {
            if (isValid(command, commandString) && (command.getPermission() == null || hasPermission(sender, command.getPermission()))) {
                message(sender, Message.COMMAND_SYNTAX_HELP, "%syntax%", command.getSyntax(), "%description%", command.getDescription());
            }
        });
    }

    /**
     * @param command
     * @param commandString
     * @return
     */
    public boolean isValid(VCommand command, String commandString) {
        return command.getParent() != null ? isValid(command.getParent(), commandString) : command.getSubCommands().contains(commandString.toLowerCase());
    }

    /**
     * Allows you to check if all commands are correct If an command does not
     * have
     */
    private void commandChecking() {
        this.commands.forEach(command -> {
            if (command.sameSubCommands()) {
                Logger.info(command + " command to an argument similar to its parent command !", LogType.ERROR);
                this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
            }
        });
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String str, String[] args) {

        for (VCommand command : commands) {

            if (command.getSubCommands().contains(cmd.getName().toLowerCase())) {
                return processTab(sender, command, args);
            } else {
                String[] newArgs = Arrays.copyOf(args, args.length - 1);
                if (newArgs.length >= 1 && command.getParent() != null && canExecute(newArgs, cmd.getName().toLowerCase(), command)) {
                    return processTab(sender, command, args);
                }
            }
        }

        return null;
    }

    /**
     * Allows to execute the tab completion
     *
     * @param sender
     * @param command
     * @param args
     * @return list of arguments
     */
    private List<String> processTab(CommandSender sender, VCommand command, String[] args) {

        CommandType type = command.getTabCompleter();
        if (type.equals(CommandType.DEFAULT)) {

            String startWith = args[args.length - 1];

            List<String> tabCompleter = new ArrayList<>();
            for (VCommand vCommand : this.commands) {
                if ((vCommand.getParent() != null && vCommand.getParent() == command)) {
                    String cmd = vCommand.getSubCommands().get(0);
                    if (vCommand.getPermission() == null || sender.hasPermission(vCommand.getPermission())) {
                        if (startWith.length() == 0 || cmd.startsWith(startWith)) {
                            tabCompleter.add(cmd);
                        }
                    }
                }
            }
            return tabCompleter.size() == 0 ? null : tabCompleter;

        } else if (type.equals(CommandType.SUCCESS)) {
            return command.toTab(this.plugin, sender, args);
        }

        return null;
    }

    /**
     * Register spigot command without plugin.yml This method will allow to
     * register a command in the spigot without using the plugin.yml This saves
     * time and understanding, the plugin.yml file is clearer
     *
     * @param string   - Main command
     * @param vCommand - Command object
     * @param aliases  - Command aliases
     */
    public void registerCommand(Plugin plugin, String string, VCommand vCommand, List<String> aliases) {
        try {
            PluginCommand command = constructor.newInstance(string, plugin);
            command.setExecutor(this);
            command.setTabCompleter(this);
            command.setAliases(aliases);

            commands.add(vCommand.addSubCommand(string));
            vCommand.addSubCommand(aliases);

            if (!commandMap.register(command.getName(), plugin.getDescription().getName(), command)) {
                Logger.info("Unable to add the command " + vCommand.getSyntax(), LogType.ERROR);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void registerCommand(fr.maxlego08.menu.api.command.Command command) {
        this.registerCommand(command.getPlugin(), command.getCommand(), new CommandInventory(this.plugin, command, false), command.getAliases());
    }

    public void unregisterCommand(fr.maxlego08.menu.api.command.Command command) {

        Optional<VCommand> optional = this.commands.stream().filter(e -> e instanceof CommandInventory && ((CommandInventory) e).getCommand().equals(command)).findFirst();
        if (optional.isPresent()) {
            VCommand vCommand = optional.get();
            this.commands.remove(vCommand);
        }
    }

}
