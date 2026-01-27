package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerData;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class DataAction extends Action {

    private final ActionPlayerData playerData;
    private final MenuPlugin plugin;

    public DataAction(ActionPlayerData playerData, MenuPlugin plugin) {
        this.playerData = playerData;
        this.plugin = plugin;
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        this.playerData.execute(player, this.plugin.getDataManager(),placeholders);
    }
}
