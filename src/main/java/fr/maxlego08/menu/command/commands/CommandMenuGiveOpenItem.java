package fr.maxlego08.menu.command.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.maxlego08.menu.common.utils.ZUtils;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;

public class CommandMenuGiveOpenItem extends SubCommand<ZMenuPlugin> {
    private final InventoryManager inventoryManager;

    public CommandMenuGiveOpenItem(ZMenuPlugin plugin) {
        super(plugin, "giveopenitem", "goi");
        this.inventoryManager = plugin.getInventoryManager();
        this.setPermission(Permission.ZMENU_GIVE_OPEN_ITEM.getPermission());

        this.addRequiredArgument(Commands.argument("inventory-name", StringArgumentType.string()).suggests((ctx, builder) -> {
            this.inventoryManager.getInventoryNames().stream().filter(entry -> entry.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase()))
                    .forEach(builder::suggest);
            return builder.buildFuture();
        }));
        this.addOptionalArgument(Commands.argument("player", ArgumentTypes.player()));
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        String inventoryName = commandDispatch.getArgument("inventory-name", String.class);
        Player player = commandDispatch.getOptionalArgument("player", Player.class).orElse(commandDispatch.getPlayer());
        if (player == null) {
            MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), commandDispatch.getSender() instanceof ConsoleCommandSender ? Message.INVENTORY_OPEN_ERROR_CONSOLE : Message.INVENTORY_OPEN_ERROR_PLAYER);
            return CommandResultType.SUCCESS;
        }

        InventoryManager inventoryManager = commandDispatch.getPlugin().getInventoryManager();
        Optional<Inventory> optional = this.inventoryManager.findInventory(inventoryName);

        if (optional.isEmpty()) {
            MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.INVENTORY_OPEN_ERROR_INVENTORY, "%name%", inventoryName);
            return CommandResultType.SUCCESS;
        }

        Inventory inventory = optional.get();
        if (inventory.getOpenWithItem() == null) {
            MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.INVENTORY_OPEN_ITEM_ERROR, "%name%", inventoryName);
            return CommandResultType.SUCCESS;
        } else {
            ItemStack itemStack = inventory.getOpenWithItem().getItemStack().build(player);
            ZUtils.give(player, itemStack);
            MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.INVENTORY_OPEN_ITEM_SUCCESS, "%name%", player.getName());
        }

        return CommandResultType.SUCCESS;
    }
}
