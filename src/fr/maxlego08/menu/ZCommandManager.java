package fr.maxlego08.menu;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.command.Command;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.command.VCommandManager;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.loader.CommandLoader;
import fr.maxlego08.menu.save.Config;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.logger.Logger.LogType;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import fr.maxlego08.menu.zcore.utils.loader.Loader;
import fr.maxlego08.menu.zcore.utils.storage.Persist;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ZCommandManager extends ZUtils implements CommandManager {

    private final Map<String, List<Command>> commands = new HashMap<String, List<Command>>();
    private final Map<UUID, Map<String, String>> playerArguments = new HashMap<>();
    private final MenuPlugin plugin;

    /**
     * @param plugin
     */
    public ZCommandManager(MenuPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public void registerCommand(Command command) {

        VCommandManager manager = this.plugin.getVCommandManager();
        manager.registerCommand(command);

        List<Command> commands = this.commands.getOrDefault(command.getPlugin().getName(), new ArrayList<Command>());
        commands.add(command);
        this.commands.put(plugin.getName(), commands);

        if (Config.enableInformationMessage) {
            Logger.info("Command /" + command.getCommand() + " successfully register.", LogType.SUCCESS);
        }
    }

    @Override
    public Collection<Command> getCommands(Plugin plugin) {
        List<Command> commands = this.commands.getOrDefault(plugin.getName(), new ArrayList<Command>());
        return Collections.unmodifiableCollection(commands);
    }

    @Override
    public Collection<Command> getCommands() {
        return this.commands.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    @Override
    public void unregisterCommands(Plugin plugin) {

        List<Command> commands = this.commands.getOrDefault(plugin.getName(), new ArrayList<Command>());
        Iterator<Command> iterator = commands.iterator();
        while (iterator.hasNext()) {

            Command command = iterator.next();

            this.plugin.getVCommandManager().unregisterCommand(command);

            JavaPlugin javaPlugin = (JavaPlugin) command.getPlugin();
            PluginCommand pluginCommand = javaPlugin.getCommand(command.getCommand());
            if (pluginCommand != null) {
                this.unRegisterBukkitCommand(javaPlugin, pluginCommand);
            }
        }

        this.commands.remove(plugin.getName());
    }

    @Override
    public void unregisterCommands(Command command) {
        JavaPlugin plugin = (JavaPlugin) command.getPlugin();
        List<Command> commands = this.commands.getOrDefault(plugin.getName(), new ArrayList<Command>());
        commands.remove(command);
        this.commands.put(plugin.getName(), commands);

        this.plugin.getVCommandManager().unregisterCommand(command);

        PluginCommand pluginCommand = plugin.getCommand(command.getCommand());
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

        try {
            Files.walk(Paths.get(folder.getPath())).skip(1).map(Path::toFile).filter(File::isFile)
                    .filter(e -> e.getName().endsWith(".yml")).forEach(file -> {
                        this.loadCommand(this.plugin, file);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void loadCommand(Plugin plugin, File file) {

        Loader<Command> loader = new CommandLoader(plugin);
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

    @Override
    public void save(Persist persist) {

    }

    @Override
    public void load(Persist persist) {
        this.loadCommands();
    }

    @Override
    public Optional<Command> getCommand(Inventory inventory) {
        return this.getCommands(inventory.getPlugin()).stream().filter(e -> e.getInventory().equals(inventory))
                .findFirst();
    }

    @Override
    public void setPlayerArgument(Player player, String key, String value) {

        Map<String, String> arguments = this.playerArguments.getOrDefault(player.getUniqueId(), new HashMap<>());
        arguments.put(key, value);
        this.playerArguments.put(player.getUniqueId(), arguments);

    }

    @Override
    public Optional<String> getPlayerArgument(Player player, String key) {
        Map<String, String> arguments = this.playerArguments.getOrDefault(player.getUniqueId(), new HashMap<>());
        return Optional.ofNullable(arguments.getOrDefault(key, null));
    }

    @Override
    public Optional<Command> getCommand(String commandName) {
        return this.getCommands().stream().filter(e -> e.getCommand().equalsIgnoreCase(commandName)).findFirst();
    }

    @Override
    public boolean reload(Command command) {


        File file = command.getFile();

        if (!file.exists()) {
            return false;
        }

        this.unregisterCommands(command);
        String path = command.getPath();
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        Loader<Command> loader = new CommandLoader(this.plugin);
        try {
            Command newCommand = loader.load(configuration, path, file);
            this.registerCommand(newCommand);
        } catch (InventoryException e) {
            return false;
        }

        return true;
    }

}
