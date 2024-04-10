package fr.maxlego08.menu.loader;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.ZCommand;
import fr.maxlego08.menu.ZCommandArgument;
import fr.maxlego08.menu.api.command.Command;
import fr.maxlego08.menu.api.command.CommandArgument;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.zcore.utils.loader.Loader;
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
    private final MenuPlugin menuPlugin;

    /**
     * @param plugin the plugin
     */
    public CommandLoader(Plugin plugin, MenuPlugin menuPlugin) {
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
        List<String> aliases = configuration.getStringList(path + "aliases");

        List<Action> commandActions = menuPlugin.getButtonManager().loadActions((List<Map<String, Object>>) configuration.getList(path + "actions", new ArrayList<>()), path, file);
        List<CommandArgument> arguments;
        List<?> listValues = configuration.getList(path + "arguments", new ArrayList<>());
        if (isListOfMap(listValues)) {
            arguments = configuration.getMapList(path + "arguments").stream().map(map -> {

                String argument = (String) map.get("name");
                String inventoryName = map.containsKey("inventory") ? (String) map.get("name") : null;
                boolean isRequired = !map.containsKey("isRequired") || (boolean) map.get("isRequired");
                List<Map<String, Object>> elements = map.containsKey("actions") ? (List<Map<String, Object>>) map.get("actions") : new ArrayList<>();
                List<Action> actions = menuPlugin.getButtonManager().loadActions(elements, path, file);
                List<String> autoCompletions = map.containsKey("auto-completion") ? (List<String>) map.get("auto-completion") : new ArrayList<>();

                System.out.println(autoCompletions + " - " + argument + " - " + actions);

                return new ZCommandArgument(argument, inventoryName, isRequired, actions, autoCompletions);
            }).collect(Collectors.toList());
        } else {
            arguments = configuration.getStringList(path + "arguments").stream().map(arg -> {
                String inventoryName = null;
                String argument = arg;
                boolean isRequired = true;
                if (arg.contains(",")) {
                    String[] values = arg.split(",");
                    argument = values[0];
                    if (values.length >= 2) isRequired = Boolean.parseBoolean(values[1]);
                    if (values.length == 3) inventoryName = values[2];
                }
                return new ZCommandArgument(argument, inventoryName, isRequired, new ArrayList<>(), new ArrayList<>());
            }).collect(Collectors.toList());
        }

        return new ZCommand(this.plugin, command, aliases, permission, inventory, arguments, commandActions, path, file);
    }

    @Override
    public void save(Command object, YamlConfiguration configuration, String path, File file, Object... objects) {

        configuration.set(path + "command", object.getCommand());
        configuration.set(path + "permission", object.getPermission());
        configuration.set(path + "inventory", object.getInventory());
        configuration.set(path + "aliases", object.getAliases());
        configuration.set(path + "arguments", object.getArguments());

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
