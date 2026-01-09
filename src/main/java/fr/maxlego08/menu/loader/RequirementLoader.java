package fr.maxlego08.menu.loader;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.pattern.ActionPattern;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.requirement.ZRequirement;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.ClickType;
import org.jspecify.annotations.NonNull;

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
    public Requirement load(@NonNull YamlConfiguration configuration, @NonNull String path, Object... objects) throws InventoryException {

        File file = (File) objects[0];
        List<ActionPattern> actionPatterns = new ArrayList<>();
        if (objects.length > 1) {
            try {
                actionPatterns = (List<ActionPattern>) objects[1];
            } catch (ClassCastException ignored){}
        }
        ButtonManager buttonManager = this.plugin.getButtonManager();
        List<Permissible> permissibles = buttonManager.loadPermissible((List<Map<String, Object>>) configuration.getList(path + "requirements", configuration.getList(path + "requirement", new ArrayList<>())), path, file);
        List<Action> successActions = buttonManager.loadActions((List<Map<String, Object>>) configuration.getList(path + "success", new ArrayList<>()), path + "success", file, actionPatterns,true,false);
        List<Action> denyActions = buttonManager.loadActions((List<Map<String, Object>>) configuration.getList(path + "deny", new ArrayList<>()), path + "deny", file, actionPatterns,false,false);
        List<ClickType> clickTypes = this.plugin.getInventoryManager().loadClicks(configuration.getStringList(path + "clicks"));
        int miniumRequirement = configuration.getInt(path + "minimumRequirement", configuration.getInt(path + "minimum-requirement", permissibles.size()));

        return new ZRequirement(miniumRequirement, permissibles, denyActions, successActions, clickTypes);
    }

    @Override
    public void save(Requirement object, @NonNull YamlConfiguration configuration, @NonNull String path, File file, Object... objects) {

    }
}
