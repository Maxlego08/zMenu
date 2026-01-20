package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.DialogInventory;
import fr.maxlego08.menu.api.DialogManager;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.zcore.utils.InventoryArgument;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;

public class DialogAction extends ActionHelper {
    private final DialogManager dialogManager;
    private final String dialog;
    private final String plugin;
    private final InventoryArgument inventoryArgument;

    public DialogAction(DialogManager dialogManager, CommandManager commandManager, String dialog, String plugin, List<String> arguments) {
        this.dialogManager = dialogManager;
        this.dialog = dialog;
        this.plugin = plugin;
        this.inventoryArgument = new InventoryArgument(commandManager, arguments);
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        inventory.getPlugin().getScheduler().runNextTick(w -> {

            Inventory fromInventory = inventory.getMenuInventory();
            List<Inventory> oldInventories = inventory.getOldInventories();

            String dialogName = this.papi(placeholders.parse(this.dialog), player);
            Optional<DialogInventory> optional = this.plugin == null ? this.dialogManager.getDialog(dialogName) : this.dialogManager.getDialog(this.plugin, dialogName);
            if (optional.isPresent()) {
                //oldInventories.add(fromInventory); SOON

                this.inventoryArgument.process(player,placeholders);
                this.dialogManager.openDialog(player, optional.get());
            } else {
                inventory.getPlugin().getInventoryManager().sendMessage(player, Message.INVENTORY_NOT_FOUND, "%name%", fromInventory.getFileName(), "%toName%", this.dialog, "%plugin%", this.plugin == null ? "zMenu" : this.plugin);
            }
        });
    }
}
