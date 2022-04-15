package fr.maxlego08.menu;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.event.events.ButtonLoadEvent;
import fr.maxlego08.menu.button.loader.NoneLoader;
import fr.maxlego08.menu.button.loader.SlotLoader;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.exceptions.InventoryFileNotFound;
import fr.maxlego08.menu.loader.InventoryLoader;
import fr.maxlego08.menu.zcore.enums.EnumInventory;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import fr.maxlego08.menu.zcore.utils.loader.Loader;
import fr.maxlego08.menu.zcore.utils.storage.Persist;

public class ZInventoryManager extends ZUtils implements InventoryManager {

	private final Map<String, List<Inventory>> inventories = new HashMap<String, List<Inventory>>();
	private final MenuPlugin plugin;

	/**
	 * @param plugin
	 */
	public ZInventoryManager(MenuPlugin plugin) {
		super();
		this.plugin = plugin;
	}

	@Override
	public void save(Persist persist) {
		// TODO
	}

	@Override
	public void load(Persist persist) {

		// Loading ButtonLoader
		// The first step will be to load the buttons in the plugin, so each
		// inventory will have the same list of buttons

		ButtonManager buttonManager = this.plugin.getButtonManager();
		buttonManager.register(new NoneLoader(this.plugin));
		buttonManager.register(new SlotLoader(this.plugin));

		ButtonLoadEvent event = new ButtonLoadEvent(buttonManager);
		event.callEvent();

		File folder = this.plugin.getDataFolder();
		if (folder.exists()) {
			folder.mkdir();
		}

		
		
	}

	@Override
	public Inventory loadInventory(Plugin plugin, String fileName) throws InventoryException {

		Loader<Inventory> loader = new InventoryLoader(this.plugin);

		File file = new File(plugin.getDataFolder(), fileName);
		if (!file.exists()) {
			throw new InventoryFileNotFound("Cannot find " + plugin.getDataFolder().getAbsolutePath() + "/" + fileName);
		}

		YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
		return loader.load(configuration, "", file);
	}

	@Override
	public Optional<Inventory> getInventory(String name) {
		return this.getInventories().stream().filter(i -> i.getFileName().equalsIgnoreCase(name)).findFirst();
	}

	@Override
	public Collection<Inventory> getInventories() {
		return this.inventories.values().stream().flatMap(List::stream).collect(Collectors.toList());
	}

	@Override
	public Collection<Inventory> getInventories(Plugin plugin) {
		return this.inventories.getOrDefault(plugin.getName(), new ArrayList<Inventory>());
	}

	@Override
	public void deleteInventory(Inventory inventory) {
		String pluginName = inventory.getPlugin().getName();
		List<Inventory> inventories = this.inventories.getOrDefault(pluginName, new ArrayList<Inventory>());
		inventories.remove(inventory);
		this.inventories.put(pluginName, inventories);
	}

	@Override
	public boolean deleteInventory(String name) {
		Optional<Inventory> optional = this.getInventory(name);
		if (optional.isPresent()) {
			this.deleteInventory(optional.get());
			return false;
		}
		return false;
	}

	@Override
	public void deleteInventories(Plugin plugin) {
		this.inventories.remove(plugin.getName());
	}

	@Override
	public void openInventory(Player player, Inventory inventory) {
		this.openInventory(player, inventory, 1, new ArrayList<>());
	}

	@Override
	public void openInventory(Player player, Inventory inventory, int page) {
		this.openInventory(player, inventory, page, new ArrayList<>());
	}

	@Override
	public void openInventory(Player player, Inventory inventory, int page, List<Inventory> oldInventories) {
		this.createInventory(this.plugin, player, EnumInventory.INVENTORY_DEFAULT, page, inventory, oldInventories);
	}

}
