package fr.maxlego08.menu.loader.deluxemenu;

import com.cryptomorin.xseries.XSound;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.event.events.ButtonLoadEvent;
import fr.maxlego08.menu.api.exceptions.InventoryButtonException;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.requirement.permissible.PermissionPermissible;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.loader.MenuItemStackLoader;
import fr.maxlego08.menu.requirement.permissible.ZPermissionPermissible;
import fr.maxlego08.menu.sound.ZSoundOption;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.ClickType;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ButtonDeluxeMenuLoader extends DeluxeMenuCommandUtils implements Loader<Button> {
    private static final Map<String, ClickType> CLICKS = new LinkedHashMap<>();

    private static final Map<String, String> MATERIAL_PREFIXES = new LinkedHashMap<>();

    private static final Map<String, String> EQUIPMENT_MATERIALS = Map.of(
            "main_hand", "armor:HAND", "off_hand", "armor:OFF_HAND", "armor_helmet", "armor:HEAD",
            "armor_chestplate", "armor:CHEST", "armor_leggings", "armor:LEGS", "armor_boots", "armor:FEET");

    private static final Map<String, String> ITEM_KEY_ALIASES = Map.of(
            "item_flags", "flags", "hide_tooltip", "hide-tooltip", "enchantment_glint_override", "enchantment-glint",
            "rarity", "item-rarity", "tooltip_style", "tooltip-style", "item_model", "item-model");

    private static final List<String> UNSUPPORTED_ITEM_KEYS = List.of(
            "rgb", "banner_meta", "base_color", "potion_effects", "trim_material", "trim_pattern", "model_data_component", "light_level", "lore_append_mode");

    static {
        CLICKS.put("left", ClickType.LEFT);
        CLICKS.put("right", ClickType.RIGHT);
        CLICKS.put("shift_left", ClickType.SHIFT_LEFT);
        CLICKS.put("shift_right", ClickType.SHIFT_RIGHT);
        CLICKS.put("middle", ClickType.MIDDLE);

        MATERIAL_PREFIXES.put("hdb-", "hdb:");
        MATERIAL_PREFIXES.put("headdb-", "hdb:");
        MATERIAL_PREFIXES.put("itemsadder-", "itemsadder:");
        MATERIAL_PREFIXES.put("oraxen-", "oraxen:");
        MATERIAL_PREFIXES.put("nexo-", "nexo:");
        MATERIAL_PREFIXES.put("mmoitems-", "mmoitems:");
        MATERIAL_PREFIXES.put("craftengine-", "craftengine:");
        MATERIAL_PREFIXES.put("executableitems-", "ei:");
        MATERIAL_PREFIXES.put("executableblocks-", "eb:");
    }

    private final ZMenuPlugin plugin;
    private final File file;
    private final int inventorySize;

    public ButtonDeluxeMenuLoader(ZMenuPlugin plugin, File file, int inventorySize, List<String> warnings) {
        super(warnings);
        this.plugin = plugin;
        this.file = file;
        this.inventorySize = inventorySize;
    }

    @Override
    public Button load(@NonNull YamlConfiguration configuration, @NonNull String path, Object... objects) throws InventoryException {

        String buttonType = "NONE";
        String buttonName = (String) objects[0];
        DefaultButtonValue defaultButtonValue = objects.length == 2 ? (DefaultButtonValue) objects[1] : new DefaultButtonValue(this.inventorySize, new HashMap<>(), this.file);

        ButtonManager buttonManager = this.plugin.getButtonManager();
        Optional<ButtonLoader> optional = buttonManager.getLoader(buttonType);

        if (optional.isEmpty()) {
            throw new InventoryButtonException("Impossible to find the type " + buttonType + " for the button " + path + " in inventory " + this.file.getAbsolutePath());
        }

        Loader<MenuItemStack> itemStackLoader = new MenuItemStackLoader(this.plugin.getInventoryManager());

        ButtonLoader loader = optional.get();
        Button button = loader.load(configuration, path, defaultButtonValue);
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

        List<String> slotsAsString = configuration.getStringList(path + "slots");
        List<Integer> slots = ButtonLoader.loadSlot(slotsAsString);
        if (slots.isEmpty()) slots = defaultButtonValue.getSlots();
        if (slots.isEmpty()) {
            slots.add(slot);
        }

        button.setSlots(slots);
        button.setPage(page);

        String playerHead = this.translateItem(configuration, path, buttonName);
        MenuItemStack itemStack = itemStackLoader.load(configuration, path, this.file);
        button.setItemStack(itemStack);
        button.setButtonName(buttonName);
        if (playerHead != null) button.setPlayerHead(playerHead);

        InventoryManager inventoryManager = this.plugin.getInventoryManager();
        button.setClickRequirements(this.loadClickRequirements(configuration, path, inventoryManager));

        ConfigurationSection viewRequirementSection = configuration.getConfigurationSection(path + "view_requirement");
        if (viewRequirementSection != null) {
            button.setViewRequirement(this.loadRequirement(inventoryManager, this.plugin.getCommandManager(), this.plugin, new ArrayList<>(), viewRequirementSection, Configuration.allClicksType, this.file));
        }

        button.setUpdated(configuration.getBoolean(path + "update", defaultButtonValue.isUpdate()));
        button.setPriority(configuration.getInt(path + "priority", 1));
        button.setSoundOption(new ZSoundOption(null, XSound.Category.MASTER.name(), null, 1f, 1f, true));

        List<String> permissions = configuration.getStringList(path + "permission");
        permissions = permissions.isEmpty() ? configuration.getStringList(path + "permissions") : permissions;
        if (permissions.isEmpty()) {
            String permission = configuration.getString(path + "permission", null);
            if (permission != null) {
                permissions.add(permission);
            }
        }
        List<PermissionPermissible> mappedPermissions = new ArrayList<>(permissions.size());
        for (String permissionValue : permissions) {
            mappedPermissions.add(new ZPermissionPermissible(permissionValue));
        }
        button.setPermissions(mappedPermissions);
        List<String> orPermissions = configuration.getStringList(path + "orPermission");
        List<String> resolvedOrPermissions = orPermissions.isEmpty() ? configuration.getStringList(path + "orPermissions") : orPermissions;
        List<PermissionPermissible> mappedOrPermissions = new ArrayList<>(resolvedOrPermissions.size());
        for (String permissionValue : resolvedOrPermissions) {
            mappedOrPermissions.add(new ZPermissionPermissible(permissionValue));
        }
        button.setOrPermissions(mappedOrPermissions);

        ButtonLoadEvent buttonLoadEvent = new ButtonLoadEvent(configuration, path, buttonManager, loader, button);
        if (Configuration.enableFastEvent) {
            inventoryManager.getFastEvents().forEach(event -> event.onButtonLoad(buttonLoadEvent));
        } else buttonLoadEvent.call();

        return button;
    }

    private List<Requirement> loadClickRequirements(YamlConfiguration configuration, String path, InventoryManager inventoryManager) {
        List<Requirement> requirements = new ArrayList<>();

        List<String> clickCommands = configuration.getStringList(path + "click_commands");
        if (!clickCommands.isEmpty()) {
            List<Action> actions = this.loadActions(inventoryManager, this.plugin.getCommandManager(), this.plugin, clickCommands, this.file);
            ConfigurationSection section = configuration.getConfigurationSection(path + "click_requirement");
            requirements.add(this.loadRequirement(inventoryManager, this.plugin.getCommandManager(), this.plugin, actions, section, Configuration.allClicksType, this.file));
            return requirements;
        }

        for (Map.Entry<String, ClickType> click : CLICKS.entrySet()) {
            List<String> commands = configuration.getStringList(path + click.getKey() + "_click_commands");
            if (commands.isEmpty()) continue;

            List<Action> actions = this.loadActions(inventoryManager, this.plugin.getCommandManager(), this.plugin, commands, this.file);
            ConfigurationSection section = configuration.getConfigurationSection(path + click.getKey() + "_click_requirement");
            requirements.add(this.loadRequirement(inventoryManager, this.plugin.getCommandManager(), this.plugin, actions, section, List.of(click.getValue()), this.file));
        }
        return requirements;
    }

    private String translateItem(YamlConfiguration configuration, String path, String buttonName) {
        String playerHead = null;
        String material = configuration.getString(path + "material");

        if (material != null) {
            String lowerMaterial = material.toLowerCase(Locale.ROOT);
            if (lowerMaterial.startsWith("head-")) {
                playerHead = material.substring("head-".length());
                configuration.set(path + "material", "PLAYER_HEAD");
            } else if (lowerMaterial.startsWith("texture-")) {
                String texture = "{\"textures\":{\"SKIN\":{\"url\":\"http://textures.minecraft.net/texture/" + material.substring("texture-".length()) + "\"}}}";
                configuration.set(path + "material", "basehead-" + Base64.getEncoder().encodeToString(texture.getBytes(StandardCharsets.UTF_8)));
            } else if (lowerMaterial.startsWith("placeholder-")) {
                configuration.set(path + "material", material.substring("placeholder-".length()));
            } else if (EQUIPMENT_MATERIALS.containsKey(lowerMaterial)) {
                configuration.set(path + "material", EQUIPMENT_MATERIALS.get(lowerMaterial));
            } else if (lowerMaterial.equals("water_bottle")) {
                configuration.set(path + "material", "POTION");
                configuration.set(path + "potion", "WATER");
            } else if (lowerMaterial.startsWith("stack-") || lowerMaterial.startsWith("simpleitemgenerator-")) {
                this.warn(this.file, "the material \"" + material + "\" of the item " + buttonName + " is not supported");
            } else {
                for (Map.Entry<String, String> prefix : MATERIAL_PREFIXES.entrySet()) {
                    if (lowerMaterial.startsWith(prefix.getKey())) {
                        configuration.set(path + "material", prefix.getValue() + material.substring(prefix.getKey().length()));
                        break;
                    }
                }
            }
        }

        List<String> enchantments = configuration.getStringList(path + "enchantments");
        if (!enchantments.isEmpty()) {
            configuration.set(path + "enchantments", enchantments.stream().map(enchantment -> enchantment.replace(';', ',')).toList());
        }

        if (configuration.contains(path + "dynamic_amount")) {
            configuration.set(path + "amount", configuration.getString(path + "dynamic_amount"));
        }

        if (configuration.contains(path + "data") && !configuration.contains(path + "damage")) {
            configuration.set(path + "damage", configuration.get(path + "data"));
            configuration.set(path + "data", null);
        }

        ITEM_KEY_ALIASES.forEach((deluxeMenusKey, zMenuKey) -> {
            if (configuration.contains(path + deluxeMenusKey) && !configuration.contains(path + zMenuKey)) {
                configuration.set(path + zMenuKey, configuration.get(path + deluxeMenusKey));
            }
        });

        for (String key : UNSUPPORTED_ITEM_KEYS) {
            if (configuration.contains(path + key)) {
                this.warn(this.file, "the option " + key + " of the item " + buttonName + " is not converted");
            }
        }

        return playerHead;
    }
}
