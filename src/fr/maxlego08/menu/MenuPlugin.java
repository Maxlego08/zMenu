package fr.maxlego08.menu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.plugin.ServicePriority;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.website.WebsiteManager;
import fr.maxlego08.menu.command.VCommandManager;
import fr.maxlego08.menu.command.commands.CommandMenu;
import fr.maxlego08.menu.inventory.VInventoryManager;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.listener.AdapterListener;
import fr.maxlego08.menu.loader.materials.HeadDatabaseLoader;
import fr.maxlego08.menu.placeholder.LocalPlaceholder;
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
	private CommandMenu commandMenu;

	private final WebsiteManager websiteManager = new ZWebsiteManager(this);
	private final Token token = new Token();

	@Override
	public void onEnable() {

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

		this.registerInventory(EnumInventory.INVENTORY_DEFAULT, new InventoryDefault());
		this.registerCommand("zmenu", this.commandMenu = new CommandMenu(this), "zm");

		/* Add Listener */
		this.addListener(new AdapterListener(this));
		this.addListener(this.vinventoryManager);

		/* Add Saver */
		this.addSave(Config.getInstance());
		this.addSave(this.messageLoader);
		this.addSave(this.inventoryManager);
		this.addSave(this.commandManager);

		if (this.isEnable(Plugins.HEADDATABASE)) {

			this.inventoryManager.registerMaterialLoader(new HeadDatabaseLoader());

		}
		this.getSavers().forEach(saver -> saver.load(this.getPersist()));

		LocalPlaceholder localPlaceholder = LocalPlaceholder.getInstance();
		localPlaceholder.register("argument_", (player, value) -> {
			Optional<String> optional = this.commandManager.getPlayerArgument(player, value);
			return optional.isPresent() ? optional.get() : null;
		});

		new Metrics(this, 14951);

		File tokenFile = new File(this.getDataFolder(), "token.json");
		if (tokenFile.exists()) {
			this.token.load(this.getPersist());
		}

		this.postEnable();
	}

	@Override
	public void onDisable() {

		this.preDisable();

		this.vinventoryManager.close();

		this.getSavers().forEach(saver -> saver.save(this.getPersist()));
		if (Token.token != null) {
			this.token.save(this.getPersist());
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
}
