package fr.maxlego08.menu.api.engine;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.button.Button;

import java.util.List;

public interface InventoryEngine extends BaseInventory {

    List<Inventory> getOldInventories();

    List<Button> getButtons();
}
