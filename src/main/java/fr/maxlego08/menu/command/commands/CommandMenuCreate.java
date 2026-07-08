package fr.maxlego08.menu.command.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;

public class CommandMenuCreate extends SubCommand<ZMenuPlugin> {

    public CommandMenuCreate(ZMenuPlugin plugin) {
        super(plugin, "create");
        this.setPermission(Permission.ZMENU_CREATE.getPermission());

        this.addRequiredArgument("file-name", StringArgumentType.string());
        this.addRequiredArgument(Commands.argument("inventory-size", IntegerArgumentType.integer(9, 54)).suggests((context, builder) -> {
            builder.suggest("9");
            builder.suggest("18");
            builder.suggest("27");
            builder.suggest("36");
            builder.suggest("45");
            builder.suggest("54");
            return builder.buildFuture();
        }));

        this.addRequiredArgument("inventory-name", StringArgumentType.greedyString());

    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        String fileName = commandDispatch.getArgument("file-name", String.class);
        int inventorySize = commandDispatch.getArgument("inventory-size", Integer.class);
        String inventoryName = commandDispatch.getArgument("inventory-name", String.class);

        if (inventorySize % 9 != 0 || inventorySize < 9 || inventorySize > 54) {
            MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.INVENTORY_CREATE_ERROR_SIZE);
            return CommandResultType.FAILURE;
        }

        this.plugin.getInventoryManager().createNewInventory(commandDispatch.getSender(), fileName, inventorySize, inventoryName);

        return CommandResultType.SUCCESS;
    }
}
