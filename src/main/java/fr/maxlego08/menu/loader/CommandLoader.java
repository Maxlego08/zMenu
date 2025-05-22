package fr.maxlego08.menu.loader;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.ZCommand;
import fr.maxlego08.menu.ZCommandArgument;
import fr.maxlego08.menu.api.command.Command;
import fr.maxlego08.menu.api.command.CommandArgument;
import fr.maxlego08.menu.api.command.CommandArgumentType;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.utils.Loader;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandLoader implements Loader<Command> {

    private final Plugin plugin;
    private final ZMenuPlugin menuPlugin;

    /**
     * @param plugin the plugin
     */
    public CommandLoader(Plugin plugin, ZMenuPlugin menuPlugin) {
        super();
        this.plugin = plugin;
        this.menuPlugin = menuPlugin;
    }

    @Override
    public Command load(YamlConfiguration configuration, String path, Object... args) throws InventoryException {

        File file = (File) args[0];
        String command = configuration.getString(path + "command");
        String permission = configuration.getString(path + "permission");
        String inventory = configuration.getString(path + "inventory");
        String denyMessage = configuration.getString(path + "deny-message", null);
        List<String> aliases = configuration.getStringList(path + "aliases");

        List<Action> commandActions = menuPlugin.getButtonManager().loadActions((List<Map<String, Object>>) configuration.getList(path + "actions", new ArrayList<>()), path, file);
        List<CommandArgument> arguments = new ArrayList<>();
        List<?> listValues = configuration.getList(path + "arguments", new ArrayList<>());
        if (isListOfMap(listValues)) {
            arguments = configuration.getMapList(path + "arguments").stream().map(map -> {

                String argument = (String) map.get("name");
                String inventoryName = map.containsKey("inventory") ? (String) map.get("name") : null;
                boolean isRequired = map.containsKey("isRequired") ? (boolean) map.get("isRequired") : map.containsKey("is-required") ? (boolean) map.get("is-required") : true;
                boolean performMainAction = map.containsKey("performMainAction") ? (boolean) map.get("performMainAction") : map.containsKey("perform-main-action") ? (boolean) map.get("perform-main-action") : true;
                CommandArgumentType argumentType = map.containsKey("argument-type") ? CommandArgumentType.valueOf((String) map.get("argument-type")) : CommandArgumentType.STRING;

                List<Map<String, Object>> elements = map.containsKey("actions") ? (List<Map<String, Object>>) map.get("actions") : new ArrayList<>();
                List<Action> actions = menuPlugin.getButtonManager().loadActions(elements, path, file);
                List<String> autoCompletions = map.containsKey("auto-completion") ? (List<String>) map.get("auto-completion") : new ArrayList<>();
                String defaultValue = map.containsKey("defaultValue") ? (String) map.get("defaultValue") : map.containsKey("default-value") ? (String) map.get("default-value") : null;

                return new ZCommandArgument(argumentType, argument, inventoryName, isRequired, performMainAction, actions, autoCompletions, defaultValue);
            }).collect(Collectors.toList());
        } else {
            List<String> strings = configuration.getStringList(path + "arguments");
            if (!strings.isEmpty()) {
                this.plugin.getLogger().warning("/" + command + " (in file " + file.getPath() + ") command uses the old argument system. Please update your configuration ! (https://docs.zmenu.dev/configurations/commands) Your command will still work properly but it is advisable to update it.");
                arguments = configuration.getStringList(path + "arguments").stream().map(arg -> {
                    String inventoryName = null;
                    String argument = arg;
                    boolean isRequired = true;
                    boolean performMainAction = true;
                    if (arg.contains(",")) {
                        String[] values = arg.split(",");
                        argument = values[0];
                        if (values.length >= 2) isRequired = Boolean.parseBoolean(values[1]);
                        if (values.length == 3) inventoryName = values[2];
                    }
                    return new ZCommandArgument(CommandArgumentType.STRING, argument, inventoryName, isRequired, performMainAction, new ArrayList<>(), new ArrayList<>(), null);
                }).collect(Collectors.toList());
            }
        }

        List<Command> subCommands = new ArrayList<>();
        ConfigurationSection configurationSection = configuration.getConfigurationSection(path + "sub-commands");
        if (configurationSection != null) {
            for (String key : configurationSection.getKeys(false)) {
                Command subCommand = load(configuration, path + "sub-commands." + key + ".", args);
                if (subCommand != null && subCommand.getCommand() != null) {
                    subCommands.add(subCommand);
                }
            }
        }

        return new ZCommand(this.plugin, command, aliases, permission, inventory, arguments, commandActions, subCommands, denyMessage, path, file);
    }

    @Override
    public void save(Command object, YamlConfiguration configuration, String path, File file, Object... objects) {

        configuration.set(path + "command", object.getCommand());
        configuration.set(path + "permission", object.getPermission());
        configuration.set(path + "inventory", object.getInventory());
        configuration.set(path + "aliases", object.getAliases());
        configuration.set(path + "arguments", object.getArguments());
        configuration.set(path + "deny-message", object.getDenyMessage());

        try {
            configuration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean isListOfMap(List<?> list) {
        for (Object item : list) {
            if (!(item instanceof Map)) {
                return false;
            }
        }
        return !list.isEmpty();
    }

}
