package fr.maxlego08.menu.loader;

import java.io.File;
import java.util.Optional;

import org.bukkit.configuration.file.YamlConfiguration;

import fr.maxlego08.menu.MenuItemStack;
import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.PermissibleButton;
import fr.maxlego08.menu.api.button.PlaceholderButton;
import fr.maxlego08.menu.api.enums.PlaceholderAction;
import fr.maxlego08.menu.api.enums.XSound;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.utils.OpenLink;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.button.ZPermissibleButton;
import fr.maxlego08.menu.button.ZPlaceholderButton;
import fr.maxlego08.menu.exceptions.InventoryButtonException;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.sound.ZSoundOption;
import fr.maxlego08.menu.zcore.utils.loader.Loader;

public class ZButtonLoader implements Loader<Button> {

	private final MenuPlugin plugin;
	private final File file;
	private final int inventorySize;

	/**
	 * @param plugin
	 * @param file
	 * @param inventorySize
	 */
	public ZButtonLoader(MenuPlugin plugin, File file, int inventorySize) {
		super();
		this.plugin = plugin;
		this.file = file;
		this.inventorySize = inventorySize;
	}

	@Override
	public Button load(YamlConfiguration configuration, String path, Object... objects) throws InventoryException {

		String buttonType = configuration.getString(path + "type", "NONE");
		String buttonName = (String) objects[0];

		ButtonManager buttonManager = this.plugin.getButtonManager();
		Optional<ButtonLoader> optional = buttonManager.getLoader(buttonType);

		if (!optional.isPresent()) {
			throw new InventoryButtonException("Impossible to find the type " + buttonType + " for the button " + path
					+ " in inventory " + this.file.getAbsolutePath());
		}

		Loader<MenuItemStack> itemStackLoader = new MenuItemStackLoader(this.plugin.getInventoryManager());

		ButtonLoader loader = optional.get();
		ZButton button = (ZButton) loader.load(configuration, path);

		int slot = 0;
		int page;

		try {

			String slotString = configuration.getString(path + "slot", "0");
			if (slotString != null && slotString.contains("-")) {

				String[] strings = slotString.split("-");
				page = Integer.valueOf(strings[0]);
				slot = Integer.valueOf(strings[1]);

			} else {
				slot = configuration.getInt(path + "slot", 0);
				page = configuration.getInt(path + "page", 1);
			}

		} catch (Exception e) {
			slot = configuration.getInt(path + "slot", 0);
			page = configuration.getInt(path + "page", 1);
		}

		page = page < 1 ? 1 : page;
		slot = slot + ((page - 1) * this.inventorySize);

		button.setSlot(slot);
		button.setPermanent(configuration.getBoolean(path + "isPermanent", false));
		button.setCloseInventory(configuration.getBoolean(path + "closeInventory", false));
		button.setItemStack(itemStackLoader.load(configuration, path + "item."));
		button.setButtonName(buttonName);
		button.setMessages(configuration.getStringList(path + "messages"));

		String playerHead = configuration.getString(path + "playerHead",
				configuration.getString(path + "item.playerHead", null));
		button.setPlayerHead(playerHead);

		button.setUpdated(configuration.getBoolean(path + "update", false));
		button.setRefreshOnClick(configuration.getBoolean(path + "refreshOnClick", false));

		if (configuration.contains(path + "openLink")) {

			Loader<OpenLink> loaderLink = new OpenLinkLoader();
			button.setOpenLink(loaderLink.load(configuration, path + "openLink."));

		}

		Optional<XSound> optionalXSound = XSound.matchXSound(configuration.getString(path + "sound", null));

		if (optionalXSound.isPresent()) {
			XSound xSound = optionalXSound.get();
			float pitch = Float.valueOf(configuration.getString(path + "pitch", "1.0f"));
			float volume = Float.valueOf(configuration.getString(path + "volume", "1.0f"));
			button.setSoundOption(new ZSoundOption(xSound, pitch, volume));
		}

		if (button instanceof PermissibleButton) {

			ZPermissibleButton permissibleButton = (ZPermissibleButton) button;
			permissibleButton.setPermission(configuration.getString(path + "permission", null));

			if (configuration.contains(path + "else")) {

				Button elseButton = this.load(configuration, path + "else.", buttonName + ".else");
				permissibleButton.setElseButton(elseButton);

				if (elseButton instanceof PermissibleButton) {
					ZPermissibleButton elsePermissibleButton = (ZPermissibleButton) elseButton;
					elsePermissibleButton.setParentButton(button);
				}

			}

		}

		if (button instanceof PlaceholderButton) {

			ZPlaceholderButton placeholderButton = (ZPlaceholderButton) button;
			placeholderButton.setPlaceholder(configuration.getString(path + "placeHolder", null));
			placeholderButton.setAction(PlaceholderAction.from(configuration.getString(path + "action", null)));
			placeholderButton.setValue(configuration.getString(path + "value", null));

		}

		return button;
	}

	@Override
	public void save(Button object, YamlConfiguration configuration, String path, Object... objects) {

	}

}
