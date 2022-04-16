package fr.maxlego08.menu.button.loader;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.buttons.NextButton;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.button.buttons.ZNextButton;

public class NextLoader implements ButtonLoader {

	private final Plugin plugin;
	private final InventoryManager manager;

	/**
	 * @param plugin
	 * @param manager
	 */
	public NextLoader(Plugin plugin, InventoryManager manager) {
		super();
		this.plugin = plugin;
		this.manager = manager;
	}

	@Override
	public Class<? extends Button> getButton() {
		return NextButton.class;
	}

	@Override
	public String getName() {
		return "next";
	}

	@Override
	public Plugin getPlugin() {
		return this.plugin;
	}

	@Override
	public Button load(YamlConfiguration configuration, String path) {
		return new ZNextButton(this.manager);
	}

}
