package fr.maxlego08.menu.zcore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.exceptions.ListenerNullException;
import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.command.VCommandManager;
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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public abstract class ZPlugin extends JavaPlugin {

    private final Logger log = new Logger(this.getDescription().getFullName());
    private final List<ListenerAdapter> listenerAdapters = new ArrayList<>();
    protected VCommandManager zCommandManager;
    protected VInventoryManager vinventoryManager;
    private long enableTime;
    private Gson gson;
    private Persist persist;

    protected void preEnable() {

        LocalPlaceholder.getInstance().setPlugin((ZMenuPlugin) this);

        this.enableTime = System.currentTimeMillis();

        this.log.log("=== ENABLE START ===");
        this.log.log("Plugin Version V<&>c" + getDescription().getVersion(), LogType.INFO);

        this.getDataFolder().mkdirs();

        this.gson = getGsonBuilder().create();
        this.persist = new Persist(this);

        Placeholder.Placeholders.getPlaceholder();
    }

    protected void postEnable() {

        if (this.zCommandManager != null) {
            this.zCommandManager.validCommands();
        }

        this.log.log("=== ENABLE DONE <&>7(<&>6" + Math.abs(enableTime - System.currentTimeMillis()) + "ms<&>7) <&>e===");

    }

    protected void preDisable() {
        this.enableTime = System.currentTimeMillis();
        this.log.log("=== DISABLE START ===");
    }

    protected void postDisable() {
        this.log.log("=== DISABLE DONE <&>7(<&>6" + Math.abs(enableTime - System.currentTimeMillis()) + "ms<&>7) <&>e===");

    }

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
     * @param listener New Listener
     */
    public void addListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    /**
     * Add a listener
     *
     * @param listener New Listener
     */
    public void addSimpleListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    /**
     * Add a listener from ListenerAdapter
     *
     * @param adapter New {@link ListenerAdapter}
     */
    public void addListener(ListenerAdapter adapter) {
        if (adapter == null) throw new ListenerNullException("Warning, your listener is null");
        this.listenerAdapters.add(adapter);
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
     * @return listenerAdapters
     */
    public List<ListenerAdapter> getListenerAdapters() {
        return this.listenerAdapters;
    }

    /**
     * @return the commandManager
     */
    public VCommandManager getVCommandManager() {
        return this.zCommandManager;
    }

    /**
     * @return the inventoryManager
     */
    public VInventoryManager getVInventoryManager() {
        return this.vinventoryManager;
    }

    /**
     * Check if plugin is enabled
     *
     * @param pluginName Plugin name
     * @return true is its enable
     */
    public boolean isEnable(Plugins pluginName) {
        Plugin plugin = getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }

    /**
     * Check if plugin is enabled
     *
     * @param pluginName Plugin name
     * @return true is its enable
     */
    public boolean isActive(Plugins pluginName) {
        Plugin plugin = getPlugin(pluginName);
        return plugin != null;
    }

    /**
     * Get plugin for plugins enum
     *
     * @param plugin Plugin name
     * @return Plugin
     */
    protected Plugin getPlugin(Plugins plugin) {
        return Bukkit.getPluginManager().getPlugin(plugin.getName());
    }

    /**
     * Register command
     *
     * @param command  Main command
     * @param vCommand VCommand
     * @param aliases  list of aliases
     */
    protected void registerCommand(String command, VCommand vCommand, String... aliases) {
        this.zCommandManager.registerCommand(this, command, vCommand, Arrays.asList(aliases));
    }

    /**
     * Register Inventory
     *
     * @param inventory  Inventory key
     * @param vInventory VInventory
     */
    protected void registerInventory(EnumInventory inventory, VInventory vInventory) {
        this.vinventoryManager.registerInventory(inventory, vInventory);
    }

    /**
     * For 1.13+
     *
     * @param resourcePath Resource path
     * @param toPath       new path
     * @param replace      replace boolean
     */
    public void saveResource(String resourcePath, String toPath, boolean replace) {
        if (resourcePath != null && !resourcePath.equals("")) {
            resourcePath = resourcePath.replace('\\', '/');
            InputStream in = this.getResource(resourcePath);
            if (in == null) {
                throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + this.getFile());
            } else {
                File outFile = new File(getDataFolder(), toPath);
                int lastIndex = toPath.lastIndexOf(47);
                File outDir = new File(getDataFolder(), toPath.substring(0, Math.max(lastIndex, 0)));
                if (!outDir.exists()) {
                    outDir.mkdirs();
                }

                try {
                    if (outFile.exists() && !replace) {
                        getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
                    } else {
                        OutputStream out = Files.newOutputStream(outFile.toPath());
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

    public Persist getPersist() {
        return persist;
    }

    public Gson getGson() {
        return gson;
    }
}
