package fr.maxlego08.menu;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.dupe.DupeManager;
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
import fr.maxlego08.menu.inventory.VInventoryManager;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.listener.AdapterListener;
import fr.maxlego08.menu.listener.SwapKeyListener;
import fr.maxlego08.menu.loader.materials.*;
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
import fr.maxlego08.menu.zcore.utils.nms.NMSUtils;
import fr.maxlego08.menu.zcore.utils.nms.NmsVersion;
import fr.maxlego08.menu.zcore.utils.plugins.Metrics;
import fr.maxlego08.menu.zcore.utils.plugins.Plugins;
import fr.maxlego08.menu.zcore.utils.plugins.VersionChecker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

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
    private final WebsiteManager websiteManager = new ZWebsiteManager(this);
    private final InventoriesPlayer inventoriesPlayer = new ZInventoriesPlayer(this);
    private final PatternManager patternManager = new ZPatternManager(this);
    private CommandMenu commandMenu;
    private ZScheduler scheduler;
    private DupeManager dupeManager;

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

        if (!folder.exists()) {
            folder.mkdirs();

            if (Config.generateDefaultFile) {
                files.forEach(filePath -> {
                    if (!new File(this.getDataFolder(), filePath).exists()) {

                        if (NMSUtils.isNewVersion()) {
                            saveResource(filePath.replace("inventories/", "inventories/1_13/"), filePath, false);
                        } else {
                            saveResource(filePath, false);
                        }
                    }
                });
            }
        }

        this.zCommandManager = new VCommandManager(this);
        this.vinventoryManager = new VInventoryManager(this);

        this.getServer().getServicesManager().register(InventoryManager.class, this.inventoryManager, this,
                ServicePriority.Highest);
        this.getServer().getServicesManager().register(ButtonManager.class, this.buttonManager, this,
                ServicePriority.Highest);
        this.getServer().getServicesManager().register(CommandManager.class, this.commandManager, this,
                ServicePriority.Highest);
        this.getServer().getServicesManager().register(WebsiteManager.class, this.websiteManager, this,
                ServicePriority.Highest);
        this.getServer().getServicesManager().register(DataManager.class, this.dataManager, this,
                ServicePriority.Highest);
        this.getServer().getServicesManager().register(InventoriesPlayer.class, this.inventoriesPlayer, this,
                ServicePriority.Highest);
        this.getServer().getServicesManager().register(PatternManager.class, this.patternManager, this, ServicePriority.Highest);
        this.getServer().getServicesManager().register(DupeManager.class, this.dupeManager, this, ServicePriority.Highest);

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
        if (this.isEnable(Plugins.ORAXEN)) {
            this.inventoryManager.registerMaterialLoader(new OraxenLoader());
        }
        if (this.isEnable(Plugins.ITEMSADDER)) {
            this.inventoryManager.registerMaterialLoader(new ItemsAdderLoader());
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

        this.getSavers().forEach(saver -> saver.load(this.getPersist()));

        LocalPlaceholder localPlaceholder = LocalPlaceholder.getInstance();
        localPlaceholder.register("argument_", (player, value) -> {
            Optional<String> optional = this.commandManager.getPlayerArgument(player, value);
            return optional.orElse(null);
        });

        localPlaceholder.register("test", (a,b) -> "&ctest");

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

        if (Config.enableDebug){
            Logger.info("Scheduler: " + this.scheduler);
            Logger.info("DupeManager: " + this.dupeManager);
        }

        Logger.info("");
        Logger.info("You can support zMenu by upgrading your account here: https://minecraft-inventory-builder.com/account-upgrade");
        Logger.info("zMenu’s site includes an inventory editor (under development), a marketplace (already available) is a forum (under development)");
        Logger.info("");

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
    public WebsiteManager getWebsiteManager() {
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
}
