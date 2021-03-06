package fr.maxlego08.menu;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.event.events.ButtonLoadEvent;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import fr.maxlego08.menu.button.buttons.ZNoneButton;
import fr.maxlego08.menu.button.loader.BackLoader;
import fr.maxlego08.menu.button.loader.HomeLoader;
import fr.maxlego08.menu.button.loader.NextLoader;
import fr.maxlego08.menu.button.loader.NoneLoader;
import fr.maxlego08.menu.button.loader.PerformLoader;
import fr.maxlego08.menu.button.loader.PreviousLoader;
import fr.maxlego08.menu.button.loader.SlotLoader;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.exceptions.InventoryFileNotFound;
import fr.maxlego08.menu.loader.InventoryLoader;
import fr.maxlego08.menu.save.Config;
import fr.maxlego08.menu.zcore.enums.EnumInventory;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.logger.Logger.LogType;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import fr.maxlego08.menu.zcore.utils.loader.Loader;
import fr.maxlego08.menu.zcore.utils.storage.Persist;

public class ZInventoryManager extends ZUtils implements InventoryManager {

	private final Map<String, List<Inventory>> inventories = new HashMap<String, List<Inventory>>();
	private final List<MaterialLoader> loaders = new ArrayList<>();
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

		this.loadButtons();
		this.loadInventories();

	}

	@Override
	public Inventory loadInventory(Plugin plugin, File file) throws InventoryException {
		return this.loadInventory(plugin, file, ZInventory.class);
	}

	@Override
	public Inventory loadInventory(Plugin plugin, String fileName) throws InventoryException {
		return this.loadInventory(plugin, fileName, ZInventory.class);
	}

	@Override
	public Inventory loadInventory(Plugin plugin, String fileName, Class<? extends Inventory> classz)
			throws InventoryException {

		File file = new File(plugin.getDataFolder(), fileName);
		if (!file.exists()) {
			throw new InventoryFileNotFound("Cannot find " + plugin.getDataFolder().getAbsolutePath() + "/" + fileName);
		}

		return this.loadInventory(plugin, file, classz);
	}

	@Override
	public Inventory loadInventory(Plugin plugin, File file, Class<? extends Inventory> classz)
			throws InventoryException {

		Loader<Inventory> loader = new InventoryLoader(this.plugin);
		YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
		Inventory inventory = loader.load(configuration, "", file, classz, plugin);

		List<Inventory> inventories = this.inventories.getOrDefault(plugin.getName(), new ArrayList<Inventory>());
		inventories.add(inventory);
		this.inventories.put(plugin.getName(), inventories);

		if (Config.enableInformationMessage) {
			Logger.info(file.getPath() + " loaded successfully !", LogType.INFO);
		}

		return inventory;
	}

	@Override
	public Optional<Plugin> getPluginIgnoreCase(String pluginName) {
		return Arrays.asList(Bukkit.getPluginManager().getPlugins()).stream().filter(e -> {
			return pluginName != null && e.getName().equalsIgnoreCase(pluginName);
		}).findFirst();
	}

	@Override
	public Optional<Inventory> getInventory(String name) {
		return this.getInventories().stream().filter(i -> name != null && i.getFileName().equalsIgnoreCase(name))
				.findFirst();
	}

	@Override
	public Optional<Inventory> getInventory(Plugin plugin, String name) {
		return this.getInventories(plugin).stream().filter(i -> name != null && i.getFileName().equalsIgnoreCase(name))
				.findFirst();
	}

	@Override
	public Optional<Inventory> getInventory(String pluginName, String name) {
		Optional<Plugin> optional = this.getPluginIgnoreCase(pluginName);
		return !optional.isPresent() || name == null ? Optional.empty() : this.getInventory(optional.get(), name);
	}

	@Override
	public Collection<Inventory> getInventories() {
		return this.inventories.values().stream().flatMap(List::stream).collect(Collectors.toList());
	}

	@Override
	public Collection<Inventory> getInventories(Plugin plugin) {
		return plugin == null ? new ArrayList<>()
				: this.inventories.getOrDefault(plugin.getName(), new ArrayList<Inventory>());
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

	@Override
	public void openInventory(Player player, Inventory inventory, int page, Inventory... inventories) {
		List<Inventory> oldInventories = new ArrayList<>();
		for (Inventory i : inventories) {
			oldInventories.add(i);
		}
		this.openInventory(player, inventory, page, oldInventories);
	}

	@Override
	public void loadButtons() {

		// Loading ButtonLoader
		// The first step will be to load the buttons in the plugin, so each
		// inventory will have the same list of buttons

		ButtonManager buttonManager = this.plugin.getButtonManager();

		buttonManager.register(new NoneLoader(this.plugin, ZNoneButton.class, "none"));
		buttonManager.register(new SlotLoader(this.plugin));
		buttonManager.register(new PerformLoader(this.plugin));
		buttonManager.register(new fr.maxlego08.menu.button.loader.InventoryLoader(this.plugin, this));
		buttonManager.register(new BackLoader(this.plugin, this));
		buttonManager.register(new HomeLoader(this.plugin, this));
		buttonManager.register(new NextLoader(this.plugin, this));
		buttonManager.register(new PreviousLoader(this.plugin, this));

		ButtonLoadEvent event = new ButtonLoadEvent(buttonManager);
		event.callEvent();
	}

	@Override
	public void loadInventories() {

		// Check if file exist
		File folder = new File(this.plugin.getDataFolder(), "inventories");
		if (!folder.exists()) {
			folder.mkdir();
		}

		// Load inventories
		try {
			Files.walk(Paths.get(folder.getPath())).skip(1).map(Path::toFile).filter(File::isFile)
					.filter(e -> e.getName().endsWith(".yml")).forEach(file -> {
						try {
							this.loadInventory(this.plugin, file);
						} catch (InventoryException e1) {
							e1.printStackTrace();
						}
					});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean registerMaterialLoader(MaterialLoader materialLoader) {

		Optional<MaterialLoader> optional = this.getMaterialLoader(materialLoader.getKey());
		if (optional.isPresent()) {
			return false;
		}

		this.loaders.add(materialLoader);
		return true;
	}

	@Override
	public Optional<MaterialLoader> getMaterialLoader(String key) {
		return this.loaders.stream().filter(e -> e.getKey().equalsIgnoreCase(key)).findFirst();
	}

	@Override
	public Collection<MaterialLoader> getMaterialLoader() {
		return Collections.unmodifiableCollection(this.loaders);
	}

	@Override
	public void openInventory(Player player, Plugin plugin, String inventoryName) {

		Optional<Inventory> optional = this.getInventory(plugin, inventoryName);

		if (!optional.isPresent()) {
			player.closeInventory();
			message(player, Message.INVENTORY_NOT_FOUND, "%name%", inventoryName, "%toName%", inventoryName, "%plugin%",
					plugin.getName());
			return;
		}

		this.openInventory(player, optional.get());
	}

	@Override
	public void openInventory(Player player, String pluginName, String inventoryName) {

		Optional<Inventory> optional = this.getInventory(pluginName, inventoryName);

		if (!optional.isPresent()) {
			player.closeInventory();
			message(player, Message.INVENTORY_NOT_FOUND, "%name%", inventoryName, "%toName%", inventoryName, "%plugin%",
					pluginName);
			return;
		}

		this.openInventory(player, optional.get());
	}

	@Override
	public void openInventory(Player player, String inventoryName) {

		Optional<Inventory> optional = this.getInventory(inventoryName);

		if (!optional.isPresent()) {
			player.closeInventory();
			message(player, Message.INVENTORY_NOT_FOUND, "%name%", inventoryName, "%toName%", inventoryName, "%plugin%",
					this.plugin.getName());
			return;
		}

		this.openInventory(player, optional.get());
	}

	@Override
	public void reloadInventory(Inventory inventory) {

		this.deleteInventory(inventory);

		/*
		 * CommandManager commandManager = this.plugin.getCommandManager();
		 * Optional<Command> optional = commandManager.getCommand(inventory); if
		 * (optional.isPresent()) { Command command = optional.get();
		 * commandManager.unregistetCommands(command); }
		 */

		try {
			this.loadInventory(inventory.getPlugin(), inventory.getFile());
		} catch (InventoryException e) {
			e.printStackTrace();
		}

	}

}
