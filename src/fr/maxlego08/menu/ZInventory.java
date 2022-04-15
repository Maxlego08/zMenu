package fr.maxlego08.menu;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.plugin.Plugin;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.PlaceholderButton;

public class ZInventory implements Inventory {

	private final Plugin plugin;
	private final String name;
	private final String fileName;
	private final int size;
	private final List<Button> buttons;

	/**
	 * @param plugin
	 * @param name
	 * @param fileName
	 * @param size
	 * @param buttons
	 */
	public ZInventory(Plugin plugin, String name, String fileName, int size, List<Button> buttons) {
		super();
		this.plugin = plugin;
		this.name = name;
		this.fileName = fileName;
		this.size = size;
		this.buttons = buttons;
	}

	@Override
	public int size() {
		return this.size;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getFileName() {
		return this.fileName;
	}

	@Override
	public Collection<Button> getButtons() {
		return Collections.unmodifiableCollection(this.buttons);
	}

	@Override
	public <T extends Button> List<T> getButtons(Class<T> type) {
		return this.getButtons().stream().filter(e -> type.isAssignableFrom(e.getClass())).map(e -> (T) e)
				.collect(Collectors.toList());
	}

	@Override
	public Plugin getPlugin() {
		return this.plugin;
	}

	@Override
	public int getMaxPage(Object... objects) {
		Optional<Integer> optional = this.buttons.stream().map(Button::getSlot).max(Integer::compare);
		if (optional.isPresent()) {
			int maxSlot = optional.get();
			return (maxSlot / this.size) + 1;
		}
		return 1;
	}

	@Override
	public List<PlaceholderButton> sortButtons(int page, Object... objects) {
		return this.buttons.stream().filter(button -> {
			int slot = button.getRealSlot(this.size, page);
			return (slot >= 0 && slot < this.size) || button.isPermament();
		}).map(button -> button.toButton(PlaceholderButton.class)).collect(Collectors.toList());
	}

}
