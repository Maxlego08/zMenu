package fr.maxlego08.menu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.ServicePriority;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.command.CommandManager;
import fr.maxlego08.menu.command.commands.CommandMenu;
import fr.maxlego08.menu.inventory.VInventoryManager;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.listener.AdapterListener;
import fr.maxlego08.menu.save.Config;
import fr.maxlego08.menu.save.MessageLoader;
import fr.maxlego08.menu.zcore.ZPlugin;
import fr.maxlego08.menu.zcore.enums.EnumInventory;
import fr.maxlego08.menu.zcore.utils.plugins.Metrics;

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
	private final MessageLoader messageLoader = new MessageLoader(this);

	@Override
	public void onEnable() {

		this.preEnable();

		List<String> files = new ArrayList<String>();
		files.add("inventories/example.yml");
		files.add("inventories/test/example2.yml");
		files.add("inventories/test/test3/example3.yml");

		files.forEach(e -> {
			if (!new File(this.getDataFolder(), e).exists()) {
				saveResource(e, false);
			}
		});
		
		this.commandManager = new CommandManager(this);
		this.vinventoryManager = new VInventoryManager(this);

		this.getServer().getServicesManager().register(InventoryManager.class, this.inventoryManager, this, ServicePriority.Highest);
		this.getServer().getServicesManager().register(ButtonManager.class, this.buttonManager, this, ServicePriority.Highest);
		
		this.registerInventory(EnumInventory.INVENTORY_DEFAULT, new InventoryDefault());
		this.registerCommand("zmenu", new CommandMenu(this), "zm");

		/* Add Listener */
		this.addListener(new AdapterListener(this));
		this.addListener(this.vinventoryManager);

		/* Add Saver */
		this.addSave(Config.getInstance());
		this.addSave(this.messageLoader);
		this.addSave(this.inventoryManager);

		this.getSavers().forEach(saver -> saver.load(this.getPersist()));

		new Metrics(this, 14951);

		this.postEnable();
	}

	@Override
	public void onDisable() {

		this.preDisable();

		this.getSavers().forEach(saver -> saver.save(this.getPersist()));

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

}
