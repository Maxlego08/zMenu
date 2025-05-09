package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerData;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import org.bukkit.entity.Player;

public class DataAction extends Action {

    private final ActionPlayerData playerData;
    private final ZMenuPlugin plugin;

    public DataAction(ActionPlayerData playerData, ZMenuPlugin plugin) {
        this.playerData = playerData;
        this.plugin = plugin;
    }

    @Override
    protected void execute(Player player, Button button, InventoryEngine inventory, Placeholders placeholders) {
        this.playerData.execute(player, this.plugin.getDataManager());
    }
}
