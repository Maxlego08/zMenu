package fr.maxlego08.menu.action;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.ClickType;

import fr.maxlego08.menu.api.action.Action;
import fr.maxlego08.menu.api.action.ActiondClick;
import fr.maxlego08.menu.api.action.permissible.Permissible;
import fr.maxlego08.menu.api.action.permissible.PermissibleLoader;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.zcore.utils.loader.Loader;

public class ActionLoader implements Loader<Action> {

	@Override
	public Action load(YamlConfiguration configuration, String path, Object... objects) throws InventoryException {

		Loader<ActiondClick> loader = new ActionClickLoader();

		List<ClickType> clickTypes = configuration.getStringList(path + "clicks").stream().map(String::toUpperCase)
				.map(ClickType::valueOf).collect(Collectors.toList());

		ActiondClick allow = null;
		ActiondClick deny = null;

		if (configuration.contains(path + "allow")) {
			allow = loader.load(configuration, path + "allow.");
		}

		if (configuration.contains(path + "deny")) {
			deny = loader.load(configuration, path + "deny.");
		}

		Loader<List<Permissible>> loaderPermissible = new PermissibleLoader();
		List<Permissible> permissibles = loaderPermissible.load(configuration, path);

		System.out.println(permissibles);
		System.out.println(allow);
		System.out.println(deny);
		System.out.println(clickTypes);

		return new ZAction(clickTypes, allow, deny, permissibles);
	}

	@Override
	public void save(Action object, YamlConfiguration configuration, String path, Object... objects) {
		// TODO Auto-generated method stub

	}

}
