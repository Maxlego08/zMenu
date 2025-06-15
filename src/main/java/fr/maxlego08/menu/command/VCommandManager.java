package fr.maxlego08.menu.command;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.command.commands.CommandInventory;
import fr.maxlego08.menu.api.utils.Message;
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

    private final ZMenuPlugin plugin;
    private final List<VCommand> commands = new ArrayList<>();

    public VCommandManager(ZMenuPlugin plugin) {
        this.plugin = plugin;
    }

    public void validCommands() {
        this.plugin.getLog().log("Loading " + getUniqueCommand() + " commands", LogType.SUCCESS);
        this.commandChecking();
    }

    public void registerCommand(VCommand command) {
        this.commands.add(command);
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
        message(this.plugin, sender, Message.COMMAND_NO_ARG);
        return true;
    }

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

    private boolean canExecute(String[] args, String cmd, VCommand command, int index) {
        if (index < 0 && command.getSubCommands().contains(cmd.toLowerCase())) return true;
        else if (index < 0) return false;
        else if (command.getSubCommands().contains(args[index].toLowerCase()))
            return canExecute(args, cmd, command.getParent(), index - 1);
        else return false;
    }

    private CommandType processRequirements(VCommand command, CommandSender sender, String[] strings) {

        if (!(sender instanceof Player) && !command.isConsoleCanUse()) {
            message(this.plugin, sender, Message.COMMAND_NO_CONSOLE);
            return CommandType.DEFAULT;
        }
        if (command.getPermission() == null || hasPermission(sender, command.getPermission())) {

            CommandType returnType = command.prePerform(this.plugin, sender, strings);
            if (returnType == CommandType.SYNTAX_ERROR)
                message(this.plugin, sender, Message.COMMAND_SYNTAX_ERROR, "%syntax%", command.getSyntax());
            return returnType;
        }

        if (command.getDenyMessage() != null) {
            messageWO(this.plugin, sender, command.getDenyMessage());
        } else {
            message(this.plugin, sender, Message.COMMAND_NO_PERMISSION);
        }
        return CommandType.DEFAULT;
    }

    public List<VCommand> getCommands() {
        return this.commands;
    }

    private int getUniqueCommand() {
        return (int) this.commands.stream().filter(command -> command.getParent() == null).count();
    }

    public void sendHelp(String commandString, CommandSender sender) {
        this.commands.forEach(command -> {
            if (isValid(command, commandString) && (command.getPermission() == null || hasPermission(sender, command.getPermission()))) {
                message(this.plugin, sender, Message.COMMAND_SYNTAX_HELP, "%syntax%", command.getSyntax(), "%description%", command.getDescription());
            }
        });
    }

    public boolean isValid(VCommand command, String commandString) {
        return command.getParent() != null ? isValid(command.getParent(), commandString) : command.getSubCommands().contains(commandString.toLowerCase());
    }

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

    private List<String> processTab(CommandSender sender, VCommand command, String[] args) {

        CommandType type = command.getTabCompleter();
        if (type.equals(CommandType.DEFAULT)) {

            String startWith = args[args.length - 1];

            List<String> tabCompleter = new ArrayList<>();
            for (VCommand vCommand : this.commands) {
                if ((vCommand.getParent() != null && vCommand.getParent() == command)) {
                    for (String subCommand : vCommand.getSubCommands()) {
                        if (vCommand.getPermission() == null || sender.hasPermission(vCommand.getPermission())) {
                            if (startWith.isEmpty() || subCommand.startsWith(startWith)) {
                                tabCompleter.add(subCommand);
                            }
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

    public void registerCommand(Plugin plugin, String string, VCommand vCommand, List<String> aliases) {
        try {
            PluginCommand command = constructor.newInstance(string, plugin);
            command.setExecutor(this);
            command.setTabCompleter(this);
            command.setAliases(aliases);
            if (vCommand.getPermission() != null) {
                command.setPermission(vCommand.getPermission());
            }

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
