package fr.maxlego08.menu.loader;

import fr.maxlego08.menu.MenuItemStack;
import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.ZInventory;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import fr.maxlego08.menu.api.pattern.Pattern;
import fr.maxlego08.menu.api.pattern.PatternManager;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.OpenWithItem;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.exceptions.InventorySizeException;
import fr.maxlego08.menu.exceptions.InventoryTypeException;
import fr.maxlego08.menu.itemstack.FullSimilar;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import fr.maxlego08.menu.zcore.utils.loader.Loader;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class InventoryLoader extends ZUtils implements Loader<Inventory> {

    private final MenuPlugin plugin;

    public InventoryLoader(MenuPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public Inventory load(YamlConfiguration configuration, String path, Object... objects) throws InventoryException {

        File file = (File) objects[0];
        String name = configuration.getString("name", configuration.getString("title"));
        name = name == null ? "" : name;
        InventoryType inventoryType;
        int size;
        String nameType = configuration.getString("type","CHEST").toUpperCase();
        try {
            inventoryType = InventoryType.valueOf(nameType);
            if (inventoryType == InventoryType.CRAFTING || inventoryType == InventoryType.PLAYER) {
                throw new InventoryTypeException("Type Inventory " + nameType + " can't use for the moment for inventory " + file.getAbsolutePath());
            }
            size = inventoryType.getDefaultSize();
        } catch (IllegalArgumentException exception) {
            throw new InventoryTypeException("Type Inventory " + nameType + " is not valid for inventory " + file.getAbsolutePath());
        }

        if (inventoryType == InventoryType.CHEST){
            size = configuration.getInt("size", 54);
            if (size % 9 != 0) {
                throw new InventorySizeException("Size " + size + " is not valid for inventory " + file.getAbsolutePath());
            }
        }

        Map<Character, List<Integer>> matrix = generateMatrix(configuration.getStringList("matrix"));
        if (!matrix.isEmpty()) size = getInventorySizeByMatrix(configuration.getStringList("matrix"));

        List<Button> buttons = new ArrayList<>();
        Loader<Button> loader = new ZButtonLoader(this.plugin, file, size, matrix);

        Loader<MenuItemStack> menuItemStackLoader = new MenuItemStackLoader(this.plugin.getInventoryManager());
        MenuItemStack itemStack = null;
        try {
            // support both old and new configs
            String loadString = null;
            if (configuration.contains("fillItem")) {
                loadString = "fillItem";
            } else if (configuration.contains("fill-item")) {
                loadString = "fill-item";
            }
            if (loadString != null)
                itemStack = menuItemStackLoader.load(configuration, loadString + ".", file);
        } catch (Exception ignored) {
        }

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

        PatternManager patternManager = this.plugin.getPatternManager();
        List<Pattern> patterns = configuration.getStringList("patterns").stream().map(patternManager::getPattern).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());

        String fileName = this.getFileNameWithoutExtension(file);

        OpenWithItem openWithItem = null;
        try {
            // support both old and new configs
            String loadString = null;
            if (configuration.contains("openWithItem")) {
                loadString = "openWithItem";
            } else if (configuration.contains("open-with-item")) {
                loadString = "open-with-item";
            }
            if (loadString != null) {
                MenuItemStack loadedItem = menuItemStackLoader.load(configuration, loadString + ".item.", file);

                List<Action> actions = configuration.getStringList(loadString + ".actions").stream().map(string -> {
                    try {
                        return Action.valueOf(string.toUpperCase());
                    } catch (Exception exception) {
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toList());

                String type = configuration.getString(loadString + ".type", "full");
                ItemStackSimilar itemStackSimilar = this.plugin.getInventoryManager().getItemStackVerification(type).orElseGet(FullSimilar::new);

                openWithItem = new OpenWithItem(loadedItem, actions, itemStackSimilar);
            }
        } catch (Exception ignored) {
        }

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

        inventory.setType(inventoryType);
        inventory.setFillItemStack(itemStack);
        inventory.setUpdateInterval(configuration.getInt(path + "update-interval", configuration.getInt(path + "updateInterval", 1000)));
        inventory.setClearInventory(configuration.getBoolean(path + "clear-inventory", configuration.getBoolean(path + "clearInventory", false)));
        inventory.setFile(file);
        inventory.setPatterns(patterns);
        inventory.setOpenWithItem(openWithItem);

        Map<String, String> translatedDisplayName = new HashMap<>();
        String loadString = configuration.contains(path + "translatedName") ? "translatedName" : configuration.contains(path + "translated-name") ? "translated-name" : null;
        if (loadString != null) {
            configuration.getMapList(path + loadString).forEach(map -> {
                if (map.containsKey("locale") && map.containsKey("name")) {
                    String locale = (String) map.get("locale");
                    String inventoryName = (String) map.get("name");
                    translatedDisplayName.put(locale.toLowerCase(), inventoryName);
                }
            });
        }
        inventory.setTranslatedNames(translatedDisplayName);

        // Open requirement
        loadString = configuration.contains("open_requirement") ? "open_requirement" : configuration.contains("open-requirement") ?  "open-requirement" : null;
        if (loadString != null) {
            if (configuration.contains(loadString) && configuration.isConfigurationSection(loadString + ".")) {
                Loader<Requirement> requirementLoader = new RequirementLoader(this.plugin);
                inventory.setOpenRequirement(requirementLoader.load(configuration, loadString + ".", file));
            }
        }

        return inventory;
    }

    @Override
    public void save(Inventory object, YamlConfiguration configuration, String path, File file, Object... objects) {
        MenuItemStackLoader itemStackLoader = new MenuItemStackLoader(this.plugin.getInventoryManager());

        configuration.set("name", object.getName());
        configuration.set("size", object.size());

        if (object.getFillItemStack() != null) {
            itemStackLoader.save(object.getFillItemStack(), configuration, "fill-item.", file);
        }

        //TODO: FINISH THE SAVE METHOD
    }
}
