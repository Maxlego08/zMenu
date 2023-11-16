package fr.maxlego08.menu.loader;

import com.cryptomorin.xseries.XSound;
import fr.maxlego08.menu.MenuItemStack;
import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.action.ActionLoader;
import fr.maxlego08.menu.action.loader.ActionPlayerDataLoader;
import fr.maxlego08.menu.action.permissible.ZPlaceholderPermissible;
import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.action.Action;
import fr.maxlego08.menu.api.action.data.ActionPlayerData;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.requirement.permissible.PermissionPermissible;
import fr.maxlego08.menu.api.requirement.permissible.PlaceholderPermissible;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.enums.PlaceholderAction;
import fr.maxlego08.menu.api.event.events.ButtonLoadEvent;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.utils.OpenLink;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.button.ZPermissibleButton;
import fr.maxlego08.menu.exceptions.InventoryButtonException;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.save.Config;
import fr.maxlego08.menu.sound.ZSoundOption;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import fr.maxlego08.menu.zcore.utils.loader.Loader;
import fr.maxlego08.menu.zcore.utils.nms.NMSUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ZButtonLoader extends ZUtils implements Loader<Button> {

    private final MenuPlugin plugin;
    private final File file;
    private final int inventorySize;

    public ZButtonLoader(MenuPlugin plugin, File file, int inventorySize) {
        super();
        this.plugin = plugin;
        this.file = file;
        this.inventorySize = inventorySize;
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, Object... objects) throws InventoryException {

        String buttonType = configuration.getString(path + "type", "NONE");
        String buttonName = (String) objects[0];
        DefaultButtonValue defaultButtonValue = objects.length == 2 ? (DefaultButtonValue) objects[1] : new DefaultButtonValue();

        ButtonManager buttonManager = this.plugin.getButtonManager();
        Optional<ButtonLoader> optional = buttonManager.getLoader(buttonType);

        if (!optional.isPresent()) {
            throw new InventoryButtonException("Impossible to find the type " + buttonType + " for the button " + path + " in inventory " + this.file.getAbsolutePath());
        }

        Loader<MenuItemStack> itemStackLoader = new MenuItemStackLoader(this.plugin.getInventoryManager());

        ButtonLoader loader = optional.get();
        ZButton button = (ZButton) loader.load(configuration, path, defaultButtonValue);
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
                slot = configuration.getInt(path + "slot", defaultButtonValue.getSlot());
                page = configuration.getInt(path + "page", defaultButtonValue.getPage());
            }

        } catch (Exception e) {
            slot = configuration.getInt(path + "slot", defaultButtonValue.getSlot());
            page = configuration.getInt(path + "page", defaultButtonValue.getPage());
        }

        page = Math.max(page, 1);
        slot = slot + ((page - 1) * this.inventorySize);

        List<String> slotsAsString = configuration.getStringList(path + "slots");
        List<Integer> slots = ButtonLoader.loadSlot(slotsAsString);
        if (slots.isEmpty()) slots = defaultButtonValue.getSlots();

        button.setSlots(slots);
        button.setSlot(slot);
        button.setPermanent(configuration.getBoolean(path + "isPermanent", defaultButtonValue.isPermanent()));
        button.setUpdateOnClick(configuration.getBoolean(path + "updateOnClick", defaultButtonValue.isUpdateOnClick()));
        button.setCloseInventory(configuration.getBoolean(path + "closeInventory", defaultButtonValue.isCloseInventory()));

        MenuItemStack itemStack = itemStackLoader.load(configuration, path + "item.", file);
        button.setItemStack(itemStack);

        button.setButtonName(buttonName);
        button.setMessages(configuration.getStringList(path + "messages"));

        String playerHead = configuration.getString(path + "playerHead", configuration.getString(path + "item.playerHead", defaultButtonValue.getPlayerHead()));

        if (playerHead != null) {
            if (NMSUtils.isNewVersion()) {
                itemStack.setMaterial("PLAYER_HEAD");
            } else {
                itemStack.setMaterial("SKULL_ITEM");
                itemStack.setData(3);
            }
            button.setPlayerHead(playerHead);
        }

        button.setUpdated(configuration.getBoolean(path + "update", defaultButtonValue.isUpdate()));
        button.setRefreshOnClick(configuration.getBoolean(path + "refreshOnClick", defaultButtonValue.isRefreshOnClick()));

        if (configuration.contains(path + "openLink")) {

            Loader<OpenLink> loaderLink = new OpenLinkLoader();
            button.setOpenLink(loaderLink.load(configuration, path + "openLink."));

        }

        String sound = configuration.getString(path + "sound", null);
        Optional<XSound> optionalXSound = sound == null || sound.isEmpty() ? Optional.empty() : XSound.matchXSound(sound);

        if (optionalXSound.isPresent()) {
            XSound xSound = optionalXSound.get();
            float pitch = Float.parseFloat(configuration.getString(path + "pitch", "1.0f"));
            float volume = Float.parseFloat(configuration.getString(path + "volume", "1.0f"));
            button.setSoundOption(new ZSoundOption(xSound, pitch, volume));
        }

        Loader<ActionPlayerData> loaderActions = new ActionPlayerDataLoader();

        List<ActionPlayerData> actionPlayerDatas = new ArrayList<>();
        if (configuration.isConfigurationSection(path + "datas")) {
            for (String key : configuration.getConfigurationSection(path + "datas.").getKeys(false)) {

                ActionPlayerData actionPlayerData = loaderActions.load(configuration, path + "datas." + key + ".");
                actionPlayerDatas.add(actionPlayerData);

            }
        }

        button.setDatas(actionPlayerDatas);

        List<String> permissions = configuration.getStringList(path + "permission");
        button.setPermissions(permissions.isEmpty() ? configuration.getStringList(path + "permissions") : permissions, configuration.getString(path + "permission", null));
        List<String> orPermissions = configuration.getStringList(path + "orPermission");
        button.setOrPermissionsString(orPermissions.isEmpty() ? configuration.getStringList(path + "orPermissions") : orPermissions);

        if (configuration.contains(path + "else")) {

            DefaultButtonValue elseDefaultButtonValue = new DefaultButtonValue();
            elseDefaultButtonValue.setSlot(slot);
            elseDefaultButtonValue.setSlots(slots);
            elseDefaultButtonValue.setPage(page);
            elseDefaultButtonValue.setPermanent(button.isPermament());

            Button elseButton = this.load(configuration, path + "else.", buttonName + ".else", elseDefaultButtonValue);
            button.setElseButton(elseButton);

            if (elseButton != null) {
                ZPermissibleButton elsePermissibleButton = (ZPermissibleButton) elseButton;
                elsePermissibleButton.setParentButton(button);
            }
        }

        List<PlaceholderPermissible> placeholders = ((List<Map<String, Object>>) configuration.getList(path + "placeholders", new ArrayList<>())).stream().map(ZPlaceholderPermissible::new).filter(permissible -> {
            if (!permissible.isValid()) {
                Logger.info("A placeholder is invalid in the placeholder list of the button " + path, Logger.LogType.ERROR);
                return false;
            }
            return true;
        }).collect(Collectors.toList());

        String placeholder = configuration.getString(path + "placeHolder", configuration.getString(path + "placeholder", null));
        PlaceholderAction placeholderAction = PlaceholderAction.from(configuration.getString(path + "action", null));
        String placeholderValue = configuration.getString(path + "value", null);
        if (placeholderAction != null && placeholderValue != null && placeholder != null) {
            placeholders.add(new ZPlaceholderPermissible(placeholderAction, placeholder, placeholderValue));
        }

        button.setPlaceholders(placeholders);

        // Perform commands
        List<String> commands = configuration.getStringList(path + "commands");
        if (commands.isEmpty()) commands = configuration.getStringList(path + "playerCommands");

        List<String> consoleCommands = configuration.getStringList(path + "consoleCommands");
        List<String> consoleRightCommands = configuration.getStringList(path + "consoleRightCommands");
        List<String> consoleLeftCommands = configuration.getStringList(path + "consoleLeftCommands");
        List<String> consolePermissionCommands = configuration.getStringList(path + "consolePermissionCommands");
        String consolePermission = configuration.getString(path + "consolePermission");

        List<Action> actions = new ArrayList<>();
        Loader<Action> actiuonLoader = new ActionLoader(this.plugin);

        if (configuration.isConfigurationSection(path + "actions.")) {
            ConfigurationSection configurationSection = configuration.getConfigurationSection(path + "actions.");
            if (configurationSection != null) {
                for (String key : configurationSection.getKeys(false)) {

                    try {
                        actions.add(actiuonLoader.load(configuration, path + "actions." + key + "."));
                    } catch (InventoryException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        button.setCommands(commands);
        button.setConsoleCommands(consoleCommands);
        button.setConsoleRightCommands(consoleRightCommands);
        button.setConsoleLeftCommands(consoleLeftCommands);
        button.setConsolePermissionCommands(consolePermissionCommands);
        button.setConsolePermission(consolePermission);
        button.setActions(actions);

        InventoryManager inventoryManager = this.plugin.getInventoryManager();
        ButtonLoadEvent buttonLoadEvent = new ButtonLoadEvent(configuration, path, buttonManager, loader, button);
        if (Config.enableFastEvent)
            inventoryManager.getFastEvents().forEach(event -> event.onButtonLoad(buttonLoadEvent));
        else buttonLoadEvent.call();

        // Load view requirements
        loadViewRequirements(button, configuration, buttonManager, path);

        return button;
    }

    /**
     * Allows to load permissions and placeholders that will have to be checked when opening the button
     *
     * @param button        The button
     * @param configuration the configuration
     * @param buttonManager the button manager
     * @param path          current path in configuration
     */
    private void loadViewRequirements(ZButton button, YamlConfiguration configuration, ButtonManager buttonManager, String path) {

        List<Map<String, Object>> viewRequirements = (List<Map<String, Object>>) configuration.getList(path + "view_requirement", new ArrayList<>());
        if (!viewRequirements.isEmpty()) {

            List<PermissionPermissible> permissionPermissibles = new ArrayList<>();
            List<PermissionPermissible> orPermissionPermissibles = new ArrayList<>();
            List<PlaceholderPermissible> placeholderPermissibles = new ArrayList<>();

            viewRequirements.forEach(map -> {

                String type = (String) map.getOrDefault("type", null);
                if (type == null) return;

                Optional<Class<? extends Permissible>> optional = buttonManager.getPermission(type);
                if (optional.isPresent()) {
                    Class<? extends Permissible> aClass = optional.get();
                    try {
                        Permissible permissible = aClass.getConstructor(Map.class).newInstance(map);

                        // ToDo, improve the whole system to ultimately have only one list of Permission and not several list as currently
                        switch (type.toLowerCase()) {
                            case "permission":
                                permissionPermissibles.add((PermissionPermissible) permissible);
                                break;
                            case "or_permission":
                                orPermissionPermissibles.add((PermissionPermissible) permissible);
                                break;
                            case "placeholder":
                                placeholderPermissibles.add((PlaceholderPermissible) permissible);
                                break;
                        }

                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException exception) {
                        exception.printStackTrace();
                    }
                }
            });

            if (!permissionPermissibles.isEmpty()) button.setPermissions(permissionPermissibles);
            if (!orPermissionPermissibles.isEmpty()) button.setOrPermissions(orPermissionPermissibles);
            if (!placeholderPermissibles.isEmpty()) button.setPlaceholders(placeholderPermissibles);
        }
    }

    @Override
    public void save(Button object, YamlConfiguration configuration, String path, File file, Object... objects) {
        //TODO: FINISH THE SAVE METHOD
    }

}
