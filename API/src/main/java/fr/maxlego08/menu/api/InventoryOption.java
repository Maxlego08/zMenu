package fr.maxlego08.menu.api;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public interface InventoryOption {

    String getName();

    void loadInventory(Inventory inventory, File file, YamlConfiguration configuration, InventoryManager inventoryManager, ButtonManager buttonManager);

}
