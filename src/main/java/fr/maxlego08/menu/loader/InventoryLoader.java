package fr.maxlego08.menu.loader;

import fr.maxlego08.menu.ZInventory;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryOption;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.exceptions.InventorySizeException;
import fr.maxlego08.menu.api.exceptions.InventoryTypeException;
import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import fr.maxlego08.menu.api.pattern.Pattern;
import fr.maxlego08.menu.api.pattern.PatternManager;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.api.utils.OpenWithItem;
import fr.maxlego08.menu.itemstack.FullSimilar;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.*;

public class InventoryLoader extends ZUtils implements Loader<Inventory> {

    private final ZMenuPlugin plugin;

    public InventoryLoader(ZMenuPlugin plugin) {
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
        String nameType = configuration.getString("type", "CHEST").toUpperCase();
        try {
            inventoryType = InventoryType.valueOf(nameType);
            if (inventoryType == InventoryType.CRAFTING || inventoryType == InventoryType.PLAYER) {
                throw new InventoryTypeException("Type Inventory " + nameType + " can't use for the moment for inventory " + file.getAbsolutePath());
            }
            size = inventoryType.getDefaultSize();
        } catch (IllegalArgumentException exception) {
            throw new InventoryTypeException("Type Inventory " + nameType + " is not valid for inventory " + file.getAbsolutePath());
        }

        if (inventoryType == InventoryType.CHEST) {
            size = configuration.getInt("size", 54);
            if (size % 9 != 0) {
                throw new InventorySizeException("Size " + size + " is not valid for inventory " + file.getAbsolutePath());
            }
        }

        Map<Character, List<Integer>> matrix = this.generateMatrix(configuration.getStringList("matrix"));
        if (!matrix.isEmpty()) size = this.getInventorySizeByMatrix(configuration.getStringList("matrix"));

        List<Button> buttons = new ArrayList<>();
        Loader<Button> loader = new ZButtonLoader(this.plugin, file, size, matrix);

        Loader<MenuItemStack> menuItemStackLoader = new MenuItemStackLoader(this.plugin.getInventoryManager());

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
            if (Config.enableDebug){
            Logger.info("items section was not found in " + file.getAbsolutePath(), Logger.LogType.ERROR);
            }
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

        inventory.setType(inventoryType);
        inventory.setUpdateInterval(configuration.getInt(path + "update-interval", configuration.getInt(path + "updateInterval", 1000)));
        inventory.setClearInventory(configuration.getBoolean(path + "clear-inventory", configuration.getBoolean(path + "clearInventory", false)));
        inventory.setCancelItemPickup(configuration.getBoolean(path + "cancel-item-pickup", configuration.getBoolean(path + "cancelItemPickup", false)));
        inventory.setTargetPlayerNamePlaceholder(configuration.getString(path + "target-player-name-placeholder", configuration.getString(path + "target_player_name_placeholder", "%player_name%")));
        inventory.setFile(file);

        this.loadFillItem(configuration, inventory, menuItemStackLoader, file);
        this.loadPatterns(configuration, inventory);
        this.loadOpenWithItem(configuration, inventory, file, menuItemStackLoader);
        this.loadOpenRequirement(configuration, inventory, file);

        /*Map<String, String> translatedDisplayName = new HashMap<>();
        MenuItemStackLoader.getTranslatedName(configuration, path, translatedDisplayName);
        String loadString;
        inventory.setTranslatedNames(translatedDisplayName);*/

        List<InventoryOption> inventoryOptions = new ArrayList<>();
        for (Map.Entry<String, List<InventoryOption>> entry : this.plugin.getInventoryManager().getInventoryOptions().entrySet()) {
            for (InventoryOption option : entry.getValue()) {
                InventoryOption instance = createInstance(entry.getKey(), option);
                if (instance != null) {
                    inventoryOptions.add(instance);
                }
            }
        }
        for (InventoryOption inventoryOption : inventoryOptions) {
            inventoryOption.loadInventory(inventory, file, configuration, this.plugin.getInventoryManager(), this.plugin.getButtonManager());
        }

        return inventory;
    }

    /**
     * Loads the fill item of the given configuration and assigns the relevant data to
     * the given inventory.
     *
     * @param configuration       the configuration to load the fill item from
     * @param inventory           the inventory to assign the fill item to
     * @param menuItemStackLoader the loader to use to load the fill item
     */
    private void loadFillItem(YamlConfiguration configuration, ZInventory inventory, Loader<MenuItemStack> menuItemStackLoader, File file) {
        try {
            String loadString = configuration.contains("fillItem") ? "fillItem" : configuration.contains("fill-item") ? "fill-item" : null;
            if (loadString != null) {
                inventory.setFillItemStack(menuItemStackLoader.load(configuration, loadString + ".", file));
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Loads the patterns section of the given configuration and assigns the relevant data to
     * the given inventory.
     *
     * @param configuration the configuration to load the patterns from
     * @param inventory     the inventory to assign the patterns to
     */
    private void loadPatterns(YamlConfiguration configuration, ZInventory inventory) {
        PatternManager patternManager = this.plugin.getPatternManager();
        List<String> patternNames = configuration.getStringList("patterns");
        List<Pattern> patterns = new ArrayList<>(patternNames.size());
        for (String patternName : patternNames) {
            Optional<Pattern> pattern = patternManager.getPattern(patternName);
            pattern.ifPresent(patterns::add);
        }
        inventory.setPatterns(patterns);
    }

    /**
     * Loads the openWithItem section of the given configuration and assigns the relevant data to the given inventory.
     * <p>
     * The openWithItem section can have the following sub-sections:
     * <ul>
     *     <li>item: the item that must be in the player's hand when they open the inventory</li>
     *     <li>actions: a list of actions that must be performed when the player opens the inventory with the given item in hand</li>
     *     <li>type: the type of verification to use when checking if the item in the player's hand matches the item specified in the configuration.
     *          The default type is "full", which uses the {@link FullSimilar} class to verify the item stack.
     *          Other types can be specified by adding a class that implements {@link ItemStackSimilar} to the {@link fr.maxlego08.menu.api.InventoryManager}.</li>
     * </ul>
     * <p>
     * If any exception is thrown while loading the openWithItem section, it is caught and ignored.
     *
     * @param configuration       the configuration to load the openWithItem section from
     * @param inventory           the inventory to assign the loaded data to
     * @param file                the file that the configuration was loaded from
     * @param menuItemStackLoader the loader to use to load the item stack from the configuration
     */
    private void loadOpenWithItem(YamlConfiguration configuration, ZInventory inventory, File file, Loader<MenuItemStack> menuItemStackLoader) {
        try {
            String loadString = configuration.contains("openWithItem") ? "openWithItem" : configuration.contains("open-with-item") ? "open-with-item" : null;
            if (loadString != null) {
                MenuItemStack loadedItem = menuItemStackLoader.load(configuration, loadString + ".item.", file);

                List<String> actionStrings = configuration.getStringList(loadString + ".actions");
                List<Action> actions = new ArrayList<>(actionStrings.size());
                for (String string : actionStrings) {
                    try {
                        actions.add(Action.valueOf(string.toUpperCase()));
                    } catch (Exception ignored) {
                    }
                }

                String type = configuration.getString(loadString + ".type", "full");
                ItemStackSimilar itemStackSimilar = this.plugin.getInventoryManager().getItemStackVerification(type).orElseGet(FullSimilar::new);

                inventory.setOpenWithItem(new OpenWithItem(loadedItem, actions, itemStackSimilar));
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Load the open requirement for the inventory.
     *
     * @param configuration the yaml configuration of the inventory
     * @param inventory     the inventory to load the requirement for
     * @param file          the file of the inventory
     * @throws InventoryException if the requirement cannot be loaded
     */
    private void loadOpenRequirement(YamlConfiguration configuration, ZInventory inventory, File file) throws InventoryException {
        String loadString = configuration.contains("open_requirement") ? "open_requirement" : configuration.contains("open-requirement") ? "open-requirement" : null;
        if (loadString != null) {
            if (configuration.contains(loadString) && configuration.isConfigurationSection(loadString + ".")) {
                Loader<Requirement> requirementLoader = new RequirementLoader(this.plugin);
                inventory.setOpenRequirement(requirementLoader.load(configuration, loadString + ".", file));
            }
        }
    }

    private InventoryOption createInstance(Plugin plugin, Class<? extends InventoryOption> aClass) {
        try {
            Constructor<? extends InventoryOption> constructor = aClass.getConstructor(Plugin.class);
            return constructor.newInstance(plugin);
        } catch (NoSuchMethodException ignored) {
            try {
                return aClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                return null;
            }
        } catch (Exception ignored) {
            return null;
        }
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
