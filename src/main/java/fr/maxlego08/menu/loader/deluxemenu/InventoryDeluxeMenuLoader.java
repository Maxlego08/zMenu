package fr.maxlego08.menu.loader.deluxemenu;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.exceptions.InventorySizeException;
import fr.maxlego08.menu.api.exceptions.InventoryTypeException;
import fr.maxlego08.menu.api.inventory.ContainerInventory;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.inventory.setter.ContainerInventorySetter;
import fr.maxlego08.menu.inventory.zinv.ZInventory;
import fr.maxlego08.menu.loader.MenuItemStackLoader;
import fr.maxlego08.menu.loader.container.EmptyContainerInventoryTypeLoader;
import fr.maxlego08.menu.registry.InventoryTypeRegistry;
import fr.maxlego08.menu.requirement.ZRequirement;
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
        super();
        this.plugin = plugin;
    }

    @Override
    public Inventory load(@NonNull YamlConfiguration configuration, @NonNull String path, Object... objects) throws InventoryException {

        File file = (File) objects[0];
        String name = configuration.getString("name", configuration.getString("menu_title", configuration.getString("title")));
        name = name == null ? "" : name;

        InventoryType inventoryType;
        int size;
        String nameType = configuration.getString("inventory_type", "CHEST").toUpperCase(Locale.ROOT);
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

        List<Button> buttons = new ArrayList<>();
        Loader<Button> loader = new ButtonDeluxeMenuLoader(this.plugin, file, size);

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
                    e.printStackTrace();
                    inventory = InventoryTypeRegistry.getInstance().get(inventoryType).orElseGet(EmptyContainerInventoryTypeLoader::new).load(this.plugin, pluginOwner, name, fileName, size, buttons, configuration, path, file);
                }
            } else {
                inventory = InventoryTypeRegistry.getInstance().get(inventoryType).orElseGet(EmptyContainerInventoryTypeLoader::new).load(this.plugin, pluginOwner, name, fileName, size, buttons, configuration, path, file);
            }
        }



        inventory.setUpdateInterval(configuration.getInt(path + "update_interval", 1) * 1000);
        inventory.setClearInventory(false);
        inventory.setFile(file);
        inventory.setTargetPlayerNamePlaceholder("%player_name%");

        // Open requirement
        List<Action> actions = new ArrayList<>();
        List<Permissible> permissibles = new ArrayList<>();

        if (configuration.contains("open_commands")) {
            actions = this.loadActions(this.plugin.getInventoryManager(), this.plugin.getCommandManager(), this.plugin, configuration.getStringList("open_commands"));
        }

        if (configuration.contains("open_requirement") && configuration.isConfigurationSection("open_requirement")) {
            ConfigurationSection configurationSection = configuration.getConfigurationSection("open_requirement.requirements");
            if (configurationSection != null) {
                permissibles = this.loadPermissibles(this.plugin.getInventoryManager(), this.plugin.getCommandManager(), this.plugin, configurationSection);
            }
        }

        Requirement requirement = new ZRequirement(configuration.getInt("open_requirement.minimum_requirements", permissibles.size()), permissibles, new ArrayList<>(), actions, new ArrayList<>());
        inventory.setOpenRequirement(requirement);

        if (Configuration.enableDebug) {
            Logger.info("The inventory " + file.getPath() + " is a DeluxeMenus configuration! It is advisable to redo your configuration with zMenu!", Logger.LogType.WARNING);
        }

        return inventory;
    }

    @Override
    public void save(Inventory inventory, @NonNull YamlConfiguration configuration, @NonNull String path, File file, Object... objects) {
        MenuItemStackLoader itemStackLoader = new MenuItemStackLoader(this.plugin.getInventoryManager());

        configuration.set("name", inventory.getName());
        configuration.set("size", inventory.size());

        if (inventory instanceof ContainerInventory containerInventory && containerInventory.getFillItemStack() != null) {
            itemStackLoader.save(containerInventory.getFillItemStack(), configuration, "fillItem.", file);
        }

        // TODO: FINISH THE SAVE METHOD
    }
}
