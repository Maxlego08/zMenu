package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.BedrockInventory;
import fr.maxlego08.menu.api.BedrockManager;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.common.utils.ActionHelper;
import fr.maxlego08.menu.zcore.utils.InventoryArgument;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;

public class BedrockAction extends ActionHelper {
    private final BedrockManager bedrockManager;
    private final String bedrockInventory;
    private final String plugin;
    private final InventoryArgument inventoryArgument;

    public BedrockAction(BedrockManager bedrockManager, CommandManager commandManager, String bedrockInventory, String plugin, List<String> arguments) {
        this.bedrockManager = bedrockManager;
        this.bedrockInventory = bedrockInventory;
        this.plugin = plugin;
        this.inventoryArgument = new InventoryArgument(commandManager, arguments);
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        inventory.getPlugin().getScheduler().runNextTick(w -> {

            Inventory fromInventory = inventory.getMenuInventory();
            List<Inventory> oldInventories = inventory.getOldInventories();

            String dialogName = this.papi(placeholders.parse(this.bedrockInventory), player);
            Optional<BedrockInventory> optional = this.plugin == null ? this.bedrockManager.getBedrockInventory(dialogName) : this.bedrockManager.getBedrockInventory(this.plugin, dialogName);
            if (optional.isPresent()) {
                oldInventories.add(fromInventory);

                this.inventoryArgument.process(player);
                this.bedrockManager.openBedrockInventory(player, optional.get(), oldInventories);
            } else {
                inventory.getPlugin().getInventoryManager().sendMessage(player, Message.INVENTORY_NOT_FOUND, "%name%", fromInventory.getFileName(), "%toName%", this.bedrockInventory, "%plugin%", this.plugin == null ? "zMenu" : this.plugin);
            }
        });
    }
}
