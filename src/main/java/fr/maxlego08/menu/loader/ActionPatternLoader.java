package fr.maxlego08.menu.loader;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.pattern.ActionPattern;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.pattern.ZActionsPattern;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;

import java.io.File;

public class ActionPatternLoader extends ZUtils implements Loader<ActionPattern> {
    private final ZMenuPlugin plugin;

    public ActionPatternLoader(ZMenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ActionPattern load(@NonNull YamlConfiguration configuration, @NonNull String path, Object... objects) throws InventoryException {
        File file = (File) objects[0];
        String name = configuration.getString("name");
        if (name == null) {
            throw new InventoryException("ActionPattern name is missing in configuration at path: " + path);
        }
        return new ZActionsPattern(name,plugin.getButtonManager().loadActions(configuration,"actions", file),plugin.getButtonManager().loadActions(configuration,"deny-actions", file));
    }

    @Override
    public void save(ActionPattern object, @NonNull YamlConfiguration configuration, @NonNull String path, File file, Object... objects) {

    }
}
