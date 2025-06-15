package fr.maxlego08.menu.loader.deluxemenu;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.ZInventory;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.exceptions.InventorySizeException;
import fr.maxlego08.menu.loader.MenuItemStackLoader;
import fr.maxlego08.menu.requirement.ZRequirement;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.api.utils.Loader;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class InventoryDeluxeMenuLoader extends DeluxeMenuCommandUtils implements Loader<Inventory> {

    private final ZMenuPlugin plugin;

    public InventoryDeluxeMenuLoader(ZMenuPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public Inventory load(YamlConfiguration configuration, String path, Object... objects) throws InventoryException {

        File file = (File) objects[0];
        String name = configuration.getString("name", configuration.getString("menu_title", configuration.getString("title")));
        name = name == null ? "" : name;

        int size = configuration.getInt("size", 54);
        if (size % 9 != 0) {
            throw new InventorySizeException("Size " + size + " is not valid for inventory " + file.getAbsolutePath());
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
            Logger.info("items section was not found in " + file.getAbsolutePath(), Logger.LogType.ERROR);
        }

        // Sort buttons with priority id
        List<Button> copiedButtons = new ArrayList<>(buttons);
        for (Button button : copiedButtons) {

            if (button.getPriority() < 0) continue; // Le bouton n'a pas de priorité

            List<Button> sameButtons = buttons.stream().filter(currentButton -> currentButton.getSlot() == button.getSlot() && currentButton.getPriority() >= 0).collect(Collectors.toList()); // On va trier les boutons par slot et priorité
            if (sameButtons.size() < 2) continue; // Pas assez de bouton pour gérer la priorité

            buttons.removeAll(sameButtons); // On supprime les boutons de la liste par défaut

            sameButtons.sort(Comparator.comparingInt(Button::getPriority).reversed());
            Queue<Button> queue = new LinkedList<>(sameButtons);

            Button lastButton = queue.poll(); // On récupère le dernier bouton
            while (!queue.isEmpty()) {
                Button currentButton = queue.poll();
                ((Button) currentButton).setElseButton(lastButton);
                lastButton = currentButton;
            }

            buttons.add(lastButton);
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

        inventory.setUpdateInterval(configuration.getInt(path + "update_interval", 1) * 1000);
        inventory.setClearInventory(false);
        inventory.setFile(file);

        // Open requirement
        List<Action> actions = new ArrayList<>();
        List<Permissible> permissibles = new ArrayList<>();

        if (configuration.contains("open_commands")) {
            actions = loadActions(plugin.getInventoryManager(), plugin.getCommandManager(), plugin, configuration.getStringList("open_commands"));
        }

        if (configuration.contains("open_requirement") && configuration.isConfigurationSection("open_requirement")) {
            ConfigurationSection configurationSection = configuration.getConfigurationSection("open_requirement.requirements");
            if (configurationSection != null) {
                permissibles = loadPermissibles(plugin.getInventoryManager(), plugin.getCommandManager(), plugin, configurationSection);
            }
        }

        Requirement requirement = new ZRequirement(configuration.getInt("open_requirement.minimum_requirements", permissibles.size()), permissibles, new ArrayList<>(), actions, new ArrayList<>());
        inventory.setOpenRequirement(requirement);

        if (Config.enableDebug) {
            plugin.getLogger().warning("The inventory " + file.getPath() + " is a DeluxeMenus configuration! It is advisable to redo your configuration with zMenu!");
        }

        return inventory;
    }

    @Override
    public void save(Inventory object, YamlConfiguration configuration, String path, File file, Object... objects) {
        MenuItemStackLoader itemStackLoader = new MenuItemStackLoader(this.plugin.getInventoryManager());

        configuration.set("name", object.getName());
        configuration.set("size", object.size());

        if (object.getFillItemStack() != null) {
            itemStackLoader.save(object.getFillItemStack(), configuration, "fillItem.", file);
        }

        //TODO: FINISH THE SAVE METHOD
    }
}
