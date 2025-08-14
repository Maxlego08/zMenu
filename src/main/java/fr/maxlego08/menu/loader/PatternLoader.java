package fr.maxlego08.menu.loader;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.api.exceptions.InventoryButtonException;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.exceptions.InventorySizeException;
import fr.maxlego08.menu.api.pattern.Pattern;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.pattern.ZPattern;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PatternLoader extends ZUtils implements Loader<Pattern> {

    private final ZMenuPlugin plugin;

    public PatternLoader(ZMenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Pattern load(YamlConfiguration configuration, String path, Object... objects) throws InventoryException {

        File file = (File) objects[0];
        String name = configuration.getString("name");
        boolean enableMultiPage = configuration.getBoolean("enableMultiPage", configuration.getBoolean("enable-multi-page", false));

        if (configuration.getString("type", "INVENTORY").equalsIgnoreCase("button")) {
            return null;
        }

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

        Map<Character, List<Integer>> matrix = generateMatrix(configuration.getStringList("matrix"));
        if (!matrix.isEmpty()) size = getInventorySizeByMatrix(configuration.getStringList("matrix"));

        Loader<Button> loader = new ZButtonLoader(this.plugin, file, size, matrix);
        List<Button> buttons = new ArrayList<>();
        ConfigurationSection section = configuration.getConfigurationSection("items.");

        if (section != null) {
            for (String buttonPath : section.getKeys(false)) {
                buttons.add(loader.load(configuration, "items." + buttonPath + ".", buttonPath));
            }
        } else {
            if (Config.enableDebug) {
                Logger.info("items section was not found in " + file.getAbsolutePath(), Logger.LogType.ERROR);
            }
        }

        return new ZPattern(name, buttons, size, enableMultiPage);
    }

    @Override
    public void save(Pattern object, YamlConfiguration configuration, String path, File file, Object... objects) {

        configuration.set("name", object.name());
        configuration.set("size", object.inventorySize());

        for (Button button : object.buttons()) {
            // TODO: SAVE BUTTONS
        }

        try {
            configuration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
