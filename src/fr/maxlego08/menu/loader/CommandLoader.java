package fr.maxlego08.menu.loader;

import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import fr.maxlego08.menu.ZCommand;
import fr.maxlego08.menu.api.command.Command;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.zcore.utils.loader.Loader;

public class CommandLoader implements Loader<Command> {

	private final Plugin plugin;

	/**
	 * @param plugin
	 */
	public CommandLoader(Plugin plugin) {
		super();
		this.plugin = plugin;
	}

	@Override
	public Command load(YamlConfiguration configuration, String path, Object... objects) throws InventoryException {

		String command = configuration.getString(path + "command");
		String permission = configuration.getString(path + "permission");
		String inventory = configuration.getString(path + "inventory");
		List<String> aliases = configuration.getStringList(path + "aliases");

		return new ZCommand(this.plugin, command, aliases, permission, inventory);
	}

	@Override
	public void save(Command object, YamlConfiguration configuration, String path, Object... objects) {

	}

}
