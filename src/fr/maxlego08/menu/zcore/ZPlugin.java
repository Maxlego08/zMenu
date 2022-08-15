package fr.maxlego08.menu.zcore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.maxlego08.menu.command.VCommandManager;
import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.exceptions.ListenerNullException;
import fr.maxlego08.menu.inventory.VInventory;
import fr.maxlego08.menu.inventory.VInventoryManager;
import fr.maxlego08.menu.listener.ListenerAdapter;
import fr.maxlego08.menu.placeholder.LocalPlaceholder;
import fr.maxlego08.menu.placeholder.Placeholder;
import fr.maxlego08.menu.zcore.enums.EnumInventory;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.logger.Logger.LogType;
import fr.maxlego08.menu.zcore.utils.gson.DataAdapter;
import fr.maxlego08.menu.zcore.utils.gson.LocationAdapter;
import fr.maxlego08.menu.zcore.utils.gson.PotionEffectAdapter;
import fr.maxlego08.menu.zcore.utils.plugins.Plugins;
import fr.maxlego08.menu.zcore.utils.storage.Persist;
import fr.maxlego08.menu.zcore.utils.storage.Saveable;

public abstract class ZPlugin extends JavaPlugin {

	private final Logger log = new Logger(this.getDescription().getFullName());
	private final List<Saveable> savers = new ArrayList<>();
	private final List<ListenerAdapter> listenerAdapters = new ArrayList<>();

	private Gson gson;
	private Persist persist;
	private long enableTime;

	protected VCommandManager zcommandManager;
	protected VInventoryManager vinventoryManager;

	protected void preEnable() {

		LocalPlaceholder.getInstance().setPlugin((MenuPlugin) this);

		this.enableTime = System.currentTimeMillis();

		this.log.log("=== ENABLE START ===");
		this.log.log("Plugin Version V<&>c" + getDescription().getVersion(), LogType.INFO);

		this.getDataFolder().mkdirs();

		this.gson = getGsonBuilder().create();
		this.persist = new Persist(this);

		Placeholder.register();
	}

	protected void postEnable() {

		if (this.zcommandManager != null) {
			this.zcommandManager.validCommands();
		}

		this.log.log(
				"=== ENABLE DONE <&>7(<&>6" + Math.abs(enableTime - System.currentTimeMillis()) + "ms<&>7) <&>e===");

	}

	protected void preDisable() {
		this.enableTime = System.currentTimeMillis();
		this.log.log("=== DISABLE START ===");
	}

	protected void postDisable() {
		this.log.log(
				"=== DISABLE DONE <&>7(<&>6" + Math.abs(enableTime - System.currentTimeMillis()) + "ms<&>7) <&>e===");

	}

	/**
	 * Build gson
	 * 
	 * @return
	 */
	public GsonBuilder getGsonBuilder() {
		return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls()
				.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE)
				.registerTypeAdapter(PotionEffect.class, new PotionEffectAdapter(this))
				.registerTypeAdapter(Data.class, new DataAdapter(this))
				.registerTypeAdapter(Location.class, new LocationAdapter(this));
	}

	/**
	 * Add a listener
	 * 
	 * @param listener
	 */
	public void addListener(Listener listener) {
		if (listener instanceof Saveable)
			this.addSave((Saveable) listener);
		Bukkit.getPluginManager().registerEvents(listener, this);
	}

	/**
	 * Add a listener from ListenerAdapter
	 * 
	 * @param adapter
	 */
	public void addListener(ListenerAdapter adapter) {
		if (adapter == null)
			throw new ListenerNullException("Warning, your listener is null");
		if (adapter instanceof Saveable)
			this.addSave((Saveable) adapter);
		this.listenerAdapters.add(adapter);
	}

	/**
	 * Add a Saveable
	 * 
	 * @param saver
	 */
	public void addSave(Saveable saver) {
		this.savers.add(saver);
	}

	/**
	 * Get logger
	 * 
	 * @return loggers
	 */
	public Logger getLog() {
		return this.log;
	}

	/**
	 * Get gson
	 * 
	 * @return {@link Gson}
	 */
	public Gson getGson() {
		return gson;
	}

	public Persist getPersist() {
		return persist;
	}

	/**
	 * Get all saveables
	 * 
	 * @return savers
	 */
	public List<Saveable> getSavers() {
		return savers;
	}

	/**
	 * 
	 * @param classz
	 * @return
	 */
	protected <T> T getProvider(Class<T> classz) {
		RegisteredServiceProvider<T> provider = getServer().getServicesManager().getRegistration(classz);
		if (provider == null) {
			log.log("Unable to retrieve the provider " + classz.toString(), LogType.WARNING);
			return null;
		}
		return provider.getProvider() != null ? (T) provider.getProvider() : null;
	}

	/**
	 * 
	 * @return listenerAdapters
	 */
	public List<ListenerAdapter> getListenerAdapters() {
		return this.listenerAdapters;
	}

	/**
	 * @return the commandManager
	 */
	public VCommandManager getVCommandManager() {
		return this.zcommandManager;
	}

	/**
	 * @return the inventoryManager
	 */
	public VInventoryManager getVInventoryManager() {
		return this.vinventoryManager;
	}

	/**
	 * Check if plugin is enable
	 * 
	 * @param pluginName
	 * @return
	 */
	protected boolean isEnable(Plugins pl) {
		Plugin plugin = getPlugin(pl);
		return plugin == null ? false : plugin.isEnabled();
	}

	/**
	 * Get plugin for plugins enum
	 * 
	 * @param pluginName
	 * @return
	 */
	protected Plugin getPlugin(Plugins plugin) {
		return Bukkit.getPluginManager().getPlugin(plugin.getName());
	}

	/**
	 * Register command
	 * 
	 * @param command
	 * @param vCommand
	 * @param aliases
	 */
	protected void registerCommand(String command, VCommand vCommand, String... aliases) {
		this.zcommandManager.registerCommand(this, command, vCommand, Arrays.asList(aliases));
	}

	/**
	 * Register Inventory
	 * 
	 * @param inventory
	 * @param vInventory
	 */
	protected void registerInventory(EnumInventory inventory, VInventory vInventory) {
		this.vinventoryManager.registerInventory(inventory, vInventory);
	}

	/**
	 * For 1.13+
	 * 
	 * @param resourcePath
	 * @param toPath
	 * @param replace
	 */
	public void saveResource(String resourcePath, String toPath, boolean replace) {
		if (resourcePath != null && !resourcePath.equals("")) {
			resourcePath = resourcePath.replace('\\', '/');
			InputStream in = this.getResource(resourcePath);
			if (in == null) {
				throw new IllegalArgumentException(
						"The embedded resource '" + resourcePath + "' cannot be found in " + this.getFile());
			} else {
				File outFile = new File(getDataFolder(), toPath);
				int lastIndex = toPath.lastIndexOf(47);
				File outDir = new File(getDataFolder(), toPath.substring(0, lastIndex >= 0 ? lastIndex : 0));
				if (!outDir.exists()) {
					outDir.mkdirs();
				}

				try {
					if (outFile.exists() && !replace) {
						getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile
								+ " because " + outFile.getName() + " already exists.");
					} else {
						OutputStream out = new FileOutputStream(outFile);
						byte[] buf = new byte[1024];

						int len;
						while ((len = in.read(buf)) > 0) {
							out.write(buf, 0, len);
						}

						out.close();
						in.close();
					}
				} catch (IOException var10) {
					getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, var10);
				}

			}
		} else {
			throw new IllegalArgumentException("ResourcePath cannot be null or empty");
		}
	}

}
