package fr.maxlego08.menu.button.loader;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.buttons.NoneButton;
import fr.maxlego08.menu.api.enums.PlaceholderAction;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.button.buttons.ZNoneButton;

public class NoneLoader implements ButtonLoader {

	private final Plugin plugin;

	/**
	 * @param plugin
	 */
	public NoneLoader(Plugin plugin) {
		super();
		this.plugin = plugin;
	}

	@Override
	public Class<? extends Button> getButton() {
		return NoneButton.class;
	}

	@Override
	public Plugin getPlugin() {
		return this.plugin;
	}

	@Override
	public Button load(YamlConfiguration configuration, String path, String buttonName, ItemStack itemStack, int slot,
			boolean isPermanent, String permission, Button elseButton, PlaceholderAction action, String placeholder,
			String value) {
		return new ZNoneButton(buttonName, itemStack, slot, isPermanent, permission, elseButton, action, placeholder,
				value);
	}

}
