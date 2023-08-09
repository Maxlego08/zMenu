package fr.maxlego08.menu.loader;

import fr.maxlego08.menu.MenuItemStack;
import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.ZInventory;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.pattern.Pattern;
import fr.maxlego08.menu.api.pattern.PatternManager;
import fr.maxlego08.menu.exceptions.InventoryButtonException;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.exceptions.InventorySizeException;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import fr.maxlego08.menu.zcore.utils.loader.Loader;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryLoader extends ZUtils implements Loader<Inventory> {

    private final MenuPlugin plugin;

    /**
     * @param plugin
     */
    public InventoryLoader(MenuPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public Inventory load(YamlConfiguration configuration, String path, Object... objects) throws InventoryException {

        File file = (File) objects[0];
        String name = configuration.getString("name");
        name = name == null ? "" : name;

        int size = configuration.getInt("size", 54);
        if (size % 9 != 0) {
            throw new InventorySizeException("Size " + size + " is not valid for inventory " + file.getAbsolutePath());
        }

        if (!configuration.contains("items") || !configuration.isConfigurationSection("items.")) {
            throw new InventoryButtonException(
                    "Impossible to find the list of buttons for the " + file.getAbsolutePath() + " inventory!");
        }

        List<Button> buttons = new ArrayList<>();
        Loader<Button> loader = new ZButtonLoader(this.plugin, file, size);

        Loader<MenuItemStack> itemStackLoader = new MenuItemStackLoader(this.plugin.getInventoryManager());
        MenuItemStack itemStack = null;
        try {
            if (configuration.contains("fillItem")) {
                itemStack = itemStackLoader.load(configuration, "fillItem.");
            }
        } catch (Exception ignored) {
        }

        ConfigurationSection section = configuration.getConfigurationSection("items.");

        if (section != null) {
            for (String buttonPath : section.getKeys(false)) {
                buttons.add(loader.load(configuration, "items." + buttonPath + ".", buttonPath));
            }
        } else {
            Logger.info("items section was not found in " + file.getAbsolutePath(), Logger.LogType.ERROR);
        }

        PatternManager patternManager = this.plugin.getPatternManager();
        List<Pattern> patterns = configuration.getStringList("patterns").stream().filter(pName -> patternManager.getPattern(pName).isPresent()).map(pName -> patternManager.getPattern(pName).get()).collect(Collectors.toList());

        String fileName = this.getFileNameWithoutExtension(file);

        ZInventory inventory;

        try {

            Class<? extends ZInventory> classz = (Class<? extends ZInventory>) objects[1];
            Constructor<? extends ZInventory> constructor = classz.getDeclaredConstructor(Plugin.class, String.class,
                    String.class, int.class, List.class);
            Plugin plugin = (Plugin) objects[2];
            inventory = constructor.newInstance(plugin, name, fileName, size, buttons);

        } catch (Exception e) {
            e.printStackTrace();
            inventory = new ZInventory(this.plugin, name, fileName, size, buttons);
        }

        inventory.setFillItemStack(itemStack);
        inventory.setUpdateInterval(configuration.getInt(path + "updateInterval", 1));
        inventory.setClearInventory(configuration.getBoolean(path + "clearInventory", false));
        inventory.setFile(file);
        inventory.setPatterns(patterns);

        return inventory;
    }

    @Override
    public void save(Inventory object, YamlConfiguration configuration, String path, File file, Object... objects) {
        ZButtonLoader buttonLoader = new ZButtonLoader(this.plugin, file, object.size());
        MenuItemStackLoader itemStackLoader = new MenuItemStackLoader(this.plugin.getInventoryManager());

        configuration.set("name", object.getName());
        configuration.set("size", object.size());

        if (object.getFillItemStack() != null) {
            itemStackLoader.save(object.getFillItemStack(), configuration, "fillItem.", file);
        }

        //TODO: FINISH THE SAVE METHOD
    }

}
