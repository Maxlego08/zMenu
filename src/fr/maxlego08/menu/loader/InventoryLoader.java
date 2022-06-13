package fr.maxlego08.menu.loader;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.ZInventory;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.exceptions.InventoryButtonException;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.exceptions.InventorySizeException;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import fr.maxlego08.menu.zcore.utils.loader.ItemStackLoader;
import fr.maxlego08.menu.zcore.utils.loader.Loader;

public class InventoryLoader extends ZUtils implements Loader<Inventory> {

	private final MenuPlugin plugin;

	/**
	 * @param plugin
	 */
	public InventoryLoader(MenuPlugin plugin) {
		super();
		this.plugin = plugin;
	}

	@Override
	public Inventory load(YamlConfiguration configuration, String path, Object... objects) throws InventoryException {

		File file = (File) objects[0];
		String name = configuration.getString("name");
		name = name == null ? "" : name;

		int size = configuration.getInt("size", 54);
		if (size % 9 != 0) {
			throw new InventorySizeException("Size " + size + " is not valid for inventory " + file.getAbsolutePath());
		}

		if (!configuration.contains("items") || !configuration.isConfigurationSection("items.")) {
			throw new InventoryButtonException(
					"Impossible to find the list of buttons for the " + file.getAbsolutePath() + " inventory!");
		}

		List<Button> buttons = new ArrayList<Button>();
		Loader<Button> loader = new ZButtonLoader(this.plugin, file, size);

		Loader<ItemStack> itemStackLoader = new ItemStackLoader(plugin.getInventoryManager());
		ItemStack itemStack = null;
		try {
			if (configuration.contains("fillItem")) {
				itemStack = itemStackLoader.load(configuration, "fillItem.");
			}
		} catch (Exception e) {
		}

		ConfigurationSection section = configuration.getConfigurationSection("items.");

		for (String buttonPath : section.getKeys(false)) {
			buttons.add(loader.load(configuration, "items." + buttonPath + ".", buttonPath));
		}

		String fileName = this.getFileNameWithoutExtension(file);

		ZInventory inventory;

		try {

			Class<? extends ZInventory> classz = (Class<? extends ZInventory>) objects[1];
			Constructor<? extends ZInventory> constructor = classz.getDeclaredConstructor(Plugin.class, String.class,
					String.class, int.class, List.class);
			Plugin plugin = (Plugin) objects[2];
			inventory = constructor.newInstance(plugin, name, fileName, size, buttons);

		} catch (Exception e) {
			e.printStackTrace();
			inventory = new ZInventory(this.plugin, name, fileName, size, buttons);
		}

		inventory.setFillItemStack(itemStack);
		return inventory;
	}

	@Override
	public void save(Inventory object, YamlConfiguration configuration, String path, Object... objects) {
	}

}
