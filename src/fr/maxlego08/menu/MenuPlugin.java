package fr.maxlego08.menu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.maxlego08.menu.api.pattern.PatternManager;
import fr.maxlego08.menu.api.players.inventory.InventoriesPlayer;
import fr.maxlego08.menu.pattern.ZPatternManager;
import fr.maxlego08.menu.players.inventory.ZInventoriesPlayer;
import fr.maxlego08.menu.scheduler.BukkitScheduler;
import fr.maxlego08.menu.scheduler.FoliaScheduler;
import fr.maxlego08.menu.api.scheduler.ZScheduler;
import fr.maxlego08.menu.zcore.utils.plugins.VersionChecker;
import org.bukkit.plugin.ServicePriority;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.website.WebsiteManager;
import fr.maxlego08.menu.command.VCommandManager;
import fr.maxlego08.menu.command.commands.CommandMenu;
import fr.maxlego08.menu.inventory.VInventoryManager;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.listener.AdapterListener;
import fr.maxlego08.menu.loader.materials.HeadDatabaseLoader;
import fr.maxlego08.menu.placeholder.LocalPlaceholder;
import fr.maxlego08.menu.players.ZDataManager;
import fr.maxlego08.menu.save.Config;
import fr.maxlego08.menu.save.MessageLoader;
import fr.maxlego08.menu.website.Token;
import fr.maxlego08.menu.website.ZWebsiteManager;
import fr.maxlego08.menu.zcore.ZPlugin;
import fr.maxlego08.menu.zcore.enums.EnumInventory;
import fr.maxlego08.menu.zcore.utils.nms.NMSUtils;
import fr.maxlego08.menu.zcore.utils.plugins.Metrics;
import fr.maxlego08.menu.zcore.utils.plugins.Plugins;

/**
 * System to create your plugins very simply Projet:
 * https://github.com/Maxlego08/TemplatePlugin
 * 
 * @author Maxlego08
 *
 */
public class MenuPlugin extends ZPlugin {

	private final ButtonManager buttonManager = new ZButtonManager();
	private final InventoryManager inventoryManager = new ZInventoryManager(this);
	private final CommandManager commandManager = new ZCommandManager(this);
	private final MessageLoader messageLoader = new MessageLoader(this);
	private final DataManager dataManager = new ZDataManager(this);
	private CommandMenu commandMenu;

	private final WebsiteManager websiteManager = new ZWebsiteManager(this);
	private final InventoriesPlayer inventoriesPlayer = new ZInventoriesPlayer(this);
	private ZScheduler scheduler;
	private final PatternManager patternManager = new ZPatternManager(this);

	private static MenuPlugin instance;

	@Override
	public void onEnable() {

		instance = this;

		this.scheduler = isFolia()
				? new FoliaScheduler(this)
				: new BukkitScheduler(this);

		this.preEnable();

		List<String> files = new ArrayList<String>();
		files.add("inventories/example.yml");
		files.add("inventories/example_shop.yml");
		files.add("inventories/example_punish.yml");
		files.add("inventories/test/example2.yml");
		files.add("inventories/test/test3/example3.yml");

		files.add("commands/commands.yml");
		files.add("commands/example/example.yml");
		files.add("commands/punish/punish.yml");

		File folder = new File(this.getDataFolder(), "inventories");

		if (!folder.exists()) {
			files.forEach(e -> {
				if (!new File(this.getDataFolder(), e).exists()) {

					if (NMSUtils.isNewVersion()) {
						saveResource(e.replace("inventories/", "inventories/1_13/"), e, false);
					} else {
						saveResource(e, false);
					}
				}
			});
		}

		this.zcommandManager = new VCommandManager(this);
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
		this.getServer().getServicesManager().register(PatternManager.class, this.patternManager, this,
				ServicePriority.Highest);

		this.registerInventory(EnumInventory.INVENTORY_DEFAULT, new InventoryDefault());
		this.registerCommand("zmenu", this.commandMenu = new CommandMenu(this), "zm");

		/* Add Listener */
		this.addListener(new AdapterListener(this));
		this.addListener(this.vinventoryManager);
		this.addListener(this.inventoriesPlayer);

		/* Add Saver */
		this.addSave(Config.getInstance());
		this.addSave(this.messageLoader);
		this.addSave(this.inventoryManager);
		this.addSave(this.commandManager);
		this.addSave(this.dataManager);

		if (this.isEnable(Plugins.HEADDATABASE)) {

			this.inventoryManager.registerMaterialLoader(new HeadDatabaseLoader());

		}
		this.getSavers().forEach(saver -> saver.load(this.getPersist()));

		LocalPlaceholder localPlaceholder = LocalPlaceholder.getInstance();
		localPlaceholder.register("argument_", (player, value) -> {
			Optional<String> optional = this.commandManager.getPlayerArgument(player, value);
			return optional.orElse(null);
		});

		((ZDataManager) this.dataManager).registerPlaceholder(localPlaceholder);

		new Metrics(this, 14951);

		File tokenFile = new File(this.getDataFolder(), "token.json");
		if (tokenFile.exists()) {
			Token.getInstance().load(this.getPersist());
		}

		// new VersionChecker(this, 253).useLastVersion();

		this.postEnable();
	}

	@Override
	public void onDisable() {

		this.preDisable();

		this.vinventoryManager.close();

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

	public PatternManager getPatternManager() {
		return patternManager;
	}
}
