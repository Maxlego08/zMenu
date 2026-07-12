package fr.maxlego08.menu.command.commands.reload;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.maxlego08.menu.common.utils.command.NonSpaceStringArgumentType;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CommandMenuReloadInventory extends SubCommand<ZMenuPlugin> {

    public CommandMenuReloadInventory(ZMenuPlugin plugin) {
        super(plugin, "inventory", "inv");
        this.setPermission(Permission.ZMENU_RELOAD_INVENTORY.getPermission());

        this.addOptionalArgument(Commands.argument("menu", new NonSpaceStringArgumentType()).suggests((ctx, builder) -> {
            plugin.getInventoryManager().getInventoryNames().stream()
                    .filter(entry -> entry.startsWith(builder.getRemainingLowerCase()))
                    .forEach(builder::suggest);
            return builder.buildFuture();
        }));
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        Optional<String> optionalArgument = commandDispatch.getOptionalArgument("menu", String.class);
        InventoryManager inventoryManager = this.plugin.getInventoryManager();

        if (optionalArgument.isPresent()) {
            String inventoryName = optionalArgument.get();
            Optional<Inventory> optional = inventoryManager.findInventory(inventoryName);

            if (optional.isEmpty()) {
                MessageUtils.message(this.plugin, commandDispatch.getSender(), Message.INVENTORY_OPEN_ERROR_INVENTORY, "%name%", inventoryName);
                return CommandResultType.SUCCESS;
            }

            Inventory inventory = optional.get();
            this.plugin.getVInventoryManager().close(v -> {
                InventoryDefault inventoryDefault = (InventoryDefault) v;
                return !inventoryDefault.isClose() && inventoryDefault.getMenuInventory().equals(inventory);
            });
            inventoryManager.reloadInventory(inventory);
            MessageUtils.message(this.plugin, commandDispatch.getSender(), Message.RELOAD_INVENTORY_FILE, "%name%", inventoryName);

            return CommandResultType.SUCCESS;
        }

        this.plugin.getVInventoryManager().close();
        inventoryManager.deleteInventories(this.plugin);
        inventoryManager.loadInventories();
        MessageUtils.message(this.plugin, commandDispatch.getSender(), Message.RELOAD_INVENTORY, "%inventories%", inventoryManager.getInventories(this.plugin).size());
        return CommandResultType.SUCCESS;
    }
}
