package fr.maxlego08.menu;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

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

public class ZCommandManager extends ZUtils implements CommandManager {

	private final Map<String, List<Command>> commands = new HashMap<String, List<Command>>();
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
	public void unregistetCommands(Plugin plugin) {

		List<Command> commands = this.commands.getOrDefault(plugin.getName(), new ArrayList<Command>());
		Iterator<Command> iterator = commands.iterator();
		while (iterator.hasNext()) {
			Command command = iterator.next();
			JavaPlugin javaPlugin = (JavaPlugin) command.getPlugin();
			PluginCommand pluginCommand = javaPlugin.getCommand(command.getCommand());
			if (pluginCommand != null) {
				this.unRegisterBukkitCommand(javaPlugin, pluginCommand);
			}
		}

		this.commands.remove(plugin.getName());
	}

	@Override
	public void unregistetCommands(Command command) {
		JavaPlugin plugin = (JavaPlugin) command.getPlugin();
		List<Command> commands = this.commands.getOrDefault(plugin.getName(), new ArrayList<Command>());
		commands.remove(command);
		this.commands.put(plugin.getName(), commands);

		PluginCommand pluginCommand = plugin.getCommand(command.getCommand());
		if (pluginCommand != null) {
			this.unRegisterBukkitCommand(plugin, pluginCommand);
		}
	}

	@Override
	public void loadCommands() {

		this.unregistetCommands(this.plugin);

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
				Command command = loader.load(configuration, "commands." + key + ".");
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

}
