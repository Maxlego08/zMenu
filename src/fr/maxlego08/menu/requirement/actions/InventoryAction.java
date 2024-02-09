package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

public class InventoryAction extends Action {

    private final InventoryManager inventoryManager;
    private final CommandManager commandManager;
    private final String inventory;
    private final String plugin;
    private final int page;
    private final List<String> arguments;

    public InventoryAction(InventoryManager inventoryManager, CommandManager commandManager, String inventory, String plugin, int page, List<String> arguments) {
        this.inventoryManager = inventoryManager;
        this.commandManager = commandManager;
        this.inventory = inventory;
        this.plugin = plugin;
        this.page = page;
        this.arguments = arguments;
    }

    @Override
    protected void execute(Player player, Button button, InventoryDefault inventory) {

        if (!this.arguments.isEmpty()) {

            for (int i = 0; i < this.arguments.size(); i++) {
                String name = String.valueOf(i - 4);
                String argument = this.arguments.get(i);

                if (argument.contains(":")) {
                    String[] values = argument.split(":", 2);
                    name = values[0];
                    argument = values[1];
                }

                this.commandManager.setPlayerArgument(player, name, argument);
            }

        }

        Optional<Inventory> optional = this.inventoryManager.getInventory(this.plugin, this.inventory);
        if (optional.isPresent()) {
            this.inventoryManager.openInventory(player, optional.get(), page);
        } else Logger.info("Unable to find the inventory " + inventory, Logger.LogType.WARNING);
    }

}
