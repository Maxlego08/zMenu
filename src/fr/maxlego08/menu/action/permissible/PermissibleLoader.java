package fr.maxlego08.menu.action.permissible;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import fr.maxlego08.menu.api.action.permissible.Permissible;
import fr.maxlego08.menu.api.enums.PlaceholderAction;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.zcore.utils.loader.Loader;

public class PermissibleLoader implements Loader<List<Permissible>> {

	@Override
	public List<Permissible> load(YamlConfiguration configuration, String path, Object... objects)
			throws InventoryException {

		List<Permissible> permissibles = new ArrayList<Permissible>();

		if (configuration.isConfigurationSection(path + "permissions.")) {

			for (String key : configuration.getConfigurationSection(path + "permissions.").getKeys(false)) {

				String currentPath = path + "permissions." + key + ".";
				if (configuration.contains(currentPath + "permission")) {

					String permission = configuration.getString(currentPath + "permission");
					boolean isReverse = permission != null && permission.startsWith("!");
					if (isReverse) {
						permission = permission.substring(1, permission.length());
					}

					permissibles.add(new ZPermissionPermissible(permission, isReverse));

				} else {

					String placeholder = configuration.getString(currentPath + "placeHolder", null);
					PlaceholderAction action = PlaceholderAction.from(configuration.getString(currentPath + "action", null));
					String value = configuration.getString(currentPath + "value", null);

					permissibles.add(new ZPlaceholderPermissible(action, placeholder, value));

				}

			}

		}

		return permissibles;
	}

	@Override
	public void save(List<Permissible> object, YamlConfiguration configuration, String path, Object... objects) {
		// TODO Auto-generated method stub

	}

}
