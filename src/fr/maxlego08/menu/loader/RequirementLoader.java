package fr.maxlego08.menu.loader;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.requirement.ZRequirement;
import fr.maxlego08.menu.zcore.utils.loader.Loader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.ClickType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequirementLoader implements Loader<Requirement> {

    private final MenuPlugin plugin;

    public RequirementLoader(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Requirement load(YamlConfiguration configuration, String path, Object... objects) throws InventoryException {

        File file = (File) objects[0];
        ButtonManager buttonManager = this.plugin.getButtonManager();
        List<Permissible> permissibles = buttonManager.loadPermissible((List<Map<String, Object>>) configuration.getList(path + "requirements", configuration.getList(path + "requirement", new ArrayList<>())), path, file);
        List<Action> successActions = buttonManager.loadActions((List<Map<String, Object>>) configuration.getList(path + "success", new ArrayList<>()), path+"success", file);
        List<Action> denyActions = buttonManager.loadActions((List<Map<String, Object>>) configuration.getList(path + "deny", new ArrayList<>()), path+"deny", file);
        List<ClickType> clickTypes = configuration.getStringList(path + "clicks").stream().map(String::toUpperCase)
                .map(ClickType::valueOf).collect(Collectors.toList());
        int miniumRequirement = configuration.getInt(path + "miniumRequirement", permissibles.size());

        return new ZRequirement(miniumRequirement, permissibles, denyActions, successActions, clickTypes);
    }

    @Override
    public void save(Requirement object, YamlConfiguration configuration, String path, File file, Object... objects) {

    }
}
