package fr.maxlego08.menu.loader.deluxemenu;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.api.event.events.ButtonLoadEvent;
import fr.maxlego08.menu.api.exceptions.InventoryButtonException;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.loader.MenuItemStackLoader;
import fr.maxlego08.menu.requirement.ZRequirement;
import fr.maxlego08.menu.requirement.permissible.ZPermissionPermissible;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.ClickType;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ButtonDeluxeMenuLoader extends DeluxeMenuCommandUtils implements Loader<Button> {

    private final ZMenuPlugin plugin;
    private final File file;
    private final int inventorySize;

    public ButtonDeluxeMenuLoader(ZMenuPlugin plugin, File file, int inventorySize) {
        super();
        this.plugin = plugin;
        this.file = file;
        this.inventorySize = inventorySize;
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, Object... objects) throws InventoryException {

        String buttonType = "NONE";
        String buttonName = (String) objects[0];
        DefaultButtonValue defaultButtonValue = objects.length == 2 ? (DefaultButtonValue) objects[1] : new DefaultButtonValue(inventorySize, new HashMap<>(), file);

        ButtonManager buttonManager = this.plugin.getButtonManager();
        Optional<ButtonLoader> optional = buttonManager.getLoader(buttonType);

        if (!optional.isPresent()) {
            throw new InventoryButtonException("Impossible to find the type " + buttonType + " for the button " + path + " in inventory " + this.file.getAbsolutePath());
        }

        Loader<MenuItemStack> itemStackLoader = new MenuItemStackLoader(this.plugin.getInventoryManager());

        ButtonLoader loader = optional.get();
        Button button = (Button) loader.load(configuration, path, defaultButtonValue);
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
        if (slots.isEmpty()) slots = defaultButtonValue.getSlots();

        button.setSlots(slots);
        button.setSlot(slot);
        button.setPage(page);

        InventoryManager inventoryManager = this.plugin.getInventoryManager();
        MenuItemStack itemStack = itemStackLoader.load(configuration, path + ".", file);
        button.setItemStack(itemStack);
        button.setButtonName(buttonName);

        // Click requirement
        List<String> clickCommands = configuration.getStringList(path + "click_commands");
        List<String> leftClickCommands = configuration.getStringList(path + "left_click_commands");
        List<String> rightClickCommands = configuration.getStringList(path + "right_click_commands");
        List<String> middleClickCommands = configuration.getStringList(path + "middle_click_commands");
        List<String> shiftLeftClickCommands = configuration.getStringList(path + "shift_left_click_commands");
        List<String> shiftRightClickCommands = configuration.getStringList(path + "shift_right_click_commands");

        List<Action> actions = loadActions(inventoryManager, plugin.getCommandManager(), plugin, clickCommands);
        List<Action> leftActions = loadActions(inventoryManager, plugin.getCommandManager(), plugin, leftClickCommands);
        List<Action> rightActions = loadActions(inventoryManager, plugin.getCommandManager(), plugin, rightClickCommands);
        List<Action> middleActions = loadActions(inventoryManager, plugin.getCommandManager(), plugin, middleClickCommands);
        List<Action> shiftLeftActions = loadActions(inventoryManager, plugin.getCommandManager(), plugin, shiftLeftClickCommands);
        List<Action> shiftRightActions = loadActions(inventoryManager, plugin.getCommandManager(), plugin, shiftRightClickCommands);

        List<Requirement> requirements = new ArrayList<>();

        ConfigurationSection leftClickRequirementSection = configuration.getConfigurationSection(path + "left_click_requirement");
        if (leftClickRequirementSection != null) {
            Requirement requirement = loadRequirement(leftActions.isEmpty() ? actions : leftActions, leftClickRequirementSection, ClickType.LEFT);
            requirements.add(requirement);
        }

        ConfigurationSection rightClickRequirementSection = configuration.getConfigurationSection(path + "right_click_requirement");
        if (rightClickRequirementSection != null) {
            Requirement requirement = loadRequirement(rightActions.isEmpty() ? actions : rightActions, rightClickRequirementSection, ClickType.RIGHT);
            requirements.add(requirement);
        }

        ConfigurationSection shiftLeftClickRequirement = configuration.getConfigurationSection(path + "shift_left_click_requirement");
        if (shiftLeftClickRequirement != null) {
            Requirement requirement = loadRequirement(shiftLeftActions, shiftLeftClickRequirement, ClickType.SHIFT_LEFT);
            requirements.add(requirement);
        }

        ConfigurationSection shiftRightClickRequirement = configuration.getConfigurationSection(path + "shift_right_click_requirement");
        if (shiftRightClickRequirement != null) {
            Requirement requirement = loadRequirement(shiftRightActions, shiftRightClickRequirement, ClickType.SHIFT_RIGHT);
            requirements.add(requirement);
        }

        ConfigurationSection middleClickRequirement = configuration.getConfigurationSection(path + "middle_click_requirement");
        if (middleClickRequirement != null) {
            Requirement requirement = loadRequirement(middleActions, middleClickRequirement, ClickType.SHIFT_RIGHT);
            requirements.add(requirement);
        }

        if (requirements.isEmpty()) {
            List<Action> globalActions = leftActions.isEmpty() ? rightActions.isEmpty() ? actions : rightActions : leftActions;
            if (!globalActions.isEmpty()) {
                Requirement requirement = new ZRequirement(0, new ArrayList<>(), new ArrayList<>(), globalActions, Config.allClicksType);
                requirements.add(requirement);
            }
        }

        button.setClickRequirements(requirements);

        // View Requirements
        ConfigurationSection viewRequirementSection = configuration.getConfigurationSection(path + "view_requirement");
        if (viewRequirementSection != null) {
            Requirement requirement = loadRequirement(new ArrayList<>(), viewRequirementSection);
            button.setViewRequirement(requirement);
        }

        button.setUpdated(configuration.getBoolean(path + "update", defaultButtonValue.isUpdate()));
        button.setPriority(configuration.getInt(path + "priority", -1));

        List<String> permissions = configuration.getStringList(path + "permission");
        permissions = permissions.isEmpty() ? configuration.getStringList(path + "permissions") : permissions;
        if (permissions.isEmpty()) {
            String permission = configuration.getString(path + "permission", null);
            if (permission != null) {
                permissions.add(permission);
            }
        }
        button.setPermissions(permissions.stream().map(ZPermissionPermissible::new).collect(Collectors.toList()));
        List<String> orPermissions = configuration.getStringList(path + "orPermission");
        button.setOrPermissions((orPermissions.isEmpty() ? configuration.getStringList(path + "orPermissions") : orPermissions).stream().map(ZPermissionPermissible::new).collect(Collectors.toList()));

        ButtonLoadEvent buttonLoadEvent = new ButtonLoadEvent(configuration, path, buttonManager, loader, button);
        if (Config.enableFastEvent) {
            inventoryManager.getFastEvents().forEach(event -> event.onButtonLoad(buttonLoadEvent));
        } else buttonLoadEvent.call();

        return button;
    }

    private Requirement loadRequirement(List<Action> actions, ConfigurationSection configurationSection, ClickType... clickTypes) {
        List<Permissible> permissibles = new ArrayList<>();
        ConfigurationSection configurationSectionRequirements = configurationSection.getConfigurationSection("requirements");
        if (configurationSectionRequirements != null) {
            permissibles = loadPermissibles(plugin.getInventoryManager(), plugin.getCommandManager(), plugin, configurationSectionRequirements);
        }

        List<Action> denyActions = loadActions(plugin.getInventoryManager(), plugin.getCommandManager(), plugin, configurationSection.getStringList("deny_commands"));

        return new ZRequirement(configurationSection.getInt("minimum_requirements", permissibles.size()), permissibles, denyActions, actions, Arrays.asList(clickTypes));
    }

    @Override
    public void save(Button object, YamlConfiguration configuration, String path, File file, Object... objects) {
        //TODO: FINISH THE SAVE METHOD
    }

}
