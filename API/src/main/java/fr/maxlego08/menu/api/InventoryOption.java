package fr.maxlego08.menu.api;

import fr.maxlego08.menu.inventory.VInventory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public interface InventoryOption {

    String getName();

    void loadInventory(Inventory inventory, File file, YamlConfiguration configuration, InventoryManager inventoryManager, ButtonManager buttonManager);

}
