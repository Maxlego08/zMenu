package fr.maxlego08.menu.loader.deluxemenu;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.inventory.setter.ContainerInventorySetter;
import fr.maxlego08.menu.inventory.zinv.ZInventory;
import fr.maxlego08.menu.loader.container.EmptyContainerInventoryTypeLoader;
import fr.maxlego08.menu.registry.InventoryTypeRegistry;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.*;

public class InventoryDeluxeMenuLoader extends DeluxeMenuCommandUtils implements Loader<Inventory> {

    private final ZMenuPlugin plugin;

    public InventoryDeluxeMenuLoader(ZMenuPlugin plugin) {
        super(new ArrayList<>());
        this.plugin = plugin;
    }

    @Override
    public Inventory load(@NonNull YamlConfiguration configuration, @NonNull String path, Object... objects) throws InventoryException {

        File file = (File) objects[0];
        String name = this.loadTitle(configuration);

        InventoryType inventoryType;
        String nameType = configuration.getString("inventory_type", "CHEST").toUpperCase(Locale.ROOT);
        try {
            inventoryType = InventoryType.valueOf(nameType);
        } catch (IllegalArgumentException exception) {
            inventoryType = InventoryType.CHEST;
        }
        if (inventoryType == InventoryType.CRAFTING || inventoryType == InventoryType.PLAYER) inventoryType = InventoryType.CHEST;
        if (!inventoryType.name().equals(nameType)) this.warn(file, "the inventory type " + nameType + " is not valid, a chest is used");

        int size = inventoryType.getDefaultSize();
        if (inventoryType == InventoryType.CHEST) size = this.loadChestSize(configuration, file);

        List<Button> buttons = new ArrayList<>();
        Loader<Button> loader = new ButtonDeluxeMenuLoader(this.plugin, file, size, this.warnings);

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
            if (Configuration.enableDebug) {
                Logger.info("items section was not found in " + file.getAbsolutePath(), Logger.LogType.ERROR);
            }
        }

        // Sort buttons with priority id
        List<Button> copiedButtons = new ArrayList<>(buttons);
        for (Button button : copiedButtons) {

            if (button.getPriority() < 0) continue; // Le bouton n'a pas de priorité

            List<Button> sameButtons = new ArrayList<>();
            for (Button currentButton : buttons) {
                if (currentButton.getSlot() == button.getSlot() && currentButton.getPriority() >= 0) {
                    sameButtons.add(currentButton);
                }
            } // On va trier les boutons par slot et priorité
            if (sameButtons.size() < 2) continue; // Pas assez de bouton pour gérer la priorité

            buttons.removeAll(sameButtons); // On supprime les boutons de la liste par défaut

            sameButtons.sort(Comparator.comparingInt(Button::getPriority).reversed());
            Queue<Button> queue = new LinkedList<>(sameButtons);

            Button lastButton = queue.poll(); // On récupère le dernier bouton
            while (!queue.isEmpty()) {
                Button currentButton = queue.poll();
                currentButton.setElseButton(lastButton);
                lastButton.setParentButton(currentButton);
                lastButton = currentButton;
            }

            buttons.add(lastButton);
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



        int updateInterval = configuration.getInt(path + "update_interval", 10);
        inventory.setUpdateInterval((updateInterval <= 0 ? 10 : updateInterval) * 1000);
        inventory.setClearInventory(false);
        inventory.setFile(file);
        inventory.setTargetPlayerNamePlaceholder("%player_name%");

        InventoryManager inventoryManager = this.plugin.getInventoryManager();
        CommandManager commandManager = this.plugin.getCommandManager();
        inventory.setOpenActions(this.loadActions(inventoryManager, commandManager, this.plugin, configuration.getStringList("open_commands"), file));
        inventory.setCloseActions(this.loadActions(inventoryManager, commandManager, this.plugin, configuration.getStringList("close_commands"), file));

        ConfigurationSection openRequirementSection = configuration.getConfigurationSection("open_requirement");
        if (openRequirementSection != null) {
            inventory.setOpenRequirement(this.loadRequirement(inventoryManager, commandManager, this.plugin, new ArrayList<>(), openRequirementSection, Configuration.allClicksType, file));
        }

        if (configuration.getBoolean("refresh", false)) this.warn(file, "refresh is not converted, only the items with update: true are refreshed");
        if (configuration.contains("args")) this.warn(file, "the menu arguments (args) are not converted");
        if (configuration.getBoolean("enable_open_requirements_bypass_permissions", false)) this.warn(file, "enable_open_requirements_bypass_permissions is not converted");

        if (Configuration.enableDebug) {
            Logger.info("The inventory " + file.getPath() + " is a DeluxeMenus configuration! It is advisable to redo your configuration with zMenu!", Logger.LogType.WARNING);
        }

        return inventory;
    }

    private String loadTitle(YamlConfiguration configuration) {
        String key = configuration.contains("menu_title") ? "menu_title" : configuration.contains("name") ? "name" : "title";
        if (configuration.isList(key)) {
            List<String> lines = configuration.getStringList(key);
            return lines.isEmpty() ? "" : lines.getFirst();
        }
        String title = configuration.getString(key);
        return title == null ? "" : title;
    }


    private int loadChestSize(YamlConfiguration configuration, File file) {
        if (!configuration.contains("size")) return 54;
        int configuredSize = configuration.getInt("size");
        int size = configuredSize;
        if ((size + 1) % 9 == 0) size++;
        if ((size - 1) % 9 == 0) size--;
        if (size < 9) size = 9;
        if (size > 54) size = 54;
        if (size % 9 != 0) size = 54;
        if (size != configuredSize) this.warn(file, "the size " + configuredSize + " is not valid, " + size + " is used");
        return size;
    }
}
