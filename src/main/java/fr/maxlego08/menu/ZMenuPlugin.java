package fr.maxlego08.menu;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.PlatformScheduler;
import dev.faststats.bukkit.BukkitContext;
import fr.maxlego08.menu.api.*;
import fr.maxlego08.menu.api.annotations.AutoFontImage;
import fr.maxlego08.menu.api.annotations.AutoListener;
import fr.maxlego08.menu.api.annotations.AutoMaterialLoader;
import fr.maxlego08.menu.api.attribute.ApplySpigotAttribute;
import fr.maxlego08.menu.api.attribute.AttributApplier;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.configuration.dialog.ConfigDialogBuilder;
import fr.maxlego08.menu.api.dupe.DupeManager;
import fr.maxlego08.menu.api.enchantment.Enchantments;
import fr.maxlego08.menu.api.itemstack.ItemStackService;
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
import fr.maxlego08.menu.api.utils.version.ClientVersionManager;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import fr.maxlego08.menu.api.utils.version.VersionFilter;
import fr.maxlego08.menu.api.website.WebsiteManager;
import fr.maxlego08.menu.command.VCommandManager;
import fr.maxlego08.menu.command.commands.CommandMenu;
import fr.maxlego08.menu.common.utils.cache.YamlFileCache;
import fr.maxlego08.menu.config.ConfigManager;
import fr.maxlego08.menu.dupe.DupeListener;
import fr.maxlego08.menu.dupe.PDCDupeManager;
import fr.maxlego08.menu.enchantment.ZEnchantments;
import fr.maxlego08.menu.font.EmptyFont;
import fr.maxlego08.menu.hooks.ComponentMeta;
import fr.maxlego08.menu.hooks.NexoTagResolverLoader;
import fr.maxlego08.menu.hooks.bedrock.ZBedrockManager;
import fr.maxlego08.menu.hooks.bedrock.listener.BedrockReplacementListener;
import fr.maxlego08.menu.hooks.dialogs.ZDialogManager;
import fr.maxlego08.menu.hooks.packetevents.PacketEventPlayerInventoryManager;
import fr.maxlego08.menu.hooks.packetevents.PacketUtils;
import fr.maxlego08.menu.hooks.packetevents.loader.PacketEventTitleAnimationLoader;
import fr.maxlego08.menu.hooks.paper.PaperProtocolClientVersionProvider;
import fr.maxlego08.menu.hooks.protocolsupport.ProtocolSupportClientVersionProvider;
import fr.maxlego08.menu.hooks.viaversion.ViaVersionClientVersionProvider;
import fr.maxlego08.menu.inventory.InventoryHotReload;
import fr.maxlego08.menu.inventory.VInventoryManager;
import fr.maxlego08.menu.itemstack.ZItemStackService;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.listener.ItemUpdaterListener;
import fr.maxlego08.menu.nms.v1_21_R1.inventory.inventories.AnvilInventoryDefault;
import fr.maxlego08.menu.nms.v1_21_R1.network.NMSMenuPacketListener;
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
import fr.maxlego08.menu.zcore.ZPlugin;
import fr.maxlego08.menu.zcore.logger.BukkitLogger;
import fr.maxlego08.menu.zcore.logger.ComponentLogger;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.plugins.Metrics;
import fr.maxlego08.menu.zcore.utils.plugins.Plugins;
import fr.maxlego08.menu.zcore.utils.plugins.VersionChecker;
import fr.maxlego08.menu.zcore.utils.toast.ToastManager;
import fr.robie.paperdispatch.cache.OfflinePlayerCache;
import fr.robie.paperdispatch.manager.ICommandManager;
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
import java.util.concurrent.TimeUnit;

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
public class ZMenuPlugin extends ZPlugin implements fr.maxlego08.menu.api.MenuPlugin {
    private static ZMenuPlugin instance;

    private final boolean isMockBukkitServer;

    private final ICommandManager<ZMenuPlugin> commandManagerLib;
    private final StorageManager storageManager;
    private final ButtonManager buttonManager;
    private final ZInventoryManager inventoryManager;
    private final TitleAnimationManager titleAnimationManager;
    private final ZClientVersionManager clientVersionManager;
    private final CommandManager commandManager;
    private final MessageLoader messageLoader;
    private final DataManager dataManager;
    private final InventoriesPlayer inventoriesPlayer;
    private final PatternManager patternManager;
    private final Enchantments enchantments;
    private final ItemManager itemManager;
    private final ItemStackService itemStackService = new ZItemStackService(this);
    private final FoliaLib foliaLib;
    private final ComponentsManager componentsManager;
    private final Map<String, Object> globalPlaceholders = new HashMap<>();
    private final ToastHelper toastHelper;
    private final AttributApplier attributApplier;
    private final File configFile;
    private final PlatformScheduler scheduler;
    private final BukkitContext context = new BukkitContext.Factory(this, "df00d7dc59087d9e248fe7723489b87b").metrics(dev.faststats.Metrics.Factory::create).create();
    private ZWebsiteManager websiteManager;
    private DialogManager dialogManager;
    private BedrockManager bedrockManager;
    private CommandMenu commandMenu;
    private DupeManager dupeManager;
    private FontImage fontImage = new EmptyFont();
    private ComponentMeta metaUpdater;
    private PacketManager packetManager;
    private InventoryHotReload inventoryHotReload;

    public ZMenuPlugin() {
        this(false);
    }

    public ZMenuPlugin(Boolean isMockBukkitServer) {
        this.isMockBukkitServer = isMockBukkitServer != null && isMockBukkitServer;

        new BukkitLogger(this.getDescription().getFullName());

        this.commandManagerLib = new fr.robie.paperdispatch.manager.CommandManager<>(this);
        this.storageManager = new ZStorageManager(this);
        this.buttonManager = new ZButtonManager(this);
        this.inventoryManager = new ZInventoryManager(this);
        this.titleAnimationManager = new ZTitleAnimationManager();
        this.clientVersionManager = new ZClientVersionManager();
        this.commandManager = new ZCommandManager(this);
        this.messageLoader = new MessageLoader(this);
        this.dataManager = new ZDataManager(this);
        this.inventoriesPlayer = new ZInventoriesPlayer(this);
        this.patternManager = new ZPatternManager(this);
        this.enchantments = new ZEnchantments();
        this.itemManager = new ZItemManager(this);
        this.foliaLib = new FoliaLib(this);
        this.componentsManager = new ZComponentsManager();
        this.toastHelper = new ToastManager(this);
        this.attributApplier = new ApplySpigotAttribute();
        this.configFile = new File(this.getDataFolder(), "config.yml");
        this.scheduler = this.foliaLib.getScheduler();
    }

    public static ZMenuPlugin getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        if (this.isActive(Plugins.PACKETEVENTS)) {
            this.safeHook("packetevents", () -> {
                PacketManager manager = new PacketUtils(this);
                manager.onLoad();
                this.packetManager = manager;
            });
        }
    }

    @Override
    public void onEnable() {

        if (!this.isMockBukkitServer) {
            NMSMenuPacketListener.init(this);
        }

        instance = this;

        if (!this.loadMeta()) {
            return;
        }
        this.saveDefaultConfig();
        Configuration.getInstance().load(this.getConfig());
        this.websiteManager = new ZWebsiteManager(this); // Create a website manager after loading config.yml, for API URL. Never change the URL, only for dev purposes

        Configuration.HAS_DIALOG_SUPPORT = this.isDialogCapableServer() && Configuration.enableMiniMessageFormat;
        Configuration.HAS_BEDROCK_INVENTORY_SUPPORT = this.hasBedrockSupport();
        OfflinePlayerCache.install(this);

        if (this.packetManager != null) {
            boolean enabled = this.safeHook("packetevents", () -> this.packetManager.onEnable());
            if (!enabled || !this.packetManager.isReady()) {
                this.packetManager = null;
            }
        }

        fr.traqueur.currencies.CurrenciesAPI.init(this);

        this.dupeManager = new PDCDupeManager(this);
        this.enchantments.register();

        this.preEnable();

        this.storageManager.loadDatabase();
        this.addListener(this.storageManager);


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

        // Order matters: translation plugins are asked first because they rewrite the
        // handshake protocol number Paper reads, which would otherwise report the server
        // version for every translated client.
        if (this.isEnable(Plugins.VIAVERSION)) {
            this.safeHook("ViaVersion", () -> {
                Logger.info("ViaVersion detected, loading client version detection");
                this.clientVersionManager.registerProvider(new ViaVersionClientVersionProvider());
            });
        }
        if (this.isEnable(Plugins.PROTOCOLSUPPORT)) {
            this.safeHook("ProtocolSupport", () -> {
                Logger.info("ProtocolSupport detected, loading client version detection");
                this.clientVersionManager.registerProvider(new ProtocolSupportClientVersionProvider());
            });
        }
        this.safeHook("Paper client version", () -> this.clientVersionManager.registerProvider(new PaperProtocolClientVersionProvider()));
        this.addListener(this.clientVersionManager);
        servicesManager.register(ClientVersionManager.class, this.clientVersionManager, this, ServicePriority.Highest);

        if (this.isDialogCapableServer()) {
            if (Configuration.enableMiniMessageFormat) {
                boolean loaded = this.safeHook("Dialogs", () -> {
                    Logger.info("Paper server detected, loading Dialogs support");
                    ConfigManager configManager = new ConfigManager(this);
                    this.dialogManager = new ZDialogManager(this, configManager);
                    this.addListener((ZDialogManager) this.dialogManager);
                    servicesManager.register(DialogManager.class, this.dialogManager, this, ServicePriority.Highest);
                    ConfigDialogBuilder configDialogBuilder = new ConfigDialogBuilder("zMenu Config", "zMenu Configuration");
                    configManager.registerConfig(configDialogBuilder, Configuration.class, this);
                });
                if (!loaded) {
                    this.dialogManager = null;
                    Configuration.HAS_DIALOG_SUPPORT = false;
                }
            } else {
                Logger.info("Paper server detected but MiniMessage format is disabled, Dialogs support will not be loaded. Enable MiniMessage format in config.yml to use Dialogs.");
            }
        }

        if (this.hasBedrockSupport()) {
            boolean loaded = this.safeHook("Bedrock", () -> {
                Logger.info("Geyser or Floodgate detected, loading Bedrock Inventory support");
                this.bedrockManager = new ZBedrockManager(this);
                this.addListener(new BedrockReplacementListener(this.bedrockManager, this.inventoryManager));
                servicesManager.register(BedrockManager.class, this.bedrockManager, this, ServicePriority.Highest);
            });
            if (!loaded) {
                this.bedrockManager = null;
                Configuration.HAS_BEDROCK_INVENTORY_SUPPORT = false;
            }
        }

        this.registerInventory(EnumInventory.INVENTORY_DEFAULT, new InventoryDefault());
        if (MinecraftVersion.getCurrentVersion().isAtLeast(MinecraftVersion.parse("1.21")) && !this.isMockBukkitServer) {
            this.vinventoryManager.registerInventory(EnumInventory.INVENTORY_DEFAULT.getId(), InventoryType.ANVIL, new AnvilInventoryDefault());
        }
        this.commandManagerLib.trackCommand(this.commandMenu = new CommandMenu(this));
        this.commandManagerLib.flushRegistrations();

        /* Add Listener */
        this.safeHook("auto listeners", this::registerAutoListeners);
        this.addListener(this.vinventoryManager);
        this.addListener(this.inventoriesPlayer);
        this.addListener(new ItemUpdaterListener(this.itemManager));
        this.addListener(this.inventoryManager);

        this.safeHook("material loaders", this::registerMaterialLoaders);
        this.safeHook("plugin hooks", this::registerHooks);

        this.inventoryManager.load();
        this.commandManager.loadCommands();
        this.messageLoader.load();
        this.inventoriesPlayer.loadInventories();
        this.dataManager.loadPlayers();
        this.itemManager.loadAll();

        Bukkit.getOnlinePlayers().forEach(this.inventoryManager.getAnimationManager().getHistory()::load);

        this.inventoryHotReload = new InventoryHotReload(this);
        if (!this.isMockBukkitServer) {
            this.inventoryHotReload.update();
            // Applies a change of hot-reload.enable made with the configuration dialog
            this.scheduler.runTimer(this.inventoryHotReload::update, 2, 2, TimeUnit.SECONDS);
        }

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

        if (this.getConfig().getBoolean("DEV-ONLY-DONT-ENABLE-THIS", false)) {
            this.websiteManager.onEnable();
        }


        new VersionChecker(this, 253).useLastVersion();
        this.context.ready();

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
        if (this.packetManager != null) {
            this.safeHook("packetevents", () -> {
                this.inventoryManager.registerInventoryListener(new PacketEventPlayerInventoryManager(this));
                this.packetManager.onPostEnable();
            });
        }

        this.logFailedHooks();

        this.postEnable();
    }

    private void logFailedHooks() {
        List<String> failedHooks = this.getFailedHooks();
        if (failedHooks.isEmpty()) return;

        Logger.info("The following hooks failed and are disabled for this session: " + String.join(", ", failedHooks), Logger.LogType.WARNING);
        Logger.info("zMenu is running normally, but the features provided by those plugins are unavailable.", Logger.LogType.WARNING);
        Logger.info("If you need help, please contact the zMenu support team.", Logger.LogType.WARNING);

        this.clearFailedHooks();
    }

    /**
     * @return true when a Bedrock bridge is installed <i>and</i> healthy
     */
    private boolean hasBedrockSupport() {
        return this.isEnable(Plugins.GEYSER) || this.isEnable(Plugins.FLOODGATE);
    }

    private void registerAutoListeners() {
        ClassRegistry<Listener, MenuPlugin> registry = ClassRegistry.<Listener, MenuPlugin>of(Listener.class, this::addListener).tryConstructor((clazz, plugin) -> clazz.getDeclaredConstructor(MenuPlugin.class).newInstance(plugin)).tryNoArgsConstructor().errorLogger(Logger::error);

        int count = VersionFilter.scanAndRegister("fr.maxlego08.menu", this, AutoListener.class, registry);

        if (Configuration.enableInformationMessage) Logger.info("Registered " + count + " auto listener(s).");
    }

    private boolean hasClass(String className) {
        try {
            Class.forName(className.replace("{}", "."));
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Registers all the hooks for the plugins that are present.
     * This method is called at the end of {@link #onEnable()} and it will
     * register all the material loaders for the plugins that are present.
     * This method will be called only once, after the plugin has been enabled.
     */
    private void registerHooks() {
        if (this.isEnable(Plugins.NEXO)) {
            this.safeHook("Nexo", () -> {
                new NexoTagResolverLoader(this, this.metaUpdater);
            });
        }

        if (this.packetManager != null) {
            this.safeHook("packetevents animations", () -> this.titleAnimationManager.registerLoader("packet-events", new PacketEventTitleAnimationLoader()));
        }

        ClassRegistry<FontImage, MenuPlugin> registry = ClassRegistry.<FontImage, MenuPlugin>of(FontImage.class, fontImage1 -> this.fontImage = fontImage1).tryNoArgsConstructor().errorLogger(Logger::error);

        VersionFilter.scanAndRegister("fr.maxlego08.menu", this, AutoFontImage.class, registry);
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
        files.add("inventories/examples/animation.yml");
        files.add("inventories/examples/animation_advanced.yml");
        files.add("inventories/examples/animation_matrix.yml");
        files.add("inventories/examples/animation_custom.yml");
        files.add("inventories/examples/animation_shared.yml");
        files.add("inventories/examples/animation_loading.yml");
        files.add("inventories/examples/animation_action.yml");
        files.add("inventories/examples/animation_idle.yml");
        files.add("inventories/examples/animation_close.yml");
        files.add("inventories/examples/animation_random.yml");
        files.add("inventories/examples/animation_variants.yml");
        files.add("inventories/examples/hub_menu.yml");

        files.add("animations/fancy_open.yml");
        files.add("animations/wave_in.yml");
        files.add("animations/pulse.yml");
        files.add("animations/glint.yml");
        files.add("animations/sparkle.yml");
        files.add("animations/heartbeat.yml");

        files.add("commands/commands.yml");
        files.add("commands/punish/punish.yml");

        files.add("patterns/pattern_example.yml");
        files.add("patterns/pattern_cookies.yml");
        files.add("patterns/playtime_reward.yml");

        files.add("actions_patterns/default-actions.yml");

        if (this.isDialogCapableServer()) {
            files.add("dialogs/confirmation-dialog.yml");
            files.add("dialogs/default-dialog.yml");
            files.add("dialogs/multi_action-dialog.yml");
            files.add("dialogs/server_link-dialog.yml");
            files.add("dialogs/dynamic-dialog-example.yml");
            files.add("dialogs/dialog_list-dialog.yml");
        }

        if (this.hasBedrockSupport()) {
            files.add("bedrock/custom-form.yml");
            files.add("bedrock/modal-form.yml");
            files.add("bedrock/simple-form.yml");
        }

        files.add("items/default-items.yml");

        return files;
    }

    @Override
    public void onDisable() {


        this.safeDisable("metrics", this.context::shutdown);

        if (this.packetManager != null) {
            this.safeDisable("packetevents", () -> this.packetManager.onDisable());
        }

        this.preDisable();

        if (this.vinventoryManager != null) this.safeDisable("inventories", this.vinventoryManager::close);
        this.safeDisable("player inventories", this.inventoriesPlayer::restoreAllInventories);
      
        if (this.inventoryHotReload != null) this.inventoryHotReload.stop();

        if (this.storageManager != null) this.safeDisable("storage", this.storageManager::flush);

        this.safeDisable("configuration", () -> Configuration.getInstance().save(this.getConfig(), this.configFile));

        this.safeDisable("file cache", YamlFileCache::clearCache);
        this.safeDisable("api offline player cache", fr.maxlego08.menu.api.utils.OfflinePlayerCache::clearCache);

        if (this.websiteManager != null) this.safeDisable("website manager", this.websiteManager::onDisable);

        if (!this.isMockBukkitServer) {
            this.safeDisable("packet listener", () -> {
                NMSMenuPacketListener nmsMenuPacketListener = NMSMenuPacketListener.get();
                if (nmsMenuPacketListener != null) {
                    nmsMenuPacketListener.shutdown();
                }
            });
        }

        this.safeDisable("offline player cache", () -> OfflinePlayerCache.uninstall(this));

        this.safeDisable("item manager", this.itemManager::unloadListeners);

        this.safeDisable("services", () -> this.getServer().getServicesManager().unregisterAll(this));

        this.postDisable();
    }

    /**
     * The oldest server version zMenu loads its dialog support on.
     * <p>
     * Note this is <b>not</b> the same threshold as
     * {@link fr.maxlego08.menu.api.utils.version.ClientVersionManager#DIALOG_MINIMUM_VERSION}
     * (1.21.6), which is the oldest <i>client</i> able to render a dialog. The server gate is
     * deliberately one patch higher; the consequence is that on a 1.21.6 server dialogs are
     * not loaded at all, so neither dialogs nor their fallbacks exist there.
     */
    private static final MinecraftVersion DIALOG_MINIMUM_SERVER_VERSION = MinecraftVersion.parse("1.21.7");

    /**
     * Whether this server can run the Paper Dialog API at all: right platform, new enough,
     * and the Paper dialog classes actually present.
     *
     * @return true if dialog support can be loaded
     */
    private boolean isDialogCapableServer() {
        return MinecraftVersion.getCurrentVersion().isAtLeast(DIALOG_MINIMUM_SERVER_VERSION)
                && this.hasClass("io{}papermc{}paper{}registry{}data{}dialog{}action{}DialogAction");
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
    public ZInventoryManager getInventoryManager() {
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
    public ClientVersionManager getClientVersionManager() {
        return this.clientVersionManager;
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
    public ItemStackService getItemStackService() {
        return this.itemStackService;
    }

    @Override
    public Optional<PacketManager> getPacketManager() {
        if (this.packetManager == null || !this.packetManager.isReady()) return Optional.empty();
        return Optional.of(this.packetManager);
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
     * Returns the class that watches the inventories folder to reload modified inventories
     *
     * @return the inventoryHotReload
     */
    public InventoryHotReload getInventoryHotReload() {
        return this.inventoryHotReload;
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

    @Override
    public boolean isMockBukkitServer() {
        return this.isMockBukkitServer;
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
        return Placeholder.Placeholders.getPlaceholder().setPlaceholders(player, string).replace("\uF000", "%");
    }

    @Override
    public String parse(OfflinePlayer offlinePlayer, String string) {
        return Placeholder.Placeholders.getPlaceholder().setPlaceholders(offlinePlayer, string).replace("\uF000", "%");
    }

    @Override
    public List<String> parse(Player player, List<String> strings) {
        return Placeholder.Placeholders.getPlaceholder().setPlaceholders(player, strings).stream().map(s -> s.replace("\uF000", "%")).toList();
    }

    @Override
    public List<String> parse(OfflinePlayer offlinePlayer, List<String> strings) {
        return Placeholder.Placeholders.getPlaceholder().setPlaceholders(offlinePlayer, strings).stream().map(s -> s.replace("\uF000", "%")).toList();
    }

    /**
     * Creates the {@link ComponentMeta} used to format items, messages and titles.
     *
     * @return {@code false} if the formatter could not be created, in which case the plugin is disabled.
     */
    private boolean loadMeta() {
        try {
            ComponentMeta componentMeta = new ComponentMeta(this);
            this.metaUpdater = componentMeta;
            new ComponentLogger(this.getDescription().getFullName(), componentMeta);
            new ComponentItemStackPlatformHelper(componentMeta);
            Logger.info("Use ComponentMeta");
            return true;
        } catch (Exception exception) {
            Logger.error("Unable to initialize the MiniMessage formatter, zMenu will be disabled.", exception);
            this.getServer().getPluginManager().disablePlugin(this);
            return false;
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
