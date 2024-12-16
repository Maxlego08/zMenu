package fr.maxlego08.menu;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.dupe.DupeManager;
import fr.maxlego08.menu.api.enchantment.Enchantments;
import fr.maxlego08.menu.api.font.FontImage;
import fr.maxlego08.menu.api.pattern.PatternManager;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.players.inventory.InventoriesPlayer;
import fr.maxlego08.menu.api.scheduler.ZScheduler;
import fr.maxlego08.menu.api.website.WebsiteManager;
import fr.maxlego08.menu.command.VCommandManager;
import fr.maxlego08.menu.command.commands.CommandMenu;
import fr.maxlego08.menu.dupe.DupeListener;
import fr.maxlego08.menu.dupe.NMSDupeManager;
import fr.maxlego08.menu.dupe.PDCDupeManager;
import fr.maxlego08.menu.enchantment.ZEnchantments;
import fr.maxlego08.menu.font.EmptyFont;
import fr.maxlego08.menu.font.ItemsAdderFont;
import fr.maxlego08.menu.inventory.VInventoryManager;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.listener.AdapterListener;
import fr.maxlego08.menu.listener.SwapKeyListener;
import fr.maxlego08.menu.loader.materials.Base64Loader;
import fr.maxlego08.menu.loader.materials.EcoLoader;
import fr.maxlego08.menu.loader.materials.HeadDatabaseLoader;
import fr.maxlego08.menu.loader.materials.ItemsAdderLoader;
import fr.maxlego08.menu.loader.materials.NexoLoader;
import fr.maxlego08.menu.loader.materials.NovaLoader;
import fr.maxlego08.menu.loader.materials.OraxenLoader;
import fr.maxlego08.menu.loader.materials.SlimeFunLoader;
import fr.maxlego08.menu.loader.materials.ZHeadLoader;
import fr.maxlego08.menu.loader.materials.ZItemsLoader;
import fr.maxlego08.menu.pattern.ZPatternManager;
import fr.maxlego08.menu.placeholder.LocalPlaceholder;
import fr.maxlego08.menu.players.ZDataManager;
import fr.maxlego08.menu.players.inventory.ZInventoriesPlayer;
import fr.maxlego08.menu.save.Config;
import fr.maxlego08.menu.save.MessageLoader;
import fr.maxlego08.menu.scheduler.BukkitScheduler;
import fr.maxlego08.menu.scheduler.FoliaScheduler;
import fr.maxlego08.menu.website.Token;
import fr.maxlego08.menu.website.ZWebsiteManager;
import fr.maxlego08.menu.zcore.ZPlugin;
import fr.maxlego08.menu.zcore.enums.EnumInventory;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.nms.NmsVersion;
import fr.maxlego08.menu.zcore.utils.plugins.Metrics;
import fr.maxlego08.menu.zcore.utils.plugins.Plugins;
import fr.maxlego08.menu.zcore.utils.plugins.VersionChecker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
public class MenuPlugin extends ZPlugin {

    private static MenuPlugin instance;
    private final ButtonManager buttonManager = new ZButtonManager();
    private final InventoryManager inventoryManager = new ZInventoryManager(this);
    private final CommandManager commandManager = new ZCommandManager(this);
    private final MessageLoader messageLoader = new MessageLoader(this);
    private final DataManager dataManager = new ZDataManager(this);
    private final ZWebsiteManager websiteManager = new ZWebsiteManager(this);
    private final InventoriesPlayer inventoriesPlayer = new ZInventoriesPlayer(this);
    private final PatternManager patternManager = new ZPatternManager(this);
    private final Enchantments enchantments = new ZEnchantments();
    private CommandMenu commandMenu;
    private ZScheduler scheduler;
    private DupeManager dupeManager;
    private FontImage fontImage = new EmptyFont();

    public static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.RegionScheduler");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static MenuPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {

        instance = this;

        this.scheduler = isFolia()
                ? new FoliaScheduler(this)
                : new BukkitScheduler(this);

        this.dupeManager = NmsVersion.nmsVersion.isPdcVersion() ? new PDCDupeManager(this) : new NMSDupeManager();
        this.enchantments.register();

        this.preEnable();

        Config.getInstance().load(getPersist());

        List<String> files = new ArrayList<>();
        files.add("inventories/basic_inventory.yml");
        files.add("inventories/advanced_inventory.yml");
        files.add("inventories/pro_inventory.yml");
        files.add("inventories/example_punish.yml");

        files.add("commands/commands.yml");
        files.add("commands/punish/punish.yml");

        files.add("patterns/pattern_example.yml");
        files.add("readme.txt");

        File folder = new File(this.getDataFolder(), "inventories");

        if (!folder.exists()) folder.mkdirs();

        if (Config.generateDefaultFile) {
            files.forEach(filePath -> {
                if (!new File(this.getDataFolder(), filePath).exists()) {

                    if (NmsVersion.nmsVersion.isNewMaterial()) {
                        saveResource(filePath.replace("inventories/", "inventories/1_13/"), filePath, false);
                    } else {
                        saveResource(filePath, false);
                    }
                }
            });
        }

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

        this.registerInventory(EnumInventory.INVENTORY_DEFAULT, new InventoryDefault());
        this.registerCommand("zmenu", this.commandMenu = new CommandMenu(this), "zm");

        /* Add Listener */
        this.addListener(new AdapterListener(this));
        this.addListener(this.vinventoryManager);
        this.addListener(this.inventoriesPlayer);
        this.addSimpleListener(this.inventoryManager);

        /* Add Saver */
        this.addSave(this.messageLoader);
        this.addSave(this.inventoryManager);
        this.addSave(this.commandManager);
        this.addSave(this.dataManager);

        this.inventoryManager.registerMaterialLoader(new Base64Loader());
        if (this.isEnable(Plugins.HEADDATABASE)) {
            this.inventoryManager.registerMaterialLoader(new HeadDatabaseLoader());
        }
        if (this.isEnable(Plugins.ZHEAD)) {
            this.inventoryManager.registerMaterialLoader(new ZHeadLoader(this));
        }
        if (this.isEnable(Plugins.ORAXEN)) {
            this.inventoryManager.registerMaterialLoader(new OraxenLoader());
        }
        if (this.isEnable(Plugins.NEXO)) {
            this.inventoryManager.registerMaterialLoader(new NexoLoader());
        }
        if (this.isEnable(Plugins.ITEMSADDER)) {
            this.inventoryManager.registerMaterialLoader(new ItemsAdderLoader(this));
            this.fontImage = new ItemsAdderFont();
        }
        if (this.isEnable(Plugins.SLIMEFUN)) {
            this.inventoryManager.registerMaterialLoader(new SlimeFunLoader());
        }
        if (this.isEnable(Plugins.NOVA)) {
            this.inventoryManager.registerMaterialLoader(new NovaLoader());
        }
        if (this.isEnable(Plugins.ECO)) {
            this.inventoryManager.registerMaterialLoader(new EcoLoader());
        }

        if (this.isEnable(Plugins.ZITEMS)) {
            this.inventoryManager.registerMaterialLoader(new ZItemsLoader(this));
        }

        this.getSavers().forEach(saver -> saver.load(this.getPersist()));

        LocalPlaceholder localPlaceholder = LocalPlaceholder.getInstance();
        localPlaceholder.register("argument_", (offlinePlayer, value) -> {
            Optional<String> optional = this.commandManager.getPlayerArgument(offlinePlayer.getUniqueId(), value);
            return optional.orElse(null);
        });

        this.websiteManager.registerPlaceholders();
        localPlaceholder.register("test", (a, b) -> "&ctest");
        localPlaceholder.register("player_page", (player, s) -> String.valueOf(this.inventoryManager.getPage(player)));
        localPlaceholder.register("player_next_page", (player, s) -> String.valueOf(this.inventoryManager.getPage(player) + 1));
        localPlaceholder.register("player_previous_page", (player, s) -> String.valueOf(this.inventoryManager.getPage(player) - 1));
        localPlaceholder.register("player_max_page", (player, s) -> String.valueOf(this.inventoryManager.getMaxPage(player)));

        ((ZDataManager) this.dataManager).registerPlaceholder(localPlaceholder);

        new Metrics(this, 14951);

        File tokenFile = new File(this.getDataFolder(), "token.json");
        if (tokenFile.exists()) {
            Token.getInstance().load(this.getPersist());
        }

        // Must register after config loads
        this.addListener(new SwapKeyListener());
        new VersionChecker(this, 253).useLastVersion();

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        if (Config.enableAntiDupe) {
            this.addListener(new DupeListener(this.scheduler, this.dupeManager));
        }

        if (Config.enableDebug) {
            Logger.info("Scheduler: " + this.scheduler);
            Logger.info("DupeManager: " + this.dupeManager);
        }

        Logger.info("");
        Logger.info("You can support zMenu by upgrading your account here: https://minecraft-inventory-builder.com/account-upgrade");
        Logger.info("zMenu’s site includes an inventory editor (under development), a marketplace (already available) is a forum (under development)");
        Logger.info("");


        this.websiteManager.loadPlaceholders();
        this.dataManager.loadDefaultValues();

        this.postEnable();
    }

    @Override
    public void onDisable() {

        this.preDisable();

        this.vinventoryManager.close();

        Config.getInstance().save(getPersist());
        this.getSavers().forEach(saver -> saver.save(this.getPersist()));
        if (Token.token != null) {
            Token.getInstance().save(this.getPersist());
        }

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

    /**
     * Return the class that will manage data
     *
     * @return the dataManager
     */
    public DataManager getDataManager() {
        return dataManager;
    }

    public ZScheduler getScheduler() {
        return scheduler;
    }

    public InventoriesPlayer getInventoriesPlayer() {
        return inventoriesPlayer;
    }

    public PatternManager getPatternManager() {
        return patternManager;
    }

    public DupeManager getDupeManager() {
        return dupeManager;
    }

    public Enchantments getEnchantments() {
        return enchantments;
    }

    public FontImage getFontImage() {
        return this.fontImage;
    }
}
