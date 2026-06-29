package fr.maxlego08.menu;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.PlatformScheduler;
import fr.maxlego08.menu.api.*;
import fr.maxlego08.menu.api.annotations.AutoListener;
import fr.maxlego08.menu.api.annotations.AutoMaterialLoader;
import fr.maxlego08.menu.api.attribute.ApplySpigotAttribute;
import fr.maxlego08.menu.api.attribute.AttributApplier;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.configuration.dialog.ConfigDialogBuilder;
import fr.maxlego08.menu.api.dupe.DupeManager;
import fr.maxlego08.menu.api.enchantment.Enchantments;
import fr.maxlego08.menu.api.font.FontImage;
import fr.maxlego08.menu.api.interfaces.ReturnBiConsumer;
import fr.maxlego08.menu.api.loader.ClassRegistry;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import fr.maxlego08.menu.api.pattern.PatternManager;
import fr.maxlego08.menu.api.placeholder.LocalPlaceholder;
import fr.maxlego08.menu.api.placeholder.Placeholder;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.players.inventory.InventoriesPlayer;
import fr.maxlego08.menu.api.storage.StorageManager;
import fr.maxlego08.menu.api.utils.EnumInventory;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.api.utils.toast.ToastHelper;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import fr.maxlego08.menu.api.utils.version.VersionFilter;
import fr.maxlego08.menu.api.website.WebsiteManager;
import fr.maxlego08.menu.command.VCommandManager;
import fr.maxlego08.menu.command.commands.CommandMenu;
import fr.maxlego08.menu.common.network.NMSMenuPacketListener;
import fr.maxlego08.menu.common.utils.cache.YamlFileCache;
import fr.maxlego08.menu.common.utils.nms.NMSUtils;
import fr.maxlego08.menu.config.ConfigManager;
import fr.maxlego08.menu.dupe.DupeListener;
import fr.maxlego08.menu.dupe.NMSDupeManager;
import fr.maxlego08.menu.dupe.PDCDupeManager;
import fr.maxlego08.menu.enchantment.ZEnchantments;
import fr.maxlego08.menu.font.EmptyFont;
import fr.maxlego08.menu.hooks.ComponentMeta;
import fr.maxlego08.menu.hooks.NexoTagResolverLoader;
import fr.maxlego08.menu.hooks.OraxenFont;
import fr.maxlego08.menu.hooks.bedrock.ZBedrockManager;
import fr.maxlego08.menu.hooks.bedrock.listener.BedrockReplacementListener;
import fr.maxlego08.menu.hooks.dialogs.ZDialogManager;
import fr.maxlego08.menu.hooks.itemsadder.ItemsAdderFont;
import fr.maxlego08.menu.hooks.packetevents.PacketEventPlayerInventoryManager;
import fr.maxlego08.menu.hooks.packetevents.PacketUtils;
import fr.maxlego08.menu.hooks.packetevents.loader.PacketEventTitleAnimationLoader;
import fr.maxlego08.menu.inventory.VInventoryManager;
import fr.maxlego08.menu.inventory.inventories.AnvilInventoryDefault;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.listener.ItemUpdaterListener;
import fr.maxlego08.menu.pattern.ZPatternManager;
import fr.maxlego08.menu.placeholder.ItemPlaceholders;
import fr.maxlego08.menu.placeholder.MenuPlaceholders;
import fr.maxlego08.menu.players.ZDataManager;
import fr.maxlego08.menu.players.inventory.ZInventoriesPlayer;
import fr.maxlego08.menu.registry.ZRuleLoaderRegistry;
import fr.maxlego08.menu.save.MessageLoader;
import fr.maxlego08.menu.storage.ZStorageManager;
import fr.maxlego08.menu.website.ZWebsiteManager;
import fr.maxlego08.menu.zcore.ComponentItemStackPlatformHelper;
import fr.maxlego08.menu.zcore.LegacyItemStackPlatformHelper;
import fr.maxlego08.menu.zcore.ZPlugin;
import fr.maxlego08.menu.zcore.logger.BukkitLogger;
import fr.maxlego08.menu.zcore.logger.ComponentLogger;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.meta.ClassicMeta;
import fr.maxlego08.menu.zcore.utils.plugins.Metrics;
import fr.maxlego08.menu.zcore.utils.plugins.Plugins;
import fr.maxlego08.menu.zcore.utils.plugins.VersionChecker;
import fr.maxlego08.menu.zcore.utils.toast.ToastManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

import java.io.File;
import java.util.*;

/**
 * System to create your plugins very simply Projet with <a href="https://github.com/Maxlego08/TemplatePlugin">https://github.com/Maxlego08/TemplatePlugin</a>
 * Documentation: <a href="https://docs.groupez.dev/zmenu/getting-started/">https://docs.groupez.dev/zmenu/getting-started</a>
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
    private final TitleAnimationManager titleAnimationManager = new ZTitleAnimationManager();
    private final CommandManager commandManager = new ZCommandManager(this);
    private final MessageLoader messageLoader = new MessageLoader(this);
    private final DataManager dataManager = new ZDataManager(this);
    private final InventoriesPlayer inventoriesPlayer = new ZInventoriesPlayer(this);
    private final PatternManager patternManager = new ZPatternManager(this);
    private final Enchantments enchantments = new ZEnchantments();
    private final ItemManager itemManager = new ZItemManager(this);
    private final FoliaLib foliaLib = new FoliaLib(this);
    private final ComponentsManager componentsManager = new ZComponentsManager();
    private final Map<String, Object> globalPlaceholders = new HashMap<>();
    private final ToastHelper toastHelper = new ToastManager(this);
    private final AttributApplier attributApplier = new ApplySpigotAttribute();
    private final File configFile = new File(this.getDataFolder(), "config.yml");
    private final PlatformScheduler scheduler = this.foliaLib.getScheduler();
    private WebsiteManager websiteManager;
    private DialogManager dialogManager;
    private BedrockManager bedrockManager;
    private CommandMenu commandMenu;
    private DupeManager dupeManager;
    private FontImage fontImage = new EmptyFont();
    private MetaUpdater metaUpdater = new ClassicMeta();
    private PacketManager packetManager;

    public ZMenuPlugin() {
        new BukkitLogger(this.getDescription().getFullName());
    }

    public static ZMenuPlugin getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        if (this.isActive(Plugins.PACKETEVENTS)) {
            this.packetManager = new PacketUtils(this);
        }
        if (this.packetManager != null) {
            this.packetManager.onLoad();
        }
    }

    @Override
    public void onEnable() {

        NMSMenuPacketListener.init(this);

        instance = this;

        this.saveDefaultConfig();
        Configuration.getInstance().load(this.getConfig());
        this.websiteManager = new ZWebsiteManager(this); // Create a website manager after loading config.yml, for API URL. Never change the URL, only for dev purposes

        if (this.packetManager != null) {
            this.packetManager.onEnable();
        }

        this.dupeManager = MinecraftVersion.getCurrentVersion().isAtLeast(MinecraftVersion.parse("1.14")) ? new PDCDupeManager(this) : new NMSDupeManager();
        this.enchantments.register();

        this.preEnable();

        this.storageManager.loadDatabase();
        this.addListener(this.storageManager);

        this.loadMeta();

        ZRuleLoaderRegistry.getInstance().registerDefaultLoaders(this);
        this.componentsManager.initializeDefaultComponents(this);

        List<String> files = this.getInventoriesFiles();
        File folder = new File(this.getDataFolder(), "inventories");

        if (!folder.exists()) folder.mkdirs();

        if (Configuration.generateDefaultFile) {
            files.forEach(filePath -> {
                if (!new File(this.getDataFolder(), filePath).exists()) {
                    this.saveResource(filePath, false);
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
        servicesManager.register(TitleAnimationManager.class, this.titleAnimationManager, this, ServicePriority.Highest);

        if (this.isPaperOrFolia() && MinecraftVersion.getCurrentVersion().isAtLeast(MinecraftVersion.parse("1.21.7"))) {
            if (Configuration.enableMiniMessageFormat) {
                Logger.info("Paper server detected, loading Dialogs support");
                ConfigManager configManager = new ConfigManager(this);
                this.dialogManager = new ZDialogManager(this, configManager);
                servicesManager.register(DialogManager.class, this.dialogManager, this, ServicePriority.Highest);
                ConfigDialogBuilder configDialogBuilder = new ConfigDialogBuilder("zMenu Config", "zMenu Configuration");
                configManager.registerConfig(configDialogBuilder, Configuration.class, this);
            } else {
                Logger.info("Paper server detected but MiniMessage format is disabled, Dialogs support will not be loaded. Enable MiniMessage format in config.yml to use Dialogs.");
            }
        }

        if (this.isActive(Plugins.GEYSER) || this.isActive(Plugins.FLOODGATE)) {
            Logger.info("Geyser or Floodgate detected, loading Bedrock Inventory support");
            this.bedrockManager = new ZBedrockManager(this);
            this.addListener(new BedrockReplacementListener(this.bedrockManager));
            servicesManager.register(BedrockManager.class, this.bedrockManager, this, ServicePriority.Highest);
        }

        this.registerInventory(EnumInventory.INVENTORY_DEFAULT, new InventoryDefault());
        this.vinventoryManager.registerInventory(EnumInventory.INVENTORY_DEFAULT.getId(), InventoryType.ANVIL, new AnvilInventoryDefault());
        this.registerCommand("zmenu", this.commandMenu = new CommandMenu(this), "zm");

        /* Add Listener */
        this.registerAutoListeners();
        this.addListener(this.vinventoryManager);
        this.addListener(this.inventoriesPlayer);
        this.addListener(new ItemUpdaterListener(this.itemManager));
        this.addListener(this.inventoryManager);

        this.registerMaterialLoaders();
        this.registerHooks();

        this.inventoryManager.load();
        this.commandManager.loadCommands();
        this.messageLoader.load();
        this.inventoriesPlayer.loadInventories();
        this.dataManager.loadPlayers();
        this.itemManager.loadAll();

        LocalPlaceholder localPlaceholder = LocalPlaceholder.getInstance();
        localPlaceholder.register("argument_", (offlinePlayer, value) -> {
            Optional<String> optional = this.commandManager.getPlayerArgument(offlinePlayer.getUniqueId(), value);
            return optional.orElse(null);
        });

        new MenuPlaceholders().register(this);
        new ItemPlaceholders().register(this);

        ((ZDataManager) this.dataManager).registerPlaceholder(localPlaceholder);

        new Metrics(this, 14951);

        File tokenFile = new File(this.getDataFolder(), "token.json");
        if (tokenFile.exists()) tokenFile.delete(); // Delete old token file

        if (getConfig().getBoolean("DEV-ONLY-DONT-ENABLE-THIS", false)) {
            this.websiteManager.onEnable();
        }


        new VersionChecker(this, 253).useLastVersion();

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        if (Configuration.enableAntiDupe) {
            this.addListener(new DupeListener(this.dupeManager));
        }

        if (!this.isActive(Plugins.ZMENUPLUS)) {
            Logger.info("");
            Logger.info("You can support zMenu by upgrading your account here: https://minecraft-inventory-builder.com/pricing");
            // Logger.info("zMenu’s site includes an inventory editor, a marketplace an a surprise (soon)");
            Logger.info("");
        }


        this.dataManager.loadDefaultValues();

//         this.inventoryManager.registerInventoryListener(this.packetUtils);
        if (this.isActive(Plugins.PACKETEVENTS)) this.inventoryManager.registerInventoryListener(new PacketEventPlayerInventoryManager(this));

        this.postEnable();
    }

    private void registerAutoListeners() {
        ClassRegistry<Listener, MenuPlugin> registry = ClassRegistry.<Listener, MenuPlugin>of(Listener.class, this::addListener).tryConstructor((clazz, plugin) -> clazz.getDeclaredConstructor(MenuPlugin.class).newInstance(plugin)).tryNoArgsConstructor().errorLogger(Logger::error);

        int count = VersionFilter.scanAndRegister("fr.maxlego08.menu", this, AutoListener.class, registry);

        if (Configuration.enableInformationMessage) Logger.info("Registered " + count + " auto listener(s).");
    }

    /**
     * Registers all the hooks for the plugins that are present.
     * This method is called at the end of {@link #onEnable()} and it will
     * register all the material loaders for the plugins that are present.
     * This method will be called only once, after the plugin has been enabled.
     */
    private void registerHooks() {
        if (this.isActive(Plugins.ORAXEN)) {
            this.fontImage = new OraxenFont();
        }

        if (this.isActive(Plugins.NEXO)) {
            if (this.metaUpdater instanceof ComponentMeta componentMeta) {
                new NexoTagResolverLoader(this, componentMeta);
            }
        }

        if (this.isActive(Plugins.ITEMSADDER)) {
            this.fontImage = new ItemsAdderFont();
        }

        if (this.isActive(Plugins.PACKETEVENTS)) {
            this.titleAnimationManager.registerLoader("packet-events", new PacketEventTitleAnimationLoader());
        }
    }

    private void registerMaterialLoaders() {
        ClassRegistry<MaterialLoader, MenuPlugin> registry = ClassRegistry.<MaterialLoader, MenuPlugin>of(MaterialLoader.class, this.inventoryManager::registerMaterialLoader).tryConstructor((clazz, plugin) -> clazz.getDeclaredConstructor(MenuPlugin.class).newInstance(plugin)).tryNoArgsConstructor().errorLogger(Logger::error);

        int count = VersionFilter.scanAndRegister("fr.maxlego08.menu", this, AutoMaterialLoader.class, registry);

        if (Configuration.enableInformationMessage) Logger.info("Registered " + count + " auto material loader(s).");
    }

    private List<String> getInventoriesFiles() {
        List<String> files = new ArrayList<>();
        files.add("inventories/basic_inventory.yml");
        files.add("inventories/advanced_inventory.yml");
        files.add("inventories/pro_inventory.yml");
        files.add("inventories/animated_title_inventory.yml");
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

        files.add("actions_patterns/default-actions.yml");

        if (this.isPaperOrFolia() && MinecraftVersion.getCurrentVersion().isAtLeast(MinecraftVersion.parse("1.21.7"))) {
            files.add("dialogs/confirmation-dialog.yml");
            files.add("dialogs/default-dialog.yml");
            files.add("dialogs/multi_action-dialog.yml");
            files.add("dialogs/server_link-dialog.yml");
        }

        if (this.isActive(Plugins.GEYSER) || this.isActive(Plugins.FLOODGATE)) {
            files.add("bedrock/custom-form.yml");
            files.add("bedrock/modal-form.yml");
            files.add("bedrock/simple-form.yml");
        }

        files.add("items/default-items.yml");

        return files;
    }

    @Override
    public void onDisable() {

        if (this.packetManager != null) {
            this.packetManager.onDisable();
        }

        this.preDisable();

        if (this.vinventoryManager != null) this.vinventoryManager.close();
        this.inventoriesPlayer.restoreAllInventories();

        Configuration.getInstance().save(this.getConfig(), this.configFile);

        YamlFileCache.clearCache();

        this.websiteManager.onDisable();

        this.itemManager.unloadListeners();

        this.getServer().getServicesManager().unregisterAll(this);

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
        return this.commandManager;
    }

    /**
     * Returns the class that will manager the dialogs
     *
     * @return the DialogManager
     */
    @Override
    public DialogManager getDialogManager() {
        return this.dialogManager;
    }

    @Override
    public ItemManager getItemManager() {
        return this.itemManager;
    }

    @Override
    public AttributApplier getAttributApplier() {
        return this.attributApplier;
    }

    @Override
    public TitleAnimationManager getTitleAnimationManager() {
        return this.titleAnimationManager;
    }

    @Override
    public ComponentsManager getComponentsManager() {
        return this.componentsManager;
    }

    /**
     * Returns the class that will manager the bedrock inventory
     *
     * @return the BedrockManager
     */
    @Override
    public BedrockManager getBedrockManager() {
        return this.bedrockManager;
    }

    @Override
    public String[] getClickRequirementKeys() {
        return new String[]{"click_requirement.", "click-requirement.", "click_requirements.", "click-requirements.", "clicks_requirement.", "clicks-requirement.", "clicks_requirements.", "clicks-requirements."};
    }

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
    public boolean isPaperOrFolia() {
        return this.foliaLib.isPaper() || this.foliaLib.isFolia();
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

    @Override
    public Optional<PacketManager> getPacketManager() {
        return Optional.ofNullable(this.packetManager);
    }

    /**
     * Returns the class that will manage the website
     *
     * @return the websiteManager
     */
    public WebsiteManager getWebsiteManager() {
        return this.websiteManager;
    }

    /**
     * Returns the main command
     *
     * @return the commandMenu
     */
    public CommandMenu getCommandMenu() {
        return this.commandMenu;
    }

    @Override
    public DataManager getDataManager() {
        return this.dataManager;
    }

    @Override
    public PlatformScheduler getScheduler() {
        return this.scheduler;
    }

    @Override
    public InventoriesPlayer getInventoriesPlayer() {
        return this.inventoriesPlayer;
    }

    @Override
    public PatternManager getPatternManager() {
        return this.patternManager;
    }

    @Override
    public DupeManager getDupeManager() {
        return this.dupeManager;
    }

    @Override
    public Enchantments getEnchantments() {
        return this.enchantments;
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
        RegisteredServiceProvider<T> provider = this.getServer().getServicesManager().getRegistration(classPath);
        if (provider == null) {
            Logger.info("Unable to retrieve the provider " + classPath);
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
        if (!Configuration.enableMiniMessageFormat || !NMSUtils.isComponentColor()) {
            this.metaUpdater = new ClassicMeta();
            Logger.info("Use ClassicMeta");
        } else {
            try {
                Class.forName("net.kyori.adventure.text.minimessage.MiniMessage");
                this.metaUpdater = new ComponentMeta(this);
                new ComponentLogger(this.getDescription().getFullName(), (ComponentMeta) this.metaUpdater);
                Logger.info("Use ComponentMeta");
            } catch (Exception ignored) {
                this.metaUpdater = new ClassicMeta();
                Logger.info("Use ClassicMeta");
            }
        }
        if (this.metaUpdater instanceof ComponentMeta componentMeta) {
            new ComponentItemStackPlatformHelper(componentMeta);
        } else {
            new LegacyItemStackPlatformHelper((ClassicMeta) this.metaUpdater);
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
