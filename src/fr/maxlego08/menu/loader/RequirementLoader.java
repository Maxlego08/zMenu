package fr.maxlego08.menu.loader;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.action.permissible.Permissible;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.requirement.ZRequirement;
import fr.maxlego08.menu.zcore.utils.loader.Loader;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequirementLoader implements Loader<Requirement> {

    private final MenuPlugin plugin;

    public RequirementLoader(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Requirement load(YamlConfiguration configuration, String path, Object... objects) throws InventoryException {

        File file = (File) objects[0];
        ButtonManager buttonManager = this.plugin.getButtonManager();
        List<Permissible> permissibles = buttonManager.loadPermissible((List<Map<String, Object>>) configuration.getList(path + "requirements", new ArrayList<>()), path);
        List<Action> successActions = buttonManager.loadActions((List<Map<String, Object>>) configuration.getList(path + "success", new ArrayList<>()), path, file);
        List<Action> denyActions = buttonManager.loadActions((List<Map<String, Object>>) configuration.getList(path + "deny", new ArrayList<>()), path, file);
        int miniumRequirement = configuration.getInt(path + "miniumRequirement", permissibles.size());

        return new ZRequirement(miniumRequirement, permissibles, denyActions, successActions);
    }

    @Override
    public void save(Requirement object, YamlConfiguration configuration, String path, File file, Object... objects) {

    }
}
