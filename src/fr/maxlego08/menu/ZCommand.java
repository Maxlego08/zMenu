package fr.maxlego08.menu;

import java.util.List;

import org.bukkit.plugin.Plugin;

import fr.maxlego08.menu.api.command.Command;

public class ZCommand implements Command {

	private final Plugin plugin;
	private final String command;
	private final List<String> aliases;
	private final String permission;
	private final String inventory;

	/**
	 * @param plugin
	 * @param string
	 * @param aliases
	 * @param permission
	 * @param inventory
	 */
	public ZCommand(Plugin plugin, String command, List<String> aliases, String permission, String inventory) {
		super();
		this.plugin = plugin;
		this.command = command;
		this.aliases = aliases;
		this.permission = permission;
		this.inventory = inventory;
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

}
