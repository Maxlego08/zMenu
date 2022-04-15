package fr.maxlego08.menu.loader;

import org.bukkit.configuration.file.YamlConfiguration;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.button.buttons.ZNoneButton;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.zcore.utils.loader.Loader;

public class ButtonLoader implements Loader<Button> {

	@Override
	public Button load(YamlConfiguration configuration, String path, Object... objects) throws InventoryException {
		return new ZNoneButton();
	}

	@Override
	public void save(Button object, YamlConfiguration configuration, String path, Object... objects) {
		
	}

}
