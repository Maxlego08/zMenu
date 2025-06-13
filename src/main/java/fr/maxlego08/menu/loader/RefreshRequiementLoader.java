package fr.maxlego08.menu.loader;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.requirement.RefreshRequirement;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.requirement.ZRefreshRequirement;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RefreshRequiementLoader implements Loader<RefreshRequirement> {

    private final MenuPlugin plugin;

    public RefreshRequiementLoader(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public RefreshRequirement load(YamlConfiguration configuration, String path, Object... objects) throws InventoryException {

        File file = (File) objects[0];
        ButtonManager buttonManager = this.plugin.getButtonManager();
        List<Permissible> permissibles = buttonManager.loadPermissible((List<Map<String, Object>>) configuration.getList(path + "requirements", configuration.getList(path + "requirement", new ArrayList<>())), path, file);
        List<Permissible> enablePermissibles = buttonManager.loadPermissible((List<Map<String, Object>>) configuration.getList(path + "enable_requirements", configuration.getList(path + "enable-requirements", new ArrayList<>())), path, file);
        boolean task = configuration.getBoolean(path + "task", false);
        boolean refreshLore = configuration.getBoolean(path + "refreshLore", configuration.getBoolean(path + "refresh-lore", false));
        boolean refreshName = configuration.getBoolean(path + "refreshName", configuration.getBoolean(path + "refresh-name", false));
        boolean refreshButton = configuration.getBoolean(path + "refreshButton", configuration.getBoolean(path + "refresh-button", false));
        int updateInterval = configuration.getInt(path + "updateInterval", configuration.getInt(path + "update-interval", 500));

        return new ZRefreshRequirement(enablePermissibles, permissibles, task, refreshLore, refreshName, refreshButton, updateInterval);
    }

    @Override
    public void save(RefreshRequirement object, YamlConfiguration configuration, String path, File file, Object... objects) {

    }
}
