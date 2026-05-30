package fr.maxlego08.menu.loader;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.*;
import fr.maxlego08.menu.api.animation.TitleAnimation;
import fr.maxlego08.menu.api.animation.TitleAnimationLoader;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.exceptions.InventorySizeException;
import fr.maxlego08.menu.api.exceptions.InventoryTypeException;
import fr.maxlego08.menu.api.inventory.ContainerInventory;
import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import fr.maxlego08.menu.api.pattern.ActionPattern;
import fr.maxlego08.menu.api.pattern.Pattern;
import fr.maxlego08.menu.api.pattern.PatternManager;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.ClearInvType;
import fr.maxlego08.menu.api.utils.InventoryReplacement;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.api.utils.OpenWithItem;
import fr.maxlego08.menu.common.utils.ZUtils;
import fr.maxlego08.menu.inventory.setter.ContainerInventorySetter;
import fr.maxlego08.menu.inventory.zinv.ZInventory;
import fr.maxlego08.menu.itemstack.FullSimilar;
import fr.maxlego08.menu.loader.container.EmptyContainerInventoryTypeLoader;
import fr.maxlego08.menu.registry.InventoryTypeRegistry;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InventoryLoader extends ZUtils implements Loader<Inventory> {

    private final ZMenuPlugin plugin;

    public InventoryLoader(ZMenuPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public Inventory load(@NonNull YamlConfiguration configuration, @NonNull String path, Object... objects) throws InventoryException {

        File file = (File) objects[0];
        var nameObject = configuration.get("name", configuration.get("title"));
        String name = "";

        if (nameObject instanceof List<?> list) {
            name = String.join("", (List<String>) list);
        } else if (nameObject instanceof String string) {
            name = string;
        }

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
                int closestMultiple = (size / 9) * 9;
                int nextMultiple = closestMultiple + 9;
                int closest = (size - closestMultiple < nextMultiple - size) ? closestMultiple : nextMultiple;
                throw new InventorySizeException("Size " + size + " is not valid for inventory " + file.getAbsolutePath() + " because it's not a multiple of 9. The closest valid size would be " + closest);
            }
        }

        Map<Character, List<Integer>> matrix = this.generateMatrix(configuration.getStringList("matrix"));
        if (!matrix.isEmpty()) size = this.getInventorySizeByMatrix(configuration.getStringList("matrix"));

        List<Button> buttons = new ArrayList<>();
        Loader<Button> loader = new ZButtonLoader(this.plugin, file, size, matrix);

        List<ActionPattern> actionPatterns = this.loadActionPatterns(configuration);

        Loader<MenuItemStack> menuItemStackLoader = new MenuItemStackLoader(this.plugin.getInventoryManager());

        ConfigurationSection section = configuration.getConfigurationSection("items.");

        if (section != null) {
            for (String buttonPath : section.getKeys(false)) {
                try {
                    buttons.add(loader.load(configuration, "items." + buttonPath + ".", buttonPath, actionPatterns));
                } catch (Exception exception) {
                    Logger.info(exception.getMessage(), Logger.LogType.ERROR);
                }
            }
        } else {
            if (Configuration.enableDebug) {
                Logger.info("items section was not found in " + file.getAbsolutePath(), Logger.LogType.ERROR);
            }
        }

        Plugin pluginOwner;
        if (objects.length >= 3 && objects[2] instanceof Plugin) {
            pluginOwner = (Plugin) objects[2];
        } else {
            pluginOwner = this.plugin;
        }

        String fileName = this.getFileNameWithoutExtension(file);

        ContainerInventorySetter inventory;
        if (!((objects[1]) instanceof Class<?> rawClass)) {
            inventory = InventoryTypeRegistry.getInstance().get(inventoryType).orElseGet(EmptyContainerInventoryTypeLoader::new).load(this.plugin, pluginOwner, name, fileName, size, buttons, configuration, path, file);
        } else {
            if (rawClass == ZInventory.class) {
                inventory = InventoryTypeRegistry.getInstance().get(inventoryType).orElseGet(EmptyContainerInventoryTypeLoader::new).load(this.plugin, pluginOwner, name, fileName, size, buttons, configuration, path, file);
            } else if (ZInventory.class.isAssignableFrom(rawClass)) {
                try {
                    Class<? extends ZInventory> classz = (Class<? extends ZInventory>) rawClass;
                    Constructor<? extends ZInventory> constructor = classz.getDeclaredConstructor(Plugin.class, String.class, String.class, int.class, List.class);
                    constructor.setAccessible(true);
                    inventory = constructor.newInstance(pluginOwner, name, fileName, size, buttons);
                } catch (Exception e) {
                    e.printStackTrace();
                    inventory = InventoryTypeRegistry.getInstance().get(inventoryType).orElseGet(EmptyContainerInventoryTypeLoader::new).load(this.plugin, pluginOwner, name, fileName, size, buttons, configuration, path, file);
                }
            } else {
                inventory = InventoryTypeRegistry.getInstance().get(inventoryType).orElseGet(EmptyContainerInventoryTypeLoader::new).load(this.plugin, pluginOwner, name, fileName, size, buttons, configuration, path, file);
            }
        }

        this.loadTitleAnimation(inventory, configuration, file);

        inventory.setType(inventoryType);
        inventory.setUpdateInterval(configuration.getInt(path + "update-interval", configuration.getInt(path + "updateInterval", 1000)));
        inventory.setClearInventory(configuration.getBoolean(path + "clear-inventory", configuration.getBoolean(path + "clearInventory", false)));
        inventory.setCancelItemPickup(configuration.getBoolean(path + "cancel-item-pickup", configuration.getBoolean(path + "cancelItemPickup", false)));
        inventory.setTargetPlayerNamePlaceholder(configuration.getString(path + "target-player-name-placeholder", configuration.getString(path + "target_player_name_placeholder", "%player_name%")));
        String clearInvTypeStr = configuration.getString("clear-inventory-type", "DEFAULT");
        try {
            ClearInvType clearInvType = ClearInvType.valueOf(clearInvTypeStr.toUpperCase());
            if (clearInvType.hasRequiredPlugin() && !this.plugin.getServer().getPluginManager().isPluginEnabled(clearInvType.getRequiredPlugin())) {
                clearInvType = ClearInvType.DEFAULT;
            }
            inventory.setClearInvType(clearInvType);
        } catch (Exception e) {
            if (Configuration.enableDebug){
                Logger.info("Clear inventory type " + clearInvTypeStr + " is not valid in " + file.getAbsolutePath(), Logger.LogType.ERROR);
            }
        }
        inventory.setClickLimiterEnabled(configuration.getBoolean(path + "click-limiter-enabled", true));
        inventory.setFile(file);
        if (configuration.isConfigurationSection("inventory-replacement")){
            String replacementName = configuration.getString("inventory-replacement.name", "");
            String replacementPlugin = configuration.getString("inventory-replacement.plugin", "zMenu");
            List<Integer> replacementPages = configuration.getIntegerList("inventory-replacement.pages");
            InventoryReplacement inventoryReplacement = new InventoryReplacement(replacementName, replacementPlugin, replacementPages);
            inventory.setInventoryReplacement(inventoryReplacement);
        }

        this.loadFillItem(configuration, inventory, menuItemStackLoader, file);
        this.loadPatterns(configuration, inventory);
        this.loadOpenWithItem(configuration, inventory, file, menuItemStackLoader);
        this.loadOpenRequirement(configuration, inventory, file);
        this.loadOpenAndCloseActions(configuration, inventory, file);

        /*Map<String, String> translatedDisplayName = new HashMap<>();
        MenuItemStackLoader.getTranslatedName(configuration, path, translatedDisplayName);
        String loadString;
        inventory.setTranslatedNames(translatedDisplayName);*/

        List<InventoryOption> inventoryOptions = new ArrayList<>();
        for (Map.Entry<Plugin, List<Class<? extends InventoryOption>>> entry : this.plugin.getInventoryManager().getInventoryOptions().entrySet()) {
            for (Class<? extends InventoryOption> optionClass : entry.getValue()) {
                InventoryOption instance = this.createInstance(entry.getKey(), optionClass);
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

    private void loadOpenAndCloseActions(@NonNull YamlConfiguration configuration, ContainerInventorySetter inventory, File file) {
        var buttonManager = this.plugin.getButtonManager();
        inventory.setOpenActions(buttonManager.loadActions(configuration, "open-actions", file));
        inventory.setCloseActions(buttonManager.loadActions(configuration, "close-actions", file));
    }

    /**
     * Loads the fill item of the given configuration and assigns the relevant data to
     * the given inventory.
     *
     * @param configuration       the configuration to load the fill item from
     * @param inventory           the inventory to assign the fill item to
     * @param menuItemStackLoader the loader to use to load the fill item
     */
    private void loadFillItem(YamlConfiguration configuration, ContainerInventorySetter inventory, Loader<MenuItemStack> menuItemStackLoader, File file) {
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
    private void loadPatterns(YamlConfiguration configuration, ContainerInventorySetter inventory) {
        PatternManager patternManager = this.plugin.getPatternManager();
        List<String> patternNames = configuration.getStringList("patterns");
        List<Pattern> patterns = new ArrayList<>(patternNames.size());
        for (String patternName : patternNames) {
            Optional<Pattern> pattern = patternManager.getPattern(patternName);
            pattern.ifPresent(patterns::add);
        }
        inventory.setPatterns(patterns);
    }

    private List<ActionPattern> loadActionPatterns(YamlConfiguration configuration) {
        PatternManager patternManager = this.plugin.getPatternManager();
        List<ActionPattern> actionPatterns = new ArrayList<>();
        for (String patternName : configuration.getStringList("action-patterns")) {
            Optional<ActionPattern> actionPattern = patternManager.getActionPattern(patternName);
            actionPattern.ifPresent(actionPatterns::add);
        }
        return actionPatterns;
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
     *          Other types can be specified by adding a class that implements {@link ItemStackSimilar} to the {@link InventoryManager}.</li>
     * </ul>
     * <p>
     * If any exception is thrown while loading the openWithItem section, it is caught and ignored.
     *
     * @param configuration       the configuration to load the openWithItem section from
     * @param inventory           the inventory to assign the loaded data to
     * @param file                the file that the configuration was loaded from
     * @param menuItemStackLoader the loader to use to load the item stack from the configuration
     */
    private void loadOpenWithItem(YamlConfiguration configuration, ContainerInventorySetter inventory, File file, Loader<MenuItemStack> menuItemStackLoader) {
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
    private void loadOpenRequirement(YamlConfiguration configuration, ContainerInventorySetter inventory, File file) throws InventoryException {
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

    private void loadTitleAnimation(ContainerInventorySetter inventory, YamlConfiguration configuration, File file) throws InventoryException {
        if (configuration.isConfigurationSection("title-animation")) {
            String titleAnimationPath = "title-animation.";
            String pluginName = configuration.getString(titleAnimationPath + "plugin");
            Optional<TitleAnimationLoader> titleAnimationLoader;
            TitleAnimationManager titleAnimationManager = this.plugin.getTitleAnimationManager();
            if (pluginName == null) {
                titleAnimationLoader = titleAnimationManager.getFirstLoader();
            } else {
                titleAnimationLoader = titleAnimationManager.getLoader(pluginName);
            }
            if (titleAnimationLoader.isPresent()) {
                TitleAnimationLoader animationLoader = titleAnimationLoader.get();
                TitleAnimation titleAnimation = animationLoader.load(configuration, titleAnimationPath, file);
                inventory.setTitleAnimation(titleAnimation);
            }
        }
    }

    @Override
    public void save(Inventory inventory, @NonNull YamlConfiguration configuration, @NonNull String path, File file, Object... objects) {
        MenuItemStackLoader itemStackLoader = new MenuItemStackLoader(this.plugin.getInventoryManager());

        configuration.set("name", inventory.getName());
        configuration.set("size", inventory.size());

        if (inventory instanceof ContainerInventory containerInventory && containerInventory.getFillItemStack() != null) {
            itemStackLoader.save(containerInventory.getFillItemStack(), configuration, "fill-item.", file);
        }

        // TODO: FINISH THE SAVE METHOD
    }
}
