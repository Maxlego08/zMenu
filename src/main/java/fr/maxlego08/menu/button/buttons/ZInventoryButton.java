package fr.maxlego08.menu.button.buttons;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.buttons.InventoryButton;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.zcore.utils.InventoryArgument;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.Optional;

public class ZInventoryButton extends InventoryButton {

    private final InventoryManager inventoryManager;
    private final String inventoryName;
    private final String pluginName;
    private final InventoryArgument inventoryArgument;
    private final int toPage;

    public ZInventoryButton(InventoryManager inventoryManager, CommandManager commandManager, String inventoryName, String pluginName, List<String> arguments, int toPage) {
        super();
        this.inventoryManager = inventoryManager;
        this.inventoryName = inventoryName;
        this.pluginName = pluginName;
        this.inventoryArgument = new InventoryArgument(commandManager, arguments);
        this.toPage = toPage;
    }

    @Override
    public String getInventory() {
        return this.inventoryName;
    }

    @Override
    public List<String> getArguments() {
        return this.inventoryArgument.getArguments();
    }

    @Override
    public int getToPage() {
        return this.toPage;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryEngine inventory, int slot, Placeholders placeholders) {
        super.onClick(player, event, inventory, slot, placeholders);

        Inventory fromInventory = inventory.getMenuInventory();
        List<Inventory> oldInventories = inventory.getOldInventories();

        Optional<Inventory> optional = this.pluginName == null ? this.inventoryManager.getInventory(this.inventoryName) : this.inventoryManager.getInventory(this.pluginName, this.inventoryName);
        if (!optional.isPresent()) {
            player.closeInventory();
            inventory.getPlugin().getInventoryManager().sendMessage(player, Message.INVENTORY_NOT_FOUND, "%name%", fromInventory.getFileName(), "%toName%", this.inventoryName, "%plugin%", this.pluginName == null ? "zMenu" : this.pluginName);
            return;
        }

        oldInventories.add(fromInventory);

        this.inventoryArgument.process(player);

        Inventory toInventory = optional.get();
        this.inventoryManager.openInventory(player, toInventory, this.toPage, oldInventories);
    }

}
