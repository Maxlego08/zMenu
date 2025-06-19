package fr.maxlego08.menu.api;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public interface InventoryOption {

    /**
     * Returns the name of this option.
     * This name is used to identify the option in the configuration file.
     * The name is case-sensitive.
     *
     * @return The name of this option.
     */
    String getName();

    /**
     * Loads the given inventory from the given configuration file
     *
     * @param inventory           the inventory to load
     * @param file                the file that the configuration is loaded from
     * @param configuration       the configuration to load the inventory from
     * @param inventoryManager    the inventory manager to use for inventory operations
     * @param buttonManager       the button manager to use for button-related operations
     */
    void loadInventory(Inventory inventory, File file, YamlConfiguration configuration, InventoryManager inventoryManager, ButtonManager buttonManager);

}
