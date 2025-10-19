package fr.maxlego08.menu;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.command.Command;
import fr.maxlego08.menu.api.command.CommandArgumentValidator;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.command.VCommandManager;
import fr.maxlego08.menu.loader.CommandLoader;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.logger.Logger.LogType;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import fr.maxlego08.menu.zcore.utils.storage.Persist;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ZCommandManager extends ZUtils implements CommandManager {

    private final Map<String, List<Command>> commands = new HashMap<>();
    private final Map<UUID, Map<String, String>> playerArguments = new HashMap<>();
    private final List<CommandArgumentValidator> commandArgumentValidators = new ArrayList<>();
    private final ZMenuPlugin plugin;

    public ZCommandManager(ZMenuPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public void registerCommand(Command command) {

        VCommandManager manager = this.plugin.getVCommandManager();
        manager.registerCommand(command);

        List<Command> commands = this.commands.getOrDefault(command.plugin().getName(), new ArrayList<>());
        commands.add(command);
        this.commands.put(command.plugin().getName(), commands);

        if (Config.enableInformationMessage) {
            Logger.info("Command /" + command.command() + " successfully register.", LogType.SUCCESS);
        }
    }

    @Override
    public Collection<Command> getCommands(Plugin plugin) {
        List<Command> commands = this.commands.getOrDefault(plugin.getName(), new ArrayList<>());
        return Collections.unmodifiableCollection(commands);
    }

    @Override
    public Collection<Command> getCommands() {
        List<Command> allCommands = new ArrayList<>();
        for (List<Command> commandList : this.commands.values()) {
            allCommands.addAll(commandList);
        }
        return allCommands;
    }

    @Override
    public void unregisterCommands(Plugin plugin) {

        List<Command> commands = this.commands.getOrDefault(plugin.getName(), new ArrayList<>());
        for (Command command : commands) {

            this.plugin.getVCommandManager().unregisterCommand(command);

            JavaPlugin javaPlugin = (JavaPlugin) command.plugin();
            PluginCommand pluginCommand = javaPlugin.getCommand(command.command());
            if (pluginCommand != null) {
                this.unRegisterBukkitCommand(javaPlugin, pluginCommand);
            }
        }

        this.commands.remove(plugin.getName());
    }

    @Override
    public void unregisterCommands(Command command) {
        JavaPlugin plugin = (JavaPlugin) command.plugin();
        List<Command> commands = this.commands.getOrDefault(plugin.getName(), new ArrayList<>());
        commands.remove(command);
        this.commands.put(plugin.getName(), commands);

        this.plugin.getVCommandManager().unregisterCommand(command);

        PluginCommand pluginCommand = plugin.getCommand(command.command());
        if (pluginCommand != null) {
            this.unRegisterBukkitCommand(plugin, pluginCommand);
        }
    }

    @Override
    public void loadCommands() {

        this.unregisterCommands(this.plugin);

        // Check if file exist
        File folder = new File(this.plugin.getDataFolder(), "commands");
        if (!folder.exists()) {
            folder.mkdir();
        }

        try (var files = Files.walk(Paths.get(folder.getPath()))) {
            files.skip(1).map(Path::toFile)
                    .filter(File::isFile)
                    .filter(e -> e.getName().endsWith(".yml"))
                    .forEach(file -> this.loadCommand(this.plugin, file));
        } catch (IOException e) {
            e.printStackTrace();
        }


        executeCraftServerSyncCommands();
    }

    public void executeCraftServerSyncCommands() {
        try {
            Object craftServer = getCraftServerInstance();
            Method syncCommandsMethod = craftServer.getClass().getDeclaredMethod("syncCommands");
            syncCommandsMethod.setAccessible(true);
            syncCommandsMethod.invoke(craftServer);
        } catch (Exception ignored) {
        }
    }

    private Object getCraftServerInstance() throws ClassNotFoundException, IllegalAccessException {
        Class<?> craftServerClass = Class.forName(Bukkit.getServer().getClass().getCanonicalName());
        return craftServerClass.cast(Bukkit.getServer());
    }

    @Override
    public void loadCommand(Plugin plugin, File file) {

        Loader<Command> loader = new CommandLoader(plugin, this.plugin);
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        if (!configuration.contains("commands") || !configuration.isConfigurationSection("commands.")) {
            return;
        }

        for (String key : configuration.getConfigurationSection("commands.").getKeys(false)) {
            try {
                Command command = loader.load(configuration, "commands." + key + ".", file);
                this.registerCommand(command);
            } catch (InventoryException e) {
                e.printStackTrace();
            }
        }
    }

    public void save(Persist persist) {

    }

    public void load(Persist persist) {
        this.loadCommands();
    }

    @Override
    public Optional<Command> getCommand(Inventory inventory) {
        for (Command command : this.getCommands(inventory.getPlugin())) {
            if (command.inventory().equals(inventory)) {
                return Optional.of(command);
            }
        }
        return Optional.empty();
    }

    @Override
    public void setPlayerArgument(Player player, String key, String value) {

        Map<String, String> arguments = this.playerArguments.getOrDefault(player.getUniqueId(), new HashMap<>());
        arguments.put(key, value);
        this.playerArguments.put(player.getUniqueId(), arguments);

    }

    @Override
    public Optional<String> getPlayerArgument(UUID uuid, String key) {
        Map<String, String> arguments = this.playerArguments.getOrDefault(uuid, new HashMap<>());
        return Optional.ofNullable(arguments.getOrDefault(key, null));
    }

    @Override
    public Optional<String> getPlayerArgument(Player player, String key) {
        return getPlayerArgument(player.getUniqueId(), key);
    }

    @Override
    public Optional<Command> getCommand(String commandName) {
        for (Command command : this.getCommands()) {
            if (command.command().equalsIgnoreCase(commandName)) {
                return Optional.of(command);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean reload(Command command) {


        File file = command.file();

        if (!file.exists()) {
            return false;
        }

        this.unregisterCommands(command);
        String path = command.path();
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        Loader<Command> loader = new CommandLoader(this.plugin, this.plugin);
        try {
            Command newCommand = loader.load(configuration, path, file);
            this.registerCommand(newCommand);
        } catch (InventoryException e) {
            return false;
        }

        return true;
    }

    @Override
    public void registerArgumentValidator(CommandArgumentValidator commandArgumentValidator) {
        this.commandArgumentValidators.add(commandArgumentValidator);
    }

    @Override
    public void unregisterArgumentValidator(CommandArgumentValidator commandArgumentValidator) {
        this.commandArgumentValidators.remove(commandArgumentValidator);
    }

    @Override
    public List<CommandArgumentValidator> getArgumentValidators() {
        return this.commandArgumentValidators;
    }

    @Override
    public Optional<CommandArgumentValidator> getArgumentValidator(String argumentTypeName) {
        for (CommandArgumentValidator validator : this.commandArgumentValidators) {
            if (validator.getType().equalsIgnoreCase(argumentTypeName)) {
                return Optional.of(validator);
            }
        }
        return Optional.empty();
    }
}
