package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.hooks.dialogs.DialogManager;
import org.bukkit.entity.Player;

public class DialogAction extends ActionHelper {
    private final DialogManager dialogManager;
    private final String dialogName;
    private final String pluginName;

    public DialogAction(DialogManager dialogManager, String dialogName, String pluginName) {
        this.dialogManager = dialogManager;
        this.dialogName = dialogName;
        this.pluginName = pluginName;
    }

    @Override
    protected void execute(Player player, Button button, InventoryEngine inventory, Placeholders placeholders) {
        inventory.getPlugin().getScheduler().runNextTick(w->{
            dialogManager.openDialog(player, pluginName+ ":" + dialogName);
        });
    }
}
