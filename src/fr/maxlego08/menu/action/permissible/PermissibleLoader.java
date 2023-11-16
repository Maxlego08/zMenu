package fr.maxlego08.menu.action.permissible;

import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.enums.PlaceholderAction;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.zcore.utils.loader.Loader;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PermissibleLoader implements Loader<List<Permissible>> {

    @Override
    public List<Permissible> load(YamlConfiguration configuration, String path, Object... objects)
            throws InventoryException {

        List<Permissible> permissibles = new ArrayList<>();

        if (configuration.isConfigurationSection(path + "permissions.")) {

            for (String key : configuration.getConfigurationSection(path + "permissions.").getKeys(false)) {

                String currentPath = path + "permissions." + key + ".";
                if (configuration.contains(currentPath + "permission")) {

                    permissibles.add(new ZPermissionPermissible(configuration.getString(currentPath + "permission")));

                } else if (configuration.contains(currentPath + "placeHolder")) {

                    String placeholder = configuration.getString(currentPath + "placeHolder", null);
                    PlaceholderAction action = PlaceholderAction
                            .from(configuration.getString(currentPath + "action", null));
                    String value = configuration.getString(currentPath + "value", null);

                    permissibles.add(new ZPlaceholderPermissible(action, placeholder, value));

                } else if (configuration.contains(currentPath + "material")) {

                    Material material = Material.valueOf(configuration.getString(currentPath + "material"));
                    int amount = configuration.getInt(currentPath + "amount", 0);

                    permissibles.add(new ZItemPermissible(material, amount));

                }
            }
        }

        return permissibles;
    }

    @Override
    public void save(List<Permissible> object, YamlConfiguration configuration, String path, File file, Object... objects) {
        // TODO Auto-generated method stub

    }

}
