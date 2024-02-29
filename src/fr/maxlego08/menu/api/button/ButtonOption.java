package fr.maxlego08.menu.api.button;

import fr.maxlego08.menu.MenuItemStack;
import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.zcore.utils.loader.Loader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;

public interface ButtonOption {

    String getName();

    Plugin getPlugin();

    void loadButton(Button button, YamlConfiguration configuration, String path, InventoryManager inventoryManager, ButtonManager buttonManager, Loader<MenuItemStack> itemStackLoader, File file);

    void onClick(Button button, Player player, InventoryClickEvent event, InventoryDefault inventory, int slot, boolean isSuccess);
}
