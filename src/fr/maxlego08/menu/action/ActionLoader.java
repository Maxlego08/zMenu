package fr.maxlego08.menu.action;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.action.loader.ActionClickLoader;
import fr.maxlego08.menu.action.permissible.PermissibleLoader;
import fr.maxlego08.menu.api.action.Action;
import fr.maxlego08.menu.api.action.ActionClick;
import fr.maxlego08.menu.api.action.permissible.Permissible;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.zcore.utils.loader.Loader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.ClickType;

import java.util.List;
import java.util.stream.Collectors;

public class ActionLoader implements Loader<Action> {

    private final MenuPlugin plugin;

    /**
     * @param plugin
     */
    public ActionLoader(MenuPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public Action load(YamlConfiguration configuration, String path, Object... objects) throws InventoryException {

        Loader<ActionClick> loader = new ActionClickLoader(this.plugin);

        List<ClickType> clickTypes = configuration.getStringList(path + "clicks").stream().map(String::toUpperCase)
                .map(ClickType::valueOf).collect(Collectors.toList());

        ActionClick allow = null;
        ActionClick deny = null;

        if (configuration.contains(path + "allow")) {
            allow = loader.load(configuration, path + "allow.");
        }

        if (configuration.contains(path + "deny")) {
            deny = loader.load(configuration, path + "deny.");
        }

        Loader<List<Permissible>> loaderPermissible = new PermissibleLoader();
        List<Permissible> permissibles = loaderPermissible.load(configuration, path);

        return new ZAction(clickTypes, allow, deny, permissibles);
    }

    @Override
    public void save(Action object, YamlConfiguration configuration, String path, Object... objects) {
        // TODO Auto-generated method stub

    }

}
