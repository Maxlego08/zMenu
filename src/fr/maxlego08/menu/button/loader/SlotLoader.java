package fr.maxlego08.menu.button.loader;

import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.buttons.SlotButton;
import fr.maxlego08.menu.api.enums.PlaceholderAction;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.button.buttons.ZSlotButton;

public class SlotLoader implements ButtonLoader {

	private final Plugin plugin;

	/**
	 * @param plugin
	 */
	public SlotLoader(Plugin plugin) {
		super();
		this.plugin = plugin;
	}

	@Override
	public Class<? extends Button> getButton() {
		return SlotButton.class;
	}

	@Override
	public String getName() {
		return "none_slot";
	}

	@Override
	public Plugin getPlugin() {
		return this.plugin;
	}

	@Override
	public Button load(YamlConfiguration configuration, String path, String buttonName, ItemStack itemStack, int slot,
			boolean isPermanent, String permission, Button elseButton, PlaceholderAction action, String placeholder,
			String value) {
		List<Integer> slots = configuration.getIntegerList(path + "slots");
		return new ZSlotButton(buttonName, itemStack, slot, isPermanent, permission, elseButton, action, placeholder,
				value, slots);
	}

}
