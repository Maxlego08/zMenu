package fr.maxlego08.menu.loader.deluxemenu;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.ZInventory;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.exceptions.InventorySizeException;
import fr.maxlego08.menu.loader.MenuItemStackLoader;
import fr.maxlego08.menu.loader.RequirementLoader;
import fr.maxlego08.menu.save.Config;
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

public class InventoryDeluxeMenuLoader extends ZUtils implements Loader<Inventory> {

    private final MenuPlugin plugin;

    public InventoryDeluxeMenuLoader(MenuPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public Inventory load(YamlConfiguration configuration, String path, Object... objects) throws InventoryException {

        File file = (File) objects[0];
        String name = configuration.getString("name", configuration.getString("menu_title", configuration.getString("title")));
        name = name == null ? "" : name;

        int size = configuration.getInt("size", 54);
        if (size % 9 != 0) {
            throw new InventorySizeException("Size " + size + " is not valid for inventory " + file.getAbsolutePath());
        }

        List<Button> buttons = new ArrayList<>();
        Loader<Button> loader = new ButtonDeluxeMenuLoader(this.plugin, file, size);

        ConfigurationSection section = configuration.getConfigurationSection("items.");

        if (section != null) {
            for (String buttonPath : section.getKeys(false)) {
                try {
                    buttons.add(loader.load(configuration, "items." + buttonPath + ".", buttonPath));
                } catch (Exception exception) {
                    Logger.info(exception.getMessage(), Logger.LogType.ERROR);
                }
            }
        } else {
            Logger.info("items section was not found in " + file.getAbsolutePath(), Logger.LogType.ERROR);
        }

        String fileName = this.getFileNameWithoutExtension(file);

        ZInventory inventory;

        try {

            Class<? extends ZInventory> classz = (Class<? extends ZInventory>) objects[1];
            Constructor<? extends ZInventory> constructor = classz.getDeclaredConstructor(Plugin.class, String.class, String.class, int.class, List.class);
            Plugin plugin = (Plugin) objects[2];
            inventory = constructor.newInstance(plugin, name, fileName, size, buttons);

        } catch (Exception exception) {
            exception.printStackTrace();
            inventory = new ZInventory(this.plugin, name, fileName, size, buttons);
        }

        inventory.setUpdateInterval(configuration.getInt(path + "update_interval", 1) * 1000);
        inventory.setClearInventory(false);
        inventory.setFile(file);

        // Open requirement
        if (configuration.contains("open_requirement") && configuration.isConfigurationSection("open_requirement.")) {
            Loader<Requirement> requirementLoader = new RequirementLoader(this.plugin);
            inventory.setOpenRequirement(requirementLoader.load(configuration, "open_requirement.", file));
        }

        if (Config.enableDebug) {
            plugin.getLogger().warning("The inventory " + file.getPath() + " is a DeluxeMenus configuration! It is advisable to redo your configuration with zMenu!");
        }

        return inventory;
    }

    @Override
    public void save(Inventory object, YamlConfiguration configuration, String path, File file, Object... objects) {
        MenuItemStackLoader itemStackLoader = new MenuItemStackLoader(this.plugin.getInventoryManager());

        configuration.set("name", object.getName());
        configuration.set("size", object.size());

        if (object.getFillItemStack() != null) {
            itemStackLoader.save(object.getFillItemStack(), configuration, "fillItem.", file);
        }

        //TODO: FINISH THE SAVE METHOD
    }
}
