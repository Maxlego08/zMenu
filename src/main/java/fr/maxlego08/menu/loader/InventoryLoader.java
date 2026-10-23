package fr.maxlego08.menu.loader;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.*;
import fr.maxlego08.menu.api.registry.Registries;
import fr.maxlego08.menu.api.animation.TitleAnimation;
import fr.maxlego08.menu.api.animation.slot.AnimationPlayMode;
import fr.maxlego08.menu.api.animation.slot.AnimationSlots;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationSettings;
import fr.maxlego08.menu.api.animation.slot.settings.item.TemporaryItem;
import fr.maxlego08.menu.api.animation.TitleAnimationLoader;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.exceptions.InventorySizeException;
import fr.maxlego08.menu.api.exceptions.InventoryTypeException;
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
import java.util.*;

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
        String nameType = configuration.getString("type", "CHEST").toUpperCase(Locale.ROOT);
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
            if (size < 9 || size > 54) {
                throw new InventorySizeException("Size " + size + " is not valid for inventory " + file.getAbsolutePath() + " because a chest inventory must be between 9 and 54.");
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
                    if (Configuration.enableDebug) {
                        Logger.info("Error while loading button " + buttonPath + " in " + file.getAbsolutePath() + ": " + exception.getMessage(), Logger.LogType.ERROR);
                    }
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
                    Logger.error(e);
                    inventory = InventoryTypeRegistry.getInstance().get(inventoryType).orElseGet(EmptyContainerInventoryTypeLoader::new).load(this.plugin, pluginOwner, name, fileName, size, buttons, configuration, path, file);
                }
            } else {
                inventory = InventoryTypeRegistry.getInstance().get(inventoryType).orElseGet(EmptyContainerInventoryTypeLoader::new).load(this.plugin, pluginOwner, name, fileName, size, buttons, configuration, path, file);
            }
        }

        this.loadTitleAnimation(inventory, configuration, file);
        SlotAnimationSettings slotAnimation = new SlotAnimationSettingsLoader(this.plugin).load(configuration, path + "open-animation.", file);
        inventory.setSlotAnimation(slotAnimation);
        this.validateAnimation(slotAnimation, buttons, size, file);

        SlotAnimationSettings closeAnimation = new SlotAnimationSettingsLoader(this.plugin).load(configuration, path + "close-animation.", file);
        inventory.setCloseAnimation(closeAnimation);
        this.validateAnimation(closeAnimation, buttons, size, file);
        this.warnAboutCloseOnlyKeys(closeAnimation, file);

        inventory.setType(inventoryType);
        inventory.setUpdateInterval(configuration.getInt(path + "update-interval", configuration.getInt(path + "updateInterval", 1000)));
        inventory.setClearInventory(configuration.getBoolean(path + "clear-inventory", configuration.getBoolean(path + "clearInventory", false)));
        inventory.setCancelItemPickup(configuration.getBoolean(path + "cancel-item-pickup", configuration.getBoolean(path + "cancelItemPickup", false)));
        inventory.setTargetPlayerNamePlaceholder(configuration.getString(path + "target-player-name-placeholder", configuration.getString(path + "target_player_name_placeholder", "%player_name%")));
        String clearInvTypeStr = configuration.getString("clear-inventory-type", "DEFAULT");
        try {
            ClearInvType clearInvType = ClearInvType.valueOf(clearInvTypeStr.toUpperCase(Locale.ROOT));
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
            ConfigurationSection replacementSection = configuration.getConfigurationSection("inventory-replacement");
            if (replacementSection != null) {
                InventoryReplacement inventoryReplacement = InventoryReplacement.deserialize(replacementSection);
                if (inventoryReplacement != null) {
                    inventory.setInventoryReplacement(inventoryReplacement);
                }
            }
        }

        this.loadFillItem(configuration, inventory, menuItemStackLoader, file);
        this.loadPatterns(configuration, inventory);
        this.loadOpenWithItem(configuration, inventory, file, menuItemStackLoader);
        this.loadOpenRequirement(configuration, inventory, file);
        this.loadOpenAndCloseActions(configuration, inventory, file);

        inventory.setTranslatedNames(MenuItemStackLoader.getTranslatedName(configuration, path));

        List<fr.maxlego08.menu.api.InventoryOption> inventoryOptions = new ArrayList<>();
        for (InventoryOptionType type : this.plugin.getInventoryManager().getRegistries().registry(Registries.INVENTORY_OPTION)) {
            InventoryOption instance = type.create();
            if (instance != null) {
                inventoryOptions.add(instance);
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
                inventory.setFillItemUseCache(configuration.getBoolean(loadString + ".use-cache",
                        configuration.getBoolean(loadString + ".useCache", true)));
            }
        } catch (Exception exception) {
            Logger.info("Unable to load the fill item of " + file.getAbsolutePath() + ": " + exception.getMessage(), Logger.LogType.ERROR);
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
                        actions.add(Action.valueOf(string.toUpperCase(Locale.ROOT)));
                    } catch (IllegalArgumentException exception) {
                        Logger.info("Action " + string + " is not valid at " + loadString + ".actions in " + file.getAbsolutePath()
                                + ", expected one of " + Arrays.toString(Action.values()), Logger.LogType.ERROR);
                    }
                }

                String type = configuration.getString(loadString + ".type", "full");
                ItemStackSimilar itemStackSimilar = this.plugin.getItemStackService().getVerification(type).orElseGet(FullSimilar::new);

                inventory.setOpenWithItem(new OpenWithItem(loadedItem, actions, itemStackSimilar));
            }
        } catch (Exception exception) {
            Logger.info("Unable to load the open with item of " + file.getAbsolutePath() + ": " + exception.getMessage(), Logger.LogType.ERROR);
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
            } else {
                Logger.info("The title animation of " + file.getName() + " was ignored: no title animation provider " + (pluginName == null ? "is registered" : "named '" + pluginName + "' is registered (is PacketEvents installed?)") + ".", Logger.LogType.WARNING);
            }
        }
    }

    /**
     * Warns about a group that was configured but that no button belongs to, which is usually a
     * typo: the group simply never plays.
     */
    private void warnAboutUnusedGroups(SlotAnimationSettings settings, List<Button> buttons, File file) {
        if (settings.groups().isEmpty()) return;

        Set<Integer> used = new HashSet<>();
        for (Button button : buttons) {
            if (button == null) continue;
            if (button.getAnimationSettings().enabled()) used.add(button.getAnimationSettings().group());
        }

        settings.groups().keySet().stream()
                .filter(group -> !used.contains(group))
                .sorted()
                .forEach(group -> Logger.info("The animation group " + group + " is configured in " + file.getAbsolutePath()
                        + " but no button belongs to it, so it never plays. Buttons join a group with `animation.group: " + group + "`.",
                        Logger.LogType.WARNING));
    }

    /**
     * Warns about the keys a closing animation loads but never acts on.
     *
     * <p>A close plays through {@code closeWithAnimation}, which asks the animation whether it wants
     * to play at all — so {@code chance} and {@code requirement} work there. It does not go through
     * the remembering that an opening animation does, so {@code play} and {@code cooldown} are read
     * and then ignored. Saying so is better than letting someone believe their menu leaves in a
     * special way exactly once.</p>
     */
    private void warnAboutCloseOnlyKeys(SlotAnimationSettings settings, File file) {
        if (settings == null) return;

        if (settings.play() != AnimationPlayMode.ALWAYS) {
            Logger.info("The close animation of " + file.getAbsolutePath() + " sets `play: " + settings.play()
                    + "`, which only applies to an opening animation and is ignored here. Use `chance` or `requirement`"
                    + " to decide whether a close animation plays.", Logger.LogType.WARNING);
        }

        if (settings.cooldown() > 0) {
            Logger.info("The close animation of " + file.getAbsolutePath() + " sets a `cooldown`, which only applies"
                    + " to an opening animation and is ignored here.", Logger.LogType.WARNING);
        }
    }

    /**
     * Warns about temporary items placed on a slot the menu does not have.
     *
     * <p>The compiler silently skips those, because it also serves animations shared between menus
     * of different sizes. A menu declaring its own animation knows its size, so a typo can be
     * reported here instead of quietly doing nothing.</p>
     */
    private void warnAboutSlotsOutOfRange(SlotAnimationSettings settings, int size, File file) {
        int total = AnimationSlots.totalSize(size);

        List<SlotAnimationSettings> toCheck = new ArrayList<>();
        toCheck.add(settings);
        settings.variants().forEach(variant -> toCheck.add(variant.settings()));

        for (SlotAnimationSettings current : toCheck) {
            for (TemporaryItem temporaryItem : current.temporaryItems()) {
                List<Integer> outside = temporaryItem.slots().stream().filter(slot -> slot < 0 || slot >= total).toList();
                if (outside.isEmpty()) continue;

                Logger.info("The animation item " + temporaryItem.name() + " uses the slots " + outside + " in "
                        + file.getAbsolutePath() + ", which this menu does not have. Valid slots are 0 to " + (size - 1)
                        + " for the menu and " + size + " to " + (total - 1) + " for the player inventory. They will be ignored.",
                        Logger.LogType.WARNING);
            }
        }
    }

    private void validateAnimation(SlotAnimationSettings settings, List<Button> buttons, int size, File file) {
        if (settings == null) return;

        this.warnAboutUnusedGroups(settings, buttons, file);
        this.warnAboutSlotsOutOfRange(settings, size, file);
        if (settings.temporaryItems().isEmpty()) return;

        Map<Integer, Button> refusing = new HashMap<>();
        for (Button button : buttons) {
            if (button == null || button.isPlayerInventory()) continue;
            if (button.getAnimationSettings().allowTemporaryItems()) continue;

            for (int slot : button.getSlots()) {
                refusing.putIfAbsent(slot, button);
            }
        }
        if (refusing.isEmpty()) return;

        settings.temporaryItems().forEach(temporaryItem -> temporaryItem.slots().stream()
                .filter(refusing::containsKey)
                .findFirst()
                .ifPresent(slot -> Logger.info("The animation item " + temporaryItem.name() + " covers the slot " + slot
                        + " of the button " + refusing.get(slot).getName() + ", which does not allow it, in " + file.getAbsolutePath()
                        + ". Add `animation.allow-temporary-items: true` to that button to draw over it.", Logger.LogType.WARNING)));
    }
}
