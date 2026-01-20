package fr.maxlego08.menu;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.PlatformScheduler;
import fr.maxlego08.menu.api.*;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.api.configuration.dialog.ConfigDialogBuilder;
import fr.maxlego08.menu.api.dupe.DupeManager;
import fr.maxlego08.menu.api.enchantment.Enchantments;
import fr.maxlego08.menu.api.font.FontImage;
import fr.maxlego08.menu.api.interfaces.ReturnBiConsumer;
import fr.maxlego08.menu.api.pattern.PatternManager;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.players.inventory.InventoriesPlayer;
import fr.maxlego08.menu.api.storage.StorageManager;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.api.utils.toast.ToastHelper;
import fr.maxlego08.menu.api.website.WebsiteManager;
import fr.maxlego08.menu.command.VCommandManager;
import fr.maxlego08.menu.command.commands.CommandMenu;
import fr.maxlego08.menu.config.ConfigManager;
import fr.maxlego08.menu.dupe.DupeListener;
import fr.maxlego08.menu.dupe.NMSDupeManager;
import fr.maxlego08.menu.dupe.PDCDupeManager;
import fr.maxlego08.menu.enchantment.ZEnchantments;
import fr.maxlego08.menu.font.EmptyFont;
import fr.maxlego08.menu.hooks.*;
import fr.maxlego08.menu.hooks.bedrock.ZBedrockManager;
import fr.maxlego08.menu.hooks.bedrock.listener.bedrockReplacementListener;
import fr.maxlego08.menu.hooks.dialogs.ZDialogManager;
import fr.maxlego08.menu.hooks.executableblocks.ExecutableBlocksLoader;
import fr.maxlego08.menu.hooks.executableitems.ExecutableItemsLoader;
import fr.maxlego08.menu.hooks.headdatabase.HeadDatabaseLoader;
import fr.maxlego08.menu.hooks.itemsadder.ItemsAdderFont;
import fr.maxlego08.menu.hooks.itemsadder.ItemsAdderLoader;
import fr.maxlego08.menu.inventory.VInventoryManager;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.listener.AdapterListener;
import fr.maxlego08.menu.listener.SwapKeyListener;
import fr.maxlego08.menu.loader.materials.ArmorLoader;
import fr.maxlego08.menu.loader.materials.Base64Loader;
import fr.maxlego08.menu.pattern.ZPatternManager;
import fr.maxlego08.menu.placeholder.ItemPlaceholders;
import fr.maxlego08.menu.placeholder.LocalPlaceholder;
import fr.maxlego08.menu.placeholder.MenuPlaceholders;
import fr.maxlego08.menu.placeholder.Placeholder;
import fr.maxlego08.menu.players.ZDataManager;
import fr.maxlego08.menu.players.inventory.ZInventoriesPlayer;
import fr.maxlego08.menu.save.MessageLoader;
import fr.maxlego08.menu.storage.ZStorageManager;
import fr.maxlego08.menu.website.Token;
import fr.maxlego08.menu.website.ZWebsiteManager;
import fr.maxlego08.menu.zcore.ZPlugin;
import fr.maxlego08.menu.zcore.enums.EnumInventory;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.meta.ClassicMeta;
import fr.maxlego08.menu.zcore.utils.nms.NMSUtils;
import fr.maxlego08.menu.zcore.utils.nms.NmsVersion;
import fr.maxlego08.menu.zcore.utils.plugins.Metrics;
import fr.maxlego08.menu.zcore.utils.plugins.Plugins;
import fr.maxlego08.menu.zcore.utils.plugins.VersionChecker;
import fr.maxlego08.menu.zcore.utils.toast.ToastManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

import java.io.File;
import java.util.*;

/**
 * System to create your plugins very simply Projet with <a href="https://github.com/Maxlego08/TemplatePlugin">https://github.com/Maxlego08/TemplatePlugin</a>
 * Documentation: <a href="https://docs.zmenu.dev/">https://docs.zmenu.dev/</a>
 * <p>
 * zMenus is a complete inventory plugin.
 * You can create your inventories and link them to custom commands. With the button system you will be able to push to the maximum the customization of your inventories.
 * You need to create an inventory per file, and you can sort your files into folders.
 * The plugin has an advanced API to allow other developers to use the same inventory configuration system. You can link inventories of several plugins together without any worries! The goal of this API is to have a uniform configuration for a better user experience.
 * </p>
 *
 * @author Maxlego08
 */
public class ZMenuPlugin extends ZPlugin implements MenuPlugin {

    private static ZMenuPlugin instance;
    private final StorageManager storageManager = new ZStorageManager(this);
    private final ButtonManager buttonManager = new ZButtonManager(this);
    private final InventoryManager inventoryManager = new ZInventoryManager(this);
    private final CommandManager commandManager = new ZCommandManager(this);
    private DialogManager dialogManager;
    private BedrockManager bedrockManager;
    private final MessageLoader messageLoader = new MessageLoader(this);
    private final DataManager dataManager = new ZDataManager(this);
    private final ZWebsiteManager websiteManager = new ZWebsiteManager(this);
    private final InventoriesPlayer inventoriesPlayer = new ZInventoriesPlayer(this);
    private final PatternManager patternManager = new ZPatternManager(this);
    private final Enchantments enchantments = new ZEnchantments();
    private final ItemManager itemManager = new ZItemManager(this);
    private final Map<String, Object> globalPlaceholders = new HashMap<>();
    private final ToastHelper toastHelper = new ToastManager(this);
    private CommandMenu commandMenu;
    private PlatformScheduler scheduler;
    private DupeManager dupeManager;
    private FontImage fontImage = new EmptyFont();
    private MetaUpdater metaUpdater = new ClassicMeta();
    private FoliaLib foliaLib;
    private final File configFile = new File(getDataFolder(), "config.yml");
    // private final PacketUtils packetUtils = new PacketUtils(this);

    public static ZMenuPlugin getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        // this.packetUtils.onLoad();
    }

    @Override
    public void onEnable() {

        instance = this;
        // this.packetUtils.onEnable();

        this.scheduler = (this.foliaLib = new FoliaLib(this)).getScheduler();

        this.dupeManager = NmsVersion.nmsVersion.isPdcVersion() ? new PDCDupeManager(this) : new NMSDupeManager();
        this.enchantments.register();

        this.preEnable();

        this.saveDefaultConfig();
        Config.getInstance().load(getConfig());
        this.storageManager.loadDatabase();
        this.addListener(this.storageManager);

        this.loadMeta();

        List<String> files = getInventoriesFiles();
        File folder = new File(this.getDataFolder(), "inventories");

        if (!folder.exists()) folder.mkdirs();

        if (Config.generateDefaultFile) {
            files.forEach(filePath -> {
                if (!new File(this.getDataFolder(), filePath).exists()) {
                    saveResource(filePath, false);
                }
            });
        }

        this.loadGlobalPlaceholders();

        this.zCommandManager = new VCommandManager(this);
        this.vinventoryManager = new VInventoryManager(this);

        ServicesManager servicesManager = this.getServer().getServicesManager();
        servicesManager.register(InventoryManager.class, this.inventoryManager, this, ServicePriority.Highest);
        servicesManager.register(ButtonManager.class, this.buttonManager, this, ServicePriority.Highest);
        servicesManager.register(CommandManager.class, this.commandManager, this, ServicePriority.Highest);
        servicesManager.register(WebsiteManager.class, this.websiteManager, this, ServicePriority.Highest);
        servicesManager.register(DataManager.class, this.dataManager, this, ServicePriority.Highest);
        servicesManager.register(InventoriesPlayer.class, this.inventoriesPlayer, this, ServicePriority.Highest);
        servicesManager.register(PatternManager.class, this.patternManager, this, ServicePriority.Highest);
        servicesManager.register(DupeManager.class, this.dupeManager, this, ServicePriority.Highest);
        servicesManager.register(Enchantments.class, this.enchantments, this, ServicePriority.Highest);

        if (isPaper() && NmsVersion.getCurrentVersion().isDialogsVersion()){
            Logger.info("Paper server detected, loading Dialogs support");
            ConfigManager configManager = new ConfigManager(this);
            this.dialogManager = new ZDialogManager(this, configManager);
            servicesManager.register(DialogManager.class, this.dialogManager, this, ServicePriority.Highest);
            ConfigDialogBuilder configDialogBuilder = new ConfigDialogBuilder("zMenu Config", "zMenu Configuration");
            Logger.info(configDialogBuilder.getName());
            configManager.registerConfig(configDialogBuilder,Config.class, this);
        }

        if (this.isActive(Plugins.GEYSER) || this.isActive(Plugins.FLOODGATE)){
            Logger.info("Geyser detected, loading Bedrock Inventory support");
            this.bedrockManager = new ZBedrockManager(this);
            this.addListener(new bedrockReplacementListener(this.bedrockManager));
            servicesManager.register(BedrockManager.class, this.bedrockManager, this, ServicePriority.Highest);
        }

        this.registerInventory(EnumInventory.INVENTORY_DEFAULT, new InventoryDefault());
        this.registerCommand("zmenu", this.commandMenu = new CommandMenu(this), "zm");

        /* Add Listener */
        this.addListener(new SwapKeyListener());
        this.addListener(new AdapterListener(this));
        this.addListener(this.vinventoryManager);
        this.addListener(this.inventoriesPlayer);
        this.addSimpleListener(this.inventoryManager);

        this.inventoryManager.registerMaterialLoader(new Base64Loader());
        this.inventoryManager.registerMaterialLoader(new ArmorLoader());
        this.registerHooks();

        this.inventoryManager.load();
        this.commandManager.loadCommands();
        this.messageLoader.load();
        this.inventoriesPlayer.loadInventories();
        this.dataManager.loadPlayers();

        LocalPlaceholder localPlaceholder = LocalPlaceholder.getInstance();
        localPlaceholder.register("argument_", (offlinePlayer, value) -> {
            Optional<String> optional = this.commandManager.getPlayerArgument(offlinePlayer.getUniqueId(), value);
            return optional.orElse(null);
        });

        this.websiteManager.registerPlaceholders();
        new MenuPlaceholders().register(this);
        new ItemPlaceholders().register(this);

        ((ZDataManager) this.dataManager).registerPlaceholder(localPlaceholder);

        new Metrics(this, 14951);

        File tokenFile = new File(this.getDataFolder(), "token.json");
        if (tokenFile.exists()) {
            Token.getInstance().load(this.getPersist());
        }

        new VersionChecker(this, 253).useLastVersion();

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        if (Config.enableAntiDupe) {
            this.addListener(new DupeListener(this.dupeManager));
        }

        if (!isActive(Plugins.ZMENUPLUS)) {
            Logger.info("");
            Logger.info("You can support zMenu by upgrading your account here: https://minecraft-inventory-builder.com/account-upgrade");
            Logger.info("zMenu’s site includes an inventory editor (under development), a marketplace (already available) is a forum (under development)");
            Logger.info("");
        }


        this.websiteManager.loadPlaceholders();
        this.dataManager.loadDefaultValues();

        // this.inventoryManager.registerInventoryListener(this.packetUtils);

        this.postEnable();
    }

    /**
     * Registers all the hooks for the plugins that are present.
     * This method is called at the end of {@link #onEnable()} and it will
     * register all the material loaders for the plugins that are present.
     * This method will be called only once, after the plugin has been enabled.
     */
    private void registerHooks() {
        if (this.isActive(Plugins.HEADDATABASE)) {
            this.inventoryManager.registerMaterialLoader(new HeadDatabaseLoader());
        }
        if (this.isActive(Plugins.ZHEAD)) {
            this.inventoryManager.registerMaterialLoader(new ZHeadLoader(this));
        }
        if (this.isActive(Plugins.ORAXEN)) {
            this.inventoryManager.registerMaterialLoader(new OraxenLoader());
        }
        if (this.isActive(Plugins.CRAFTENGINE)) {
            this.inventoryManager.registerMaterialLoader(new CraftEngineLoader());
        }
        if (this.isActive(Plugins.NEXO)) {
            this.inventoryManager.registerMaterialLoader(new NexoLoader());
        }
        if (this.isEnable(Plugins.MAGICCOSMETICS)) {
            this.inventoryManager.registerMaterialLoader(new MagicCosmeticsLoader());
        }
        if (this.isEnable(Plugins.HMCCOSMETICS)) {
            this.inventoryManager.registerMaterialLoader(new HmccosmeticsLoader());
        }
        if (this.isEnable(Plugins.ITEMSADDER)) {
            this.inventoryManager.registerMaterialLoader(new ItemsAdderLoader(this));
            this.fontImage = new ItemsAdderFont();
        }
        if (this.isActive(Plugins.SLIMEFUN)) {
            this.inventoryManager.registerMaterialLoader(new SlimeFunLoader());
        }
        if (this.isActive(Plugins.NOVA)) {
            this.inventoryManager.registerMaterialLoader(new NovaLoader());
        }
        if (this.isActive(Plugins.ECO)) {
            this.inventoryManager.registerMaterialLoader(new EcoLoader());
        }
        if (this.isActive(Plugins.ZITEMS)) {
            this.inventoryManager.registerMaterialLoader(new ZItemsLoader(this));
        }
        if (this.isActive(Plugins.EXECUTABLE_ITEMS)) {
            this.inventoryManager.registerMaterialLoader(new ExecutableItemsLoader());
        }
        if (this.isActive(Plugins.EXECUTABLE_BLOCKS)) {
            this.inventoryManager.registerMaterialLoader(new ExecutableBlocksLoader());
        }
    }

    private List<String> getInventoriesFiles() {
        List<String> files = new ArrayList<>();
        files.add("inventories/basic_inventory.yml");
        files.add("inventories/advanced_inventory.yml");
        files.add("inventories/pro_inventory.yml");
        files.add("inventories/example_punish.yml");
        files.add("inventories/examples/cookies.yml");
        files.add("inventories/examples/playtimes.yml");
        files.add("inventories/examples/switch.yml");
        files.add("inventories/examples/item_drag.yml");

        files.add("commands/commands.yml");
        files.add("commands/punish/punish.yml");

        files.add("patterns/pattern_example.yml");
        files.add("patterns/pattern_cookies.yml");
        files.add("patterns/playtime_reward.yml");

        if (isPaper() && NmsVersion.getCurrentVersion().isDialogsVersion()){
            files.add("dialogs/confirmation-dialog.yml");
            files.add("dialogs/default-dialog.yml");
            files.add("dialogs/multi_action-dialog.yml");
            files.add("dialogs/server_link-dialog.yml");
        }

        if (this.isActive(Plugins.GEYSER)){
            files.add("bedrock/custom-form.yml");
            files.add("bedrock/modal-form.yml");
            files.add("bedrock/simple-form.yml");
        }

        return files;
    }

    @Override
    public void onDisable() {

        this.preDisable();

        if (this.vinventoryManager != null) this.vinventoryManager.close();

        Config.getInstance().save(getConfig(), this.configFile);

        if (Token.token != null) {
            Token.getInstance().save(this.getPersist());
        }
        // this.packetUtils.onDisable();

        this.postDisable();
    }

    /**
     * Returns the class that will manage the loading of the buttons
     *
     * @return {@link ButtonManager}
     */
    public ButtonManager getButtonManager() {
        return this.buttonManager;
    }

    /**
     * Return the class that will manage the inventories
     *
     * @return the inventoryManager
     */
    public InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    /**
     * Returns the class that will load the message file
     *
     * @return the messageLoader
     */
    public MessageLoader getMessageLoader() {
        return this.messageLoader;
    }

    /**
     * Returns the class that will manager the commands
     *
     * @return the commandManager
     */
    public CommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * Returns the class that will manager the dialogs
     *
     * @return the DialogManager
     */
    @Override
    public DialogManager getDialogManager() {return this.dialogManager;}

    /**
     * @return
     */
    @Override
    public ItemManager getItemManager() {
        return this.itemManager;
    }

    /**
     * Returns the class that will manager the bedrock inventory
     *
     * @return the BedrockManager
     */
    @Override
    public BedrockManager getBedrockManager() {return this.bedrockManager;}

    @Override
    public StorageManager getStorageManager() {
        return this.storageManager;
    }

    @Override
    public boolean isFolia() {
        return this.foliaLib.isFolia();
    }

    @Override
    public boolean isPaper() {
        return this.foliaLib.isPaper();
    }

    @Override
    public boolean isSpigot() {
        return this.foliaLib.isSpigot();
    }

    @Override
    public void registerPlaceholder(String startWith, ReturnBiConsumer<OfflinePlayer, String, String> biConsumer) {
        LocalPlaceholder.getInstance().register(startWith, biConsumer);
    }

    @Override
    public MenuItemStack loadItemStack(YamlConfiguration configuration, String path, File file) {
        return this.inventoryManager.loadItemStack(configuration, path, file);
    }

    /**
     * Returns the class that will manage the website
     *
     * @return the websitemanager
     */
    public ZWebsiteManager getWebsiteManager() {
        return websiteManager;
    }

    /**
     * Returns the main command
     *
     * @return the commandMenu
     */
    public CommandMenu getCommandMenu() {
        return commandMenu;
    }

    @Override
    public DataManager getDataManager() {
        return dataManager;
    }

    @Override
    public PlatformScheduler getScheduler() {
        return scheduler;
    }

    @Override
    public InventoriesPlayer getInventoriesPlayer() {
        return inventoriesPlayer;
    }

    @Override
    public PatternManager getPatternManager() {
        return patternManager;
    }

    @Override
    public DupeManager getDupeManager() {
        return dupeManager;
    }

    @Override
    public Enchantments getEnchantments() {
        return enchantments;
    }

    @Override
    public MetaUpdater getMetaUpdater() {
        return this.metaUpdater;
    }

    @Override
    public FontImage getFontImage() {
        return this.fontImage;
    }

    @Override
    public Map<String, Object> getGlobalPlaceholders() {
        return this.globalPlaceholders;
    }

    public void loadGlobalPlaceholders() {

        this.globalPlaceholders.clear();
        File file = new File(this.getDataFolder(), "global-placeholders.yml");
        if (!file.exists()) {
            this.saveResource("global-placeholders.yml", false);
        }

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        configuration.getKeys(false).forEach(key -> this.globalPlaceholders.put(key, configuration.get(key)));
    }

    @Override
    public <T> T getProvider(Class<T> classPath) {
        RegisteredServiceProvider<T> provider = getServer().getServicesManager().getRegistration(classPath);
        if (provider == null) {
            getLogger().info("Unable to retrieve the provider " + classPath);
            return null;
        }
        return provider.getProvider();
    }

    @Override
    public String parse(Player player, String string) {
        return Placeholder.Placeholders.getPlaceholder().setPlaceholders(player, string);
    }

    @Override
    public String parse(OfflinePlayer offlinePlayer, String string) {
        return Placeholder.Placeholders.getPlaceholder().setPlaceholders(offlinePlayer, string);
    }

    @Override
    public List<String> parse(Player player, List<String> strings) {
        return Placeholder.Placeholders.getPlaceholder().setPlaceholders(player, strings);
    }

    @Override
    public List<String> parse(OfflinePlayer offlinePlayer, List<String> strings) {
        return Placeholder.Placeholders.getPlaceholder().setPlaceholders(offlinePlayer, strings);
    }

    private void loadMeta() {
        if (!Config.enableMiniMessageFormat || !NMSUtils.isComponentColor()) {
            this.metaUpdater = new ClassicMeta();
            getLogger().info("Use ClassicMeta");
        } else {
            try {
                Class.forName("net.kyori.adventure.text.minimessage.MiniMessage");
                this.metaUpdater = new ComponentMeta(this);
                getLogger().info("Use ComponentMeta");
            } catch (Exception ignored) {
                this.metaUpdater = new ClassicMeta();
                getLogger().info("Use ClassicMeta");
            }
        }
    }

    @Override
    public ToastHelper getToastHelper() {
        return this.toastHelper;
    }

    public File getConfigFile() {
        return this.configFile;
    }
}
