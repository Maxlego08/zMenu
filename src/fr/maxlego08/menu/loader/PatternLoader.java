package fr.maxlego08.menu.loader;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.pattern.Pattern;
import fr.maxlego08.menu.exceptions.InventoryButtonException;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.exceptions.InventorySizeException;
import fr.maxlego08.menu.pattern.ZPattern;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.loader.Loader;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PatternLoader implements Loader<Pattern> {

    private final MenuPlugin plugin;

    public PatternLoader(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Pattern load(YamlConfiguration configuration, String path, Object... objects) throws InventoryException {

        File file = (File) objects[0];
        String name = configuration.getString("name");

        if (name == null) {
            Logger.info("name is null for pattern " + file.getAbsolutePath(), Logger.LogType.ERROR);
            return null;
        }

        int size = configuration.getInt("size", 54);
        if (size % 9 != 0) {
            throw new InventorySizeException("Size " + size + " is not valid for inventory " + file.getAbsolutePath());
        }

        if (!configuration.contains("items") || !configuration.isConfigurationSection("items.")) {
            throw new InventoryButtonException(
                    "Impossible to find the list of buttons for the " + file.getAbsolutePath() + " pattern !");
        }

        Loader<Button> loader = new ZButtonLoader(this.plugin, file, size);
        List<Button> buttons = new ArrayList<>();
        ConfigurationSection section = configuration.getConfigurationSection("items.");

        if (section != null) {
            for (String buttonPath : section.getKeys(false)) {
                buttons.add(loader.load(configuration, "items." + buttonPath + ".", buttonPath));
            }
        } else {
            Logger.info("items section was not found in " + file.getAbsolutePath(), Logger.LogType.ERROR);
        }

        return new ZPattern(name, buttons, size);
    }

    @Override
    public void save(Pattern object, YamlConfiguration configuration, String path, Object... objects) {
    }
}
