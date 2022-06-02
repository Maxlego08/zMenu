package fr.maxlego08.menu.button.loader;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.button.ZButton;

public class NoneLoader implements ButtonLoader {

	private final Plugin plugin;
	private final Class<? extends ZButton> classz;
	private final String name;

	/**
	 * @param plugin
	 */
	public NoneLoader(Plugin plugin, Class<? extends ZButton> classz, String name) {
		super();
		this.plugin = plugin;
		this.classz = classz;
		this.name = name;
	}

	@Override
	public Class<? extends Button> getButton() {
		return this.classz;
	}

	@Override
	public String getName() {
		return this.name;
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
