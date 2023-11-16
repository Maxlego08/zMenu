package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerData;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.entity.Player;

public class DataAction extends Action {

    private final ActionPlayerData playerData;
    private final MenuPlugin plugin;

    public DataAction(ActionPlayerData playerData, MenuPlugin plugin) {
        this.playerData = playerData;
        this.plugin = plugin;
    }

    @Override
    protected void execute(Player player, Button button, InventoryDefault inventory) {
        this.playerData.execute(player, this.plugin.getDataManager());
    }
}
