package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
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

public class InventoryAction extends ActionHelper {

    private final InventoryManager inventoryManager;
    private final String inventory;
    private final String plugin;
    private final InventoryArgument inventoryArgument;
    private String stringPage;
    private int intPage;

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
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {

        inventory.getPlugin().getScheduler().runNextTick(w -> {

            Inventory fromInventory = inventory.getMenuInventory();
            List<Inventory> oldInventories = inventory.getOldInventories();

            String inventoryName = this.papi(placeholders.parse(this.inventory), player);
            Optional<Inventory> optional = this.plugin == null ? this.inventoryManager.getInventory(inventoryName) : this.inventoryManager.getInventory(this.plugin, inventoryName);
            if (optional.isPresent()) {

                int page = this.stringPage == null ? this.intPage : getInt(this.papi(placeholders.parse(this.stringPage), player));
                oldInventories.add(fromInventory);

                this.inventoryArgument.process(player);
                this.inventoryManager.openInventory(player, optional.get(), page, oldInventories);

            } else {
                inventory.getPlugin().getInventoryManager().sendMessage(player, Message.INVENTORY_NOT_FOUND, "%name%", fromInventory.getFileName(), "%toName%", this.inventory, "%plugin%", this.plugin == null ? "zMenu" : this.plugin);
            }
        });
    }

    private int getInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return 1;
        }
    }
}
