package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.entity.Player;

public class RefreshAction extends Action {

    @Override
    protected void execute(Player player, Button button, InventoryDefault inventory) {
        if (button != null) {
            inventory.buildButton(button.getMasterParentButton());
        }
    }
}
