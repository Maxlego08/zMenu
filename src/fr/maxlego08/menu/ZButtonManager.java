package fr.maxlego08.menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.plugin.Plugin;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.exceptions.ButtonAlreadyRegisterException;
import fr.maxlego08.menu.zcore.utils.ZUtils;

public class ZButtonManager extends ZUtils implements ButtonManager {

	private final Map<String, List<ButtonLoader>> loaders = new HashMap<String, List<ButtonLoader>>();

	@Override
	public void register(ButtonLoader button) {

		Optional<ButtonLoader> optional = this.getLoader(button.getButton());
		if (optional.isPresent()) {
			ButtonLoader loader = optional.get();
			throw new ButtonAlreadyRegisterException("Button " + button.getButton().getName()
					+ " was already register in the " + loader.getPlugin().getName());
		}

		Plugin plugin = button.getPlugin();
		List<ButtonLoader> buttonLoaders = this.loaders.getOrDefault(plugin.getName(), new ArrayList<ButtonLoader>());
		buttonLoaders.add(button);
		this.loaders.put(plugin.getName(), buttonLoaders);
	}

	@Override
	public void unregister(ButtonLoader button) {
		String pluginName = button.getPlugin().getName();
		List<ButtonLoader> buttonLoaders = this.loaders.getOrDefault(pluginName, new ArrayList<ButtonLoader>());
		buttonLoaders.add(button);
		this.loaders.put(pluginName, buttonLoaders);
	}

	@Override
	public void unregisters(Plugin plugin) {
		this.loaders.remove(plugin);
	}

	@Override
	public Collection<ButtonLoader> getLoaders() {
		return this.loaders.values().stream().flatMap(List::stream).collect(Collectors.toList());
	}

	@Override
	public Collection<ButtonLoader> getLoaders(Plugin plugin) {
		return this.loaders.getOrDefault(plugin.getName(), new ArrayList<ButtonLoader>());
	}

	@Override
	public Optional<ButtonLoader> getLoader(Class<? extends Button> classz) {
		return this.getLoaders().stream().filter(e -> e.getButton().getClass().isAssignableFrom(classz)).findFirst();
	}

	@Override
	public Optional<ButtonLoader> getLoader(String name) {
		return this.getLoaders().stream().filter(e -> e.getName().equalsIgnoreCase(name)).findFirst();
	}

}
