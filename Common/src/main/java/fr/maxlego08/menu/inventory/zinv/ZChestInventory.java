package fr.maxlego08.menu.inventory.zinv;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.inventory.setter.ChestInventorySetter;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class ZChestInventory extends ZInventory implements ChestInventorySetter {
    /**
     * @param plugin   The plugin where the inventory comes from
     * @param name     Inventory name
     * @param fileName Inventory file name
     * @param size     Inventory size
     * @param buttons  List of {@link Button}
     */
    public ZChestInventory(Plugin plugin, String name, String fileName, int size, List<Button> buttons) {
        super(plugin, name, fileName, size, buttons);
    }


}
