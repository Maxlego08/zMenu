package fr.maxlego08.menu.button.loader;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.buttons.SlotButton;
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
	public Button load(YamlConfiguration configuration, String path) {
		List<String> slotsAsString = configuration.getStringList(path + "slots");
		List<Integer> slots = configuration.getIntegerList(path + "slots");
		if (slotsAsString.size() > 0) {
			for (String line : slotsAsString) {
				if (line.contains("-")) {
					try {
						String[] values = line.split("-");
						int from = Integer.valueOf(values[0]);
						int to = Integer.valueOf(values[1]) + 1;
						slots.addAll(IntStream.range(Math.min(from, to), Math.max(from, to)).boxed()
								.collect(Collectors.toList()));
					} catch (Exception ignored) {
						ignored.printStackTrace();
					}
				} else {
					try {
						slots.add(Integer.parseInt(line));
					} catch (NumberFormatException e) {
					}
				}

			}
		}
		return new ZSlotButton(slots);
	}

}
