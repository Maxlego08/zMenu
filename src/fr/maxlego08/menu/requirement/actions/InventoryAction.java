package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.InventoryArgument;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

public class InventoryAction extends Action {

    private final InventoryManager inventoryManager;
    private final String inventory;
    private final String plugin;
    private String stringPage;
    private int intPage;
    private final InventoryArgument inventoryArgument;

    public InventoryAction(InventoryManager inventoryManager, CommandManager commandManager, String inventory, String plugin, String page, List<String> arguments) {
        this.inventoryManager = inventoryManager;
        this.inventory = inventory;
        this.plugin = plugin;
        if (page.contains("%")) {
            this.stringPage = page;
        } else {
            this.intPage = getInt(page);
        }
        this.inventoryArgument = new InventoryArgument(commandManager, arguments);
    }

    @Override
    protected void execute(Player player, Button button, InventoryDefault inventory, Placeholders placeholders) {

        inventory.getPlugin().getScheduler().runTask(null, () -> {
            this.inventoryArgument.process(player);

            Optional<Inventory> optional = this.inventoryManager.getInventory(this.plugin, this.papi(placeholders.parse(this.inventory), player, false));
            if (optional.isPresent()) {
                int page = this.stringPage == null
                        ? this.intPage
                        : getInt(this.papi(placeholders.parse(this.stringPage), player, false));

                this.inventoryManager.openInventory(player, optional.get(), page);
            } else Logger.info("Unable to find the inventory " + inventory, Logger.LogType.WARNING);
        });
    }
    private int getInt(String value){
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return 1;
        }
    }
}
