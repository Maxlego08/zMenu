package fr.maxlego08.menu.button.loader;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.buttons.NoneButton;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.button.ZButton;

public class NoneLoader implements ButtonLoader {

	private final Plugin plugin;
	private final Class<? extends ZButton> classz;

	/**
	 * @param plugin
	 */
	public NoneLoader(Plugin plugin, Class<? extends ZButton> classz) {
		super();
		this.plugin = plugin;
		this.classz = classz;
	}

	@Override
	public Class<? extends Button> getButton() {
		return NoneButton.class;
	}

	@Override
	public String getName() {
		return "none";
	}

	@Override
	public Plugin getPlugin() {
		return this.plugin;
	}

	@Override
	public Button load(YamlConfiguration configuration, String path) {
		try {
			return this.classz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

}
