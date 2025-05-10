package fr.maxlego08.menu.api.engine;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.button.Button;

import java.util.List;

public interface InventoryEngine extends BaseInventory {

    List<Inventory> getOldInventories();

    List<Button> getButtons();

    void buildButton(Button button);

    void displayButton(Button button);

    void displayFinalButton(Button button, int... slots);

    Inventory getMenuInventory();

    int getMaxPage();

    void setMaxPage(int maxPage);
}
