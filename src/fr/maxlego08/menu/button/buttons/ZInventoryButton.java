package fr.maxlego08.menu.button.buttons;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.buttons.InventoryButton;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.zcore.enums.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.Optional;

public class ZInventoryButton extends ZButton implements InventoryButton {

    private final InventoryManager inventoryManager;
    private final CommandManager commandManager;
    private final String inventoryName;
    private final String pluginName;
    private final List<String> arguments;
    private final int toPage;

    public ZInventoryButton(InventoryManager inventoryManager, CommandManager commandManager, String inventoryName, String pluginName, List<String> arguments, int toPage) {
        super();
        this.inventoryManager = inventoryManager;
        this.commandManager = commandManager;
        this.inventoryName = inventoryName;
        this.pluginName = pluginName;
        this.arguments = arguments;
        this.toPage = toPage;
    }

    @Override
    public String getInventory() {
        return this.inventoryName;
    }

    @Override
    public List<String> getArguments() {
        return null;
    }

    @Override
    public int getToPage() {
        return 0;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot) {
        super.onClick(player, event, inventory, slot);

        Inventory fromInventory = inventory.getMenuInventory();
        List<Inventory> oldInventories = inventory.getOldInventories();
        oldInventories.add(fromInventory);

        Optional<Inventory> optional = this.pluginName == null ? this.inventoryManager.getInventory(this.inventoryName) : this.inventoryManager.getInventory(this.pluginName, this.inventoryName);
        if (!optional.isPresent()) {
            player.closeInventory();
            message(player, Message.INVENTORY_NOT_FOUND, "%name%", fromInventory.getFileName(), "%toName%",
                    this.inventoryName, "%plugin%", this.pluginName == null ? "zMenu" : this.pluginName);
            return;
        }

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

        Inventory toInventory = optional.get();
        this.inventoryManager.openInventory(player, toInventory, this.toPage, oldInventories);
    }

}
