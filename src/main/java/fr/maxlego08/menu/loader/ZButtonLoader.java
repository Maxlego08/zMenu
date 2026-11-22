package fr.maxlego08.menu.loader;

import com.cryptomorin.xseries.XSound;
import fr.maxlego08.menu.ZMenuItemStack;
import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.button.ButtonOptionType;
import fr.maxlego08.menu.api.registry.Registries;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.ButtonOption;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.enums.PlaceholderAction;
import fr.maxlego08.menu.api.event.events.ButtonLoadEvent;
import fr.maxlego08.menu.api.exceptions.InventoryButtonException;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.loader.PermissibleLoader;
import fr.maxlego08.menu.api.pattern.ActionPattern;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.RefreshRequirement;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerData;
import fr.maxlego08.menu.api.requirement.permissible.PermissionPermissible;
import fr.maxlego08.menu.api.requirement.permissible.PlaceholderPermissible;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.api.utils.OpenLink;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.common.utils.ZUtils;
import fr.maxlego08.menu.common.utils.cache.YamlFileCache;
import fr.maxlego08.menu.common.utils.cache.YamlFileCacheEntry;
import fr.maxlego08.menu.common.utils.yaml.YamlParser;
import fr.maxlego08.menu.loader.permissible.PlaceholderPermissibleLoader;
import fr.maxlego08.menu.requirement.permissible.ZPermissionPermissible;
import fr.maxlego08.menu.requirement.permissible.ZPlaceholderPermissible;
import fr.maxlego08.menu.sound.ZSoundOption;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.Bukkit;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;

public class ZButtonLoader extends ZUtils implements Loader<Button> {

    private static final Set<Class<? extends ButtonOption>> WARNED_OPTIONS = ConcurrentHashMap.newKeySet();

    private final MenuPlugin plugin;
    private final File file;
    private final int inventorySize;
    private final Map<Character, List<Integer>> matrix;

    public ZButtonLoader(MenuPlugin plugin, File file, int inventorySize, Map<Character, List<Integer>> matrix) {
        super();
        this.plugin = plugin;
        this.file = file;
        this.inventorySize = inventorySize;
        this.matrix = matrix;
    }

    @Override
    public Button load(@NonNull YamlDocument configuration, @NonNull String path, Object... objects) throws InventoryException {

        String buttonType = configuration.getString(path + "type", "NONE");
        String buttonName = (String) objects[0];
        DefaultButtonValue defaultButtonValue = null;
        List<ActionPattern> actionPatterns = new ArrayList<>();
        for (Object o : objects) {
            if (o instanceof DefaultButtonValue) {
                defaultButtonValue = (DefaultButtonValue) o;
            } else if (o instanceof List<?> lst && !lst.isEmpty() && lst.getFirst() instanceof ActionPattern) {
                actionPatterns = (List<ActionPattern>) o;
            }
        }
        if (defaultButtonValue == null) {
            defaultButtonValue = new DefaultButtonValue(this.inventorySize, this.matrix, this.file);
        }

        Section patternSection = configuration.getSection(path + "pattern");

        // Using the pattern file to load the button
        if (patternSection != null) {

            Map<String, Object> mapPlaceholders = new HashMap<>();
            patternSection.getRoutesAsStrings(false).forEach(key -> mapPlaceholders.put(key, patternSection.get(key)));

            String fileName = configuration.getString(path + "pattern.fileName", configuration.getString(path + "pattern.file-name", configuration.getString(path + "pattern.file")));
            String pluginName = configuration.getString(path + "pattern.pluginName", configuration.getString(path + "pattern.plugin-name", configuration.getString(path + "pattern.plugin", null)));
            Plugin patternPlugin = pluginName != null ? Bukkit.getPluginManager().getPlugin(pluginName) : this.plugin;
            if (patternPlugin == null) throw new InventoryButtonException("Impossible to load the pattern " + fileName);

            Path patternPath = patternPlugin.getDataFolder().toPath().resolve("patterns").resolve(fileName + ".yml");
            Optional<YamlFileCacheEntry> yamlFileEntry = YamlFileCache.getYamlFileEntry(patternPath);
            if (yamlFileEntry.isEmpty()) {
                throw new InventoryButtonException("Impossible to load the pattern " + fileName + ", file doesnt exist");
            }

            YamlDocument patternYamlConfiguration = yamlFileEntry.get().getYamlConfiguration();

            this.loadDefaultPatternValues(patternYamlConfiguration, mapPlaceholders);

            mapPlaceholders.putAll(this.plugin.getGlobalPlaceholders());

            this.loadLocalPlaceholders(patternYamlConfiguration, mapPlaceholders);

            patternYamlConfiguration = YamlParser.parseConfiguration(patternYamlConfiguration, mapPlaceholders);
            Button patternButton = this.load(patternYamlConfiguration, "button.", buttonName, actionPatterns);
            // Load view requirements for the pattern button (from main config)
            this.loadViewRequirements(patternButton, configuration, path, this.file);

            // Load else button from main config if present
            if (configuration.contains(path + "else")) {
                DefaultButtonValue elseDefaultButtonValue = new DefaultButtonValue(this.inventorySize, this.matrix, this.file);
                Button elseButton = this.load(configuration, path + "else.", buttonName + ".else", elseDefaultButtonValue, actionPatterns);
                patternButton.setElseButton(elseButton);
                if (elseButton != null) {
                    elseButton.setParentButton(patternButton);
                }
            }

            return patternButton;
        }

        ButtonManager buttonManager = this.plugin.getButtonManager();
        Optional<ButtonLoader> optional = buttonManager.getLoader(buttonType);

        if (optional.isEmpty()) {
            throw new InventoryButtonException("Impossible to find the type " + buttonType + " for the button " + path + " in inventory " + this.file.getAbsolutePath());
        }

        Loader<MenuItemStack> itemStackLoader = new MenuItemStackLoader(this.plugin.getInventoryManager());

        ButtonLoader loader = optional.get();
        Button button = loader.load(configuration, path, defaultButtonValue);

        if (button == null) {
            throw new InventoryButtonException("Impossible to find the type " + buttonType + " for the button " + path + " in inventory " + this.file.getAbsolutePath());
        }

        button.setPlugin(this.plugin);

        int slot;
        int page;

        try {

            String slotString = configuration.getString(path + "slot", String.valueOf(defaultButtonValue.getSlot()));
            if (slotString.contains("-")) {

                String[] strings = slotString.split("-");
                page = Integer.parseInt(strings[0]);
                slot = Integer.parseInt(strings[1]);
            } else {

                slot = this.parseInt(configuration.getString(path + "slot", null), defaultButtonValue.getSlot());
                page = this.parseInt(configuration.getString(path + "page", null), defaultButtonValue.getPage());
            }
        } catch (Exception ignored) {
            slot = this.parseInt(configuration.getString(path + "slot", null), defaultButtonValue.getSlot());
            page = this.parseInt(configuration.getString(path + "page", null), defaultButtonValue.getPage());
        }

        page = Math.max(page, 1);
        if (slot != defaultButtonValue.getSlot() || slot == 0) {
            slot = slot + ((page - 1) * this.inventorySize);
        }

        List<String> slotsAsString = YamlSections.getStringList(configuration, path + "slots");
        List<Integer> slots = ButtonLoader.loadSlot(slotsAsString);
        if (slots.isEmpty()) {
            slots = defaultButtonValue.getSlots();
        } else {
            int finalPage = page;
            List<Integer> adjustedSlots = new ArrayList<>(slots.size());
            for (Integer specialSlot : slots) {
                adjustedSlots.add(specialSlot + ((finalPage - 1) * this.inventorySize));
            }
            slots = adjustedSlots;
        }

        char currentChar = buttonName.charAt(0);
        if (this.matrix.containsKey(currentChar)) {
            slots = this.matrix.get(currentChar);
            if (slots.size() == 1) {
                slot = slots.getFirst();
                slots = new ArrayList<>();
            }
        }

        if (slots.isEmpty()) {
            button.setSlot(slot);
        } else {
            button.setSlots(slots);
        }
        button.setPage(page);

        button.setPermanent(configuration.getBoolean(path + "isPermanent", configuration.getBoolean(path + "is-permanent", defaultButtonValue.isPermanent())));
        button.setUpdateOnClick(configuration.getBoolean(path + "updateOnClick", configuration.getBoolean(path + "update-on-click", defaultButtonValue.isUpdateOnClick())));
        button.setCloseInventory(configuration.getBoolean(path + "closeInventory", configuration.getBoolean(path + "close-inventory", defaultButtonValue.isCloseInventory())));

        ZMenuItemStack itemStack = (ZMenuItemStack) itemStackLoader.load(configuration, path + "item.", this.file);
        button.setItemStack(itemStack);

        button.setButtonName(buttonName);
        button.setMessages(YamlSections.getStringList(configuration, path + "messages"));

        String playerHead = configuration.getString(path + "playerHead", configuration.getString(path + "player-head", configuration.getString(path + "item.playerHead", configuration.getString(path + "item.player-head", defaultButtonValue.getPlayerHead()))));

        if (playerHead != null) {
            itemStack.setMaterial("PLAYER_HEAD");
            button.setPlayerHead(playerHead);
        }

        button.setUpdated(configuration.getBoolean(path + "update", defaultButtonValue.isUpdate()));
        button.setMasterButtonUpdated(configuration.getBoolean(path + "updateMasterButton", configuration.getBoolean(path + "update-master-button", defaultButtonValue.isUpdateMasterButton())));
        button.setRefreshOnClick(configuration.getBoolean(path + "refreshOnClick", configuration.getBoolean(path + "refresh-on-click", defaultButtonValue.isRefreshOnClick())));
        button.setRefreshOnDrag(configuration.getBoolean(path + "refreshOnDrag", configuration.getBoolean(path + "refresh-on-drag", defaultButtonValue.isRefreshOnDrag())));
        button.setUseCache(configuration.getBoolean(path + "useCache", configuration.getBoolean(path + "use-cache", defaultButtonValue.isUseCache())));
        button.setOpenAsync(configuration.getBoolean(path + "openAsync", configuration.getBoolean(path + "open-async", false)));

        String loadString = null;
        if (configuration.contains(path + "openLink")) {
            loadString = "openLink";
        } else if (configuration.contains(path + "open-link")) {
            loadString = "open-link";
        }
        if (configuration.contains(path + loadString)) {

            Loader<OpenLink> loaderLink = new OpenLinkLoader(this.plugin);
            button.setOpenLink(loaderLink.load(configuration, path + loadString + "."));

        }

        String sound = configuration.getString(path + "sound", null);
        Optional<XSound> optionalXSound = sound == null || sound.isEmpty() ? Optional.empty() : XSound.of(sound);
        String categoryName = configuration.getString(path + "sound-category", configuration.getString(path + "category", XSound.Category.MASTER.name()));
        float pitch = Float.parseFloat(configuration.getString(path + "pitch", "1.0f"));
        float volume = Float.parseFloat(configuration.getString(path + "volume", "1.0f"));

        if (optionalXSound.isPresent()) {
            XSound xSound = optionalXSound.get();
            button.setSoundOption(new ZSoundOption(xSound, categoryName, null, pitch, volume, false));
        } else {
            button.setSoundOption(new ZSoundOption(null, categoryName, sound, pitch, volume, true));
        }

        Loader<ActionPlayerData> loaderActions = new ActionPlayerDataLoader(this.plugin);

        List<ActionPlayerData> actionPlayerDatas = new ArrayList<>();
        if (configuration.isSection(path + "datas")) {
            for (String key : configuration.getSection(path + "datas.").getRoutesAsStrings(false)) {
                ActionPlayerData actionPlayerData = loaderActions.load(configuration, path + "datas." + key + ".");
                actionPlayerDatas.add(actionPlayerData);
            }
        }

        button.setDatas(actionPlayerDatas);

        List<String> permissions = YamlSections.getStringList(configuration, path + "permission");
        if (permissions.isEmpty()) permissions = YamlSections.getStringList(configuration, path + "permissions");
        if (!permissions.isEmpty()) {
            List<PermissionPermissible> mappedPermissions = new ArrayList<>(permissions.size());
            for (String permissionValue : permissions) {
                mappedPermissions.add(new ZPermissionPermissible(permissionValue));
            }
            button.setPermissions(mappedPermissions);
        }
        String permission = configuration.getString(path + "permission", null);
        if (permission != null) {
            button.getPermissions().add(new ZPermissionPermissible(permission));
        }

        List<String> orPermissions = YamlSections.getStringList(configuration, path + "orPermission");
        if (orPermissions.isEmpty()) {
            orPermissions = YamlSections.getStringList(configuration, path + "or-permission");
        }
        if (orPermissions.isEmpty()) {
            orPermissions = YamlSections.getStringList(configuration, path + "orPermissions");
        }
        if (orPermissions.isEmpty()) {
            orPermissions = YamlSections.getStringList(configuration, path + "or-permissions");
        }
        List<PermissionPermissible> mappedOrPermissions = new ArrayList<>(orPermissions.size());
        for (String permissionValue : orPermissions) {
            mappedOrPermissions.add(new ZPermissionPermissible(permissionValue));
        }
        button.setOrPermissions(mappedOrPermissions);

        if (configuration.contains(path + "else")) {

            DefaultButtonValue elseDefaultButtonValue = new DefaultButtonValue(this.inventorySize, this.matrix, this.file);
            elseDefaultButtonValue.setSlot(slot);
            elseDefaultButtonValue.setSlots(slots);
            elseDefaultButtonValue.setPage(page);
            elseDefaultButtonValue.setPermanent(button.isPermanent());
            elseDefaultButtonValue.setUseCache(button.isUseCache());
            elseDefaultButtonValue.setUpdate(button.isUpdated());
            elseDefaultButtonValue.setUpdateMasterButton(button.isUpdatedMasterButton());
            elseDefaultButtonValue.setUpdateOnClick(button.updateOnClick());

            Button elseButton = this.load(configuration, path + "else.", buttonName + ".else", elseDefaultButtonValue, actionPatterns);
            button.setElseButton(elseButton);

            if (elseButton != null) {
                elseButton.setParentButton(button);
            }
        }

        PermissibleLoader permissibleLoader = new PlaceholderPermissibleLoader(this.plugin.getButtonManager());
        List<Map<String, Object>> placeholderMaps = (List<Map<String, Object>>) YamlSections.getList(configuration, path + "placeholders", new ArrayList<>());
        List<PlaceholderPermissible> placeholders = new ArrayList<>();
        for (Map<String, Object> map : placeholderMaps) {
            PlaceholderPermissible permissible = (PlaceholderPermissible) permissibleLoader.load(path + "placeholders", new TypedMapAccessor(map), this.file);
            if (permissible == null || !permissible.isValid()) {
                Logger.info("A placeholder is invalid in the placeholder list of the button " + path + " in file " + this.file.getAbsolutePath(), Logger.LogType.ERROR);
                continue;
            }
            placeholders.add(permissible);
        }

        String placeholder = configuration.getString(path + "placeHolder", configuration.getString(path + "placeholder", null));
        PlaceholderAction placeholderAction = PlaceholderAction.from(configuration.getString(path + "action", null));
        String placeholderValue = configuration.getString(path + "value", null);
        String targetPlayer = configuration.getString(path + "target", null);
        boolean mathExpression = configuration.getBoolean(path + "math");
        if (placeholderAction != null && placeholderValue != null && placeholder != null) {
            placeholders.add(new ZPlaceholderPermissible(placeholderAction, placeholder, placeholderValue, targetPlayer, new ArrayList<>(), new ArrayList<>(), mathExpression));
        }

        button.setPlaceholders(placeholders);

        // Perform commands
        List<String> commands = YamlSections.getStringList(configuration, path + "commands");
        if (commands.isEmpty()) commands = YamlSections.getStringList(configuration, path + "playerCommands");
        if (commands.isEmpty()) commands = YamlSections.getStringList(configuration, path + "player-commands");

        List<String> leftCommands = YamlSections.getStringList(configuration, path + "leftCommands");
        if (leftCommands.isEmpty()) leftCommands = YamlSections.getStringList(configuration, path + "left-commands");
        if (leftCommands.isEmpty()) leftCommands = YamlSections.getStringList(configuration, path + "leftPlayerCommands");
        if (leftCommands.isEmpty()) leftCommands = YamlSections.getStringList(configuration, path + "left-player-commands");

        List<String> rightCommands = YamlSections.getStringList(configuration, path + "rightCommands");
        if (rightCommands.isEmpty()) rightCommands = YamlSections.getStringList(configuration, path + "right-commands");
        if (rightCommands.isEmpty()) rightCommands = YamlSections.getStringList(configuration, path + "rightPlayerCommands");
        if (rightCommands.isEmpty()) rightCommands = YamlSections.getStringList(configuration, path + "right-player-commands");

        List<String> consoleCommands = YamlSections.getStringList(configuration, path + "consoleCommands");
        if (consoleCommands.isEmpty()) {
            consoleCommands = YamlSections.getStringList(configuration, path + "console-commands");
        }

        List<String> consoleRightCommands = YamlSections.getStringList(configuration, path + "consoleRightCommands");
        if (consoleRightCommands.isEmpty()) {
            consoleRightCommands = YamlSections.getStringList(configuration, path + "console-right-commands");
        }

        List<String> consoleLeftCommands = YamlSections.getStringList(configuration, path + "consoleLeftCommands");
        if (consoleLeftCommands.isEmpty()) {
            consoleLeftCommands = YamlSections.getStringList(configuration, path + "console-left-commands");
        }

        List<String> consolePermissionCommands = YamlSections.getStringList(configuration, path + "consolePermissionCommands");
        if (consolePermissionCommands.isEmpty()) {
            consolePermissionCommands = YamlSections.getStringList(configuration, path + "console-permission-commands");
        }
        String consolePermission = configuration.getString(path + "consolePermission", configuration.getString(path + "console-permission"));

        button.setCommands(commands);
        button.setLeftCommands(leftCommands);
        button.setRightCommands(rightCommands);
        button.setConsoleCommands(consoleCommands);
        button.setConsoleRightCommands(consoleRightCommands);
        button.setConsoleLeftCommands(consoleLeftCommands);
        button.setConsolePermissionCommands(consolePermissionCommands);
        button.setConsolePermission(consolePermission);

        // Load view requirements
        this.loadViewRequirements(button, configuration, path, this.file);
        // Load clicks requirements
        this.loadClickRequirements(button, configuration, path, this.file, actionPatterns);
        // Load refresh requirements
        this.loadRefreshRequirements(button, configuration, path, this.file);
        // Load actions
        boolean stopOnEmpty = configuration.getBoolean(path + "stop-on-empty", true);
        List<Action> actions = buttonManager.loadActions((List<Map<String, Object>>) YamlSections.getList(configuration, path + "actions", new ArrayList<>()), path + "actions", this.file, actionPatterns, true, stopOnEmpty);

        button.setActions(actions);

        InventoryManager inventoryManager = this.plugin.getInventoryManager();

        List<ButtonOption> buttonOptions = new ArrayList<>();
        for (ButtonOptionType type : inventoryManager.getRegistries().registry(Registries.BUTTON_OPTION)) {
            ButtonOption instance = this.createInstance(type.plugin(), type.optionClass());
            if (instance != null) {
                buttonOptions.add(instance);
            }
        }
        buttonOptions.forEach(option -> option.loadButton(button, configuration, path, inventoryManager, buttonManager, itemStackLoader, this.file));
        button.setOptions(buttonOptions);

        ButtonLoadEvent buttonLoadEvent = new ButtonLoadEvent(configuration, path, buttonManager, loader, button);
        if (Configuration.enableFastEvent) {
            inventoryManager.getFastEvents().forEach(event -> event.onButtonLoad(buttonLoadEvent));
        } else buttonLoadEvent.call();

        return button;
    }

    @Override
    @Deprecated
    public Button load(YamlConfiguration configuration, String path, Object... objects) throws InventoryException  {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(configurationDocument, path, objects);
    }

    private void loadDefaultPatternValues(YamlDocument patternConfig, Map<String, Object> mapPlaceholders) {
        Section defaultValueConfigurationSection = patternConfig.getSection("default-values.");
        if (defaultValueConfigurationSection != null) {
            Map<String, Object> defaultValues = defaultValueConfigurationSection.getStringRouteMappedValues(true);
            for (String key : defaultValues.keySet()) {
                if (!mapPlaceholders.containsKey(key)) {
                    mapPlaceholders.put(key, defaultValues.get(key));
                }
            }
        }
    }

    /**
     * Allows to load clicks requirements
     *
     * @param button        The button
     * @param configuration the configuration
     * @param file          the file
     * @param path          current path in configuration
     */
    private void loadClickRequirements(Button button, YamlDocument configuration, String path, File file, List<ActionPattern> actionPatterns) throws InventoryException {

        String[] sectionStrings = this.plugin.getClickRequirementKeys();
        Section section = null;
        String sectionString = "";
        for (String string : sectionStrings) {
            sectionString = string;
            section = configuration.getSection(path + sectionString);
            if (section != null) break;
        }
        if (section == null) return;

        Loader<Requirement> loader = new RequirementLoader(this.plugin);
        List<Requirement> requirements = new ArrayList<>();
        for (String key : section.getRoutesAsStrings(false)) {
            requirements.add(loader.load(configuration, path + sectionString + key + ".", file, actionPatterns));
        }
        button.setClickRequirements(requirements);
    }

    /**
     * Allows loading view requirements
     *
     * @param button        The button
     * @param configuration the configuration
     * @param file          the file
     * @param path          current path in configuration
     * @throws InventoryException if the configuration does not contain the "view_requirement" section
     */
    private void loadViewRequirements(Button button, YamlDocument configuration, String path, File file) throws InventoryException {
        Loader<Requirement> loader = new RequirementLoader(this.plugin);
        String requirementPath = configuration.isSection(path + "view_requirement.") ? "view_requirement." : configuration.isSection(path + "view-requirement.") ? "view-requirement." : null;
        if (requirementPath == null) return;

        button.setViewRequirement(loader.load(configuration, path + requirementPath, file));
    }

    /**
     * Allows loading refresh requirements
     *
     * @param button        The button
     * @param configuration the configuration
     * @param file          the file
     * @param path          current path in configuration
     */
    private void loadRefreshRequirements(Button button, YamlDocument configuration, String path, File file) throws InventoryException {
        Loader<RefreshRequirement> loader = new RefreshRequiementLoader(this.plugin);
        String requirementPath = configuration.isSection(path + "refresh_requirements.") ? "refresh_requirements." : configuration.isSection(path + "refresh-requirements.") ? "refresh-requirements." : null;
        if (requirementPath == null) return;

        button.setRefreshRequirement(loader.load(configuration, path + requirementPath, file));
    }

    /**
     * Buttons serialize themselves; this only supplies the inventory size they need to write
     * slots relative to their page.
     */
    @Override
    public void serialize(@NonNull Button button, @NonNull Section section, Object... objects) {
        button.serialize(section, this.inventorySize);
    }

    @Override
    @Deprecated
    public void serialize(Button button, ConfigurationSection section, Object... objects) {
        YamlSections.write(section, copy -> this.serialize(button, copy, objects));
    }

    private ButtonOption createInstance(Plugin plugin, Class<? extends ButtonOption> aClass) {
        try {
            Constructor<? extends ButtonOption> constructor = aClass.getConstructor(Plugin.class);
            return constructor.newInstance(plugin);
        } catch (NoSuchMethodException ignored) {
            try {
                return aClass.getConstructor().newInstance();
            } catch (Exception exception) {
                this.warnOptionOnce(aClass, "it needs a public constructor taking a Plugin, or a public no-argument constructor");
                return null;
            }
        } catch (Exception exception) {
            this.warnOptionOnce(aClass, exception.toString());
            return null;
        }
    }

    private void warnOptionOnce(Class<? extends ButtonOption> aClass, String reason) {
        if (WARNED_OPTIONS.add(aClass)) {
            Logger.info("The button option " + aClass.getName() + " was ignored: " + reason + ".", Logger.LogType.WARNING);
        }
    }

}
