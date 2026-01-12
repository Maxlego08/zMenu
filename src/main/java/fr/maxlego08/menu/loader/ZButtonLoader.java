package fr.maxlego08.menu.loader;

import com.cryptomorin.xseries.XSound;
import fr.maxlego08.menu.ZMenuItemStack;
import fr.maxlego08.menu.api.ButtonManager;
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
import fr.maxlego08.menu.loader.permissible.PlaceholderPermissibleLoader;
import fr.maxlego08.menu.requirement.permissible.ZPermissionPermissible;
import fr.maxlego08.menu.requirement.permissible.ZPlaceholderPermissible;
import fr.maxlego08.menu.sound.ZSoundOption;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import fr.maxlego08.menu.zcore.utils.nms.NmsVersion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.*;

public class ZButtonLoader extends ZUtils implements Loader<Button> {

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
    public Button load(@NonNull YamlConfiguration configuration, @NonNull String path, Object... objects) throws InventoryException {

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

        ConfigurationSection patternSection = configuration.getConfigurationSection(path + "pattern");

        // Using the pattern file to load the button
        if (patternSection != null) {

            Map<String, Object> mapPlaceholders = new HashMap<>();
            patternSection.getKeys(false).forEach(key -> mapPlaceholders.put(key, patternSection.get(key)));

            String fileName = configuration.getString(path + "pattern.fileName", configuration.getString(path + "pattern.file-name", configuration.getString(path + "pattern.file")));
            String pluginName = configuration.getString(path + "pattern.pluginName", configuration.getString(path + "pattern.plugin-name", configuration.getString(path + "pattern.plugin", null)));
            Plugin patternPlugin = pluginName != null ? Bukkit.getPluginManager().getPlugin(pluginName) : this.plugin;
            if (patternPlugin == null) throw new InventoryButtonException("Impossible to load the pattern " + fileName);

            File patternFile = new File(patternPlugin.getDataFolder(), "patterns/" + fileName + ".yml");
            if (!patternFile.exists()) {
                throw new InventoryButtonException("Impossible to load the pattern " + fileName + ", file doesnt exist");
            }
            loadDefaultPatternValues(patternFile, mapPlaceholders);

            mapPlaceholders.putAll(this.plugin.getGlobalPlaceholders());
            YamlConfiguration patternConfiguration = loadAndReplaceConfiguration(patternFile, mapPlaceholders);
            Button patternButton = this.load(patternConfiguration, "button.", buttonName, actionPatterns);
            // Load view requirements for the pattern button (from main config)
            loadViewRequirements(patternButton, configuration, path, file);

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

                slot = parseInt(configuration.getString(path + "slot", null), defaultButtonValue.getSlot());
                page = parseInt(configuration.getString(path + "page", null), defaultButtonValue.getPage());
            }
        } catch (Exception ignored) {
            slot = parseInt(configuration.getString(path + "slot", null), defaultButtonValue.getSlot());
            page = parseInt(configuration.getString(path + "page", null), defaultButtonValue.getPage());
        }

        page = Math.max(page, 1);
        if (slot != defaultButtonValue.getSlot() || slot == 0) {
            slot = slot + ((page - 1) * this.inventorySize);
        }

        List<String> slotsAsString = configuration.getStringList(path + "slots");
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

        ZMenuItemStack itemStack = (ZMenuItemStack) itemStackLoader.load(configuration, path + "item.", file);
        button.setItemStack(itemStack);

        button.setButtonName(buttonName);
        button.setMessages(configuration.getStringList(path + "messages"));

        String playerHead = configuration.getString(path + "playerHead", configuration.getString(path + "player-head", configuration.getString(path + "item.playerHead", configuration.getString(path + "item.player-head", defaultButtonValue.getPlayerHead()))));

        if (playerHead != null) {
            if (NmsVersion.nmsVersion.isNewMaterial()) {
                itemStack.setMaterial("PLAYER_HEAD");
            } else {
                itemStack.setMaterial("SKULL_ITEM");
                itemStack.setData("3");
            }
            button.setPlayerHead(playerHead);
        }

        button.setUpdated(configuration.getBoolean(path + "update", defaultButtonValue.isUpdate()));
        button.setMasterButtonUpdated(configuration.getBoolean(path + "updateMasterButton", configuration.getBoolean(path + "update-master-button", defaultButtonValue.isUpdateMasterButton())));
        button.setRefreshOnClick(configuration.getBoolean(path + "refreshOnClick", configuration.getBoolean(path + "refresh-on-click", defaultButtonValue.isRefreshOnClick())));
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

        Loader<ActionPlayerData> loaderActions = new ActionPlayerDataLoader(this.plugin.getStorageManager());

        List<ActionPlayerData> actionPlayerDatas = new ArrayList<>();
        if (configuration.isConfigurationSection(path + "datas")) {
            for (String key : configuration.getConfigurationSection(path + "datas.").getKeys(false)) {
                ActionPlayerData actionPlayerData = loaderActions.load(configuration, path + "datas." + key + ".");
                actionPlayerDatas.add(actionPlayerData);
            }
        }

        button.setDatas(actionPlayerDatas);

        List<String> permissions = configuration.getStringList(path + "permission");
        if (permissions.isEmpty()) permissions = configuration.getStringList(path + "permissions");
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

        List<String> orPermissions = configuration.getStringList(path + "orPermission");
        if (orPermissions.isEmpty()) {
            orPermissions = configuration.getStringList(path + "or-permission");
        }
        if (orPermissions.isEmpty()) {
            orPermissions = configuration.getStringList(path + "orPermissions");
        }
        if (orPermissions.isEmpty()) {
            orPermissions = configuration.getStringList(path + "or-permissions");
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
        List<Map<String, Object>> placeholderMaps = (List<Map<String, Object>>) configuration.getList(path + "placeholders", new ArrayList<>());
        List<PlaceholderPermissible> placeholders = new ArrayList<>();
        for (Map<String, Object> map : placeholderMaps) {
            PlaceholderPermissible permissible = (PlaceholderPermissible) permissibleLoader.load(path + "placeholders", new TypedMapAccessor(map), file);
            if (permissible == null || !permissible.isValid()) {
                Logger.info("A placeholder is invalid in the placeholder list of the button " + path + " in file " + file.getAbsolutePath(), Logger.LogType.ERROR);
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
        List<String> commands = configuration.getStringList(path + "commands");
        if (commands.isEmpty()) commands = configuration.getStringList(path + "playerCommands");
        if (commands.isEmpty()) commands = configuration.getStringList(path + "player-commands");

        List<String> leftCommands = configuration.getStringList(path + "leftCommands");
        if (leftCommands.isEmpty()) leftCommands = configuration.getStringList(path + "left-commands");
        if (leftCommands.isEmpty()) leftCommands = configuration.getStringList(path + "leftPlayerCommands");
        if (leftCommands.isEmpty()) leftCommands = configuration.getStringList(path + "left-player-commands");

        List<String> rightCommands = configuration.getStringList(path + "rightCommands");
        if (rightCommands.isEmpty()) rightCommands = configuration.getStringList(path + "right-commands");
        if (rightCommands.isEmpty()) rightCommands = configuration.getStringList(path + "rightPlayerCommands");
        if (rightCommands.isEmpty()) rightCommands = configuration.getStringList(path + "right-player-commands");

        List<String> consoleCommands = configuration.getStringList(path + "consoleCommands");
        if (consoleCommands.isEmpty()) {
            consoleCommands = configuration.getStringList(path + "console-commands");
        }

        List<String> consoleRightCommands = configuration.getStringList(path + "consoleRightCommands");
        if (consoleRightCommands.isEmpty()) {
            consoleRightCommands = configuration.getStringList(path + "console-right-commands");
        }

        List<String> consoleLeftCommands = configuration.getStringList(path + "consoleLeftCommands");
        if (consoleLeftCommands.isEmpty()) {
            consoleLeftCommands = configuration.getStringList(path + "console-left-commands");
        }

        List<String> consolePermissionCommands = configuration.getStringList(path + "consolePermissionCommands");
        if (consolePermissionCommands.isEmpty()) {
            consolePermissionCommands = configuration.getStringList(path + "console-permission-commands");
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
        loadViewRequirements(button, configuration, path, file);
        // Load clicks requirements
        loadClickRequirements(button, configuration, path, file, actionPatterns);
        // Load refresh requirements
        loadRefreshRequirements(button, configuration, path, file);
        // Load actions
        boolean stopOnEmpty = configuration.getBoolean(path + "stop-on-empty", true);
        List<Action> actions = buttonManager.loadActions((List<Map<String, Object>>) configuration.getList(path + "actions", new ArrayList<>()), path + "actions", file, actionPatterns,true,stopOnEmpty);

        button.setActions(actions);

        InventoryManager inventoryManager = this.plugin.getInventoryManager();

        List<ButtonOption> buttonOptions = new ArrayList<>();
        for (Map.Entry<Plugin, List<Class<? extends ButtonOption>>> entry : inventoryManager.getOptions().entrySet()) {
            for (Class<? extends ButtonOption> optionClass : entry.getValue()) {
                ButtonOption instance = createInstance(entry.getKey(), optionClass);
                if (instance != null) {
                    buttonOptions.add(instance);
                }
            }
        }
        buttonOptions.forEach(option -> option.loadButton(button, configuration, path, inventoryManager, buttonManager, itemStackLoader, file));
        button.setOptions(buttonOptions);

        ButtonLoadEvent buttonLoadEvent = new ButtonLoadEvent(configuration, path, buttonManager, loader, button);
        if (Configuration.enableFastEvent) {
            inventoryManager.getFastEvents().forEach(event -> event.onButtonLoad(buttonLoadEvent));
        } else buttonLoadEvent.call();

        return button;
    }

    private void loadDefaultPatternValues(File patternFile, Map<String, Object> mapPlaceholders) {
        YamlConfiguration patternConfig = YamlConfiguration.loadConfiguration(patternFile);
        if (patternConfig.isConfigurationSection("default-values.")) {
            Map<String, Object> defaultValues = patternConfig.getConfigurationSection("default-values.").getValues(false);
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
    private void loadClickRequirements(Button button, YamlConfiguration configuration, String path, File file,List<ActionPattern> actionPatterns) throws InventoryException {
        String[] sectionStrings = {"click_requirement.", "click-requirement.", "click_requirements.", "click-requirements.", "clicks_requirement.", "clicks-requirement.", "clicks_requirements.", "clicks-requirements."};
        ConfigurationSection section = null;
        String sectionString = "";
        for (String string : sectionStrings) {
            sectionString = string;
            section = configuration.getConfigurationSection(path + sectionString);
            if (section != null) break;
        }
        if (section == null) return;

        Loader<Requirement> loader = new RequirementLoader(this.plugin);
        List<Requirement> requirements = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            requirements.add(loader.load(configuration, path + sectionString + key + ".", file,actionPatterns));
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
    private void loadViewRequirements(Button button, YamlConfiguration configuration, String path, File file) throws InventoryException {
        Loader<Requirement> loader = new RequirementLoader(this.plugin);
        String requirementPath = configuration.isConfigurationSection(path + "view_requirement.") ? "view_requirement." : configuration.isConfigurationSection(path + "view-requirement.") ? "view-requirement." : null;
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
    private void loadRefreshRequirements(Button button, YamlConfiguration configuration, String path, File file) throws InventoryException {
        Loader<RefreshRequirement> loader = new RefreshRequiementLoader(this.plugin);
        String requirementPath = configuration.isConfigurationSection(path + "refresh_requirements.") ? "refresh_requirements." : configuration.isConfigurationSection(path + "refresh-requirements.") ? "refresh-requirements." : null;
        if (requirementPath == null) return;

        button.setRefreshRequirement(loader.load(configuration, path + requirementPath, file));
    }

    @Override
    public void save(Button object, @NonNull YamlConfiguration configuration, @NonNull String path, File file, Object... objects) {
        // TODO: FINISH THE SAVE METHOD
    }

    private ButtonOption createInstance(Plugin plugin, Class<? extends ButtonOption> aClass) {
        try {
            Constructor<? extends ButtonOption> constructor = aClass.getConstructor(Plugin.class);
            return constructor.newInstance(plugin);
        } catch (Exception ignored) {
            return null;
        }
    }

}
