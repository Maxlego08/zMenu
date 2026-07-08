package fr.maxlego08.menu.command.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CommandMenuSave extends SubCommand<ZMenuPlugin> {

    public CommandMenuSave(ZMenuPlugin plugin) {
        super(plugin, "save");
        this.setPermission(Permission.ZMENU_SAVE.getPermission());
        this.addRequiredArgument("item-name", StringArgumentType.string());
        this.addRequiredArgument(Commands.argument("type", StringArgumentType.string())
                .suggests((context, builder) -> builder.suggest("yml").suggest("base64").buildFuture()));
        this.setPlayerOnly();
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {

        InventoryManager inventoryManager = commandDispatch.getPlugin().getInventoryManager();
        String name = commandDispatch.getArgument("item-name", String.class);
        String type = commandDispatch.getArgument("type", String.class);

        ItemStack itemStack = commandDispatch.getPlayer().getItemInHand();
        if (itemStack.getType() == Material.AIR) {
            MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.SAVE_ERROR_EMPTY);
            return CommandResultType.SUCCESS;
        }

        inventoryManager.saveItem(commandDispatch.getSender(), itemStack, name, type);
        return CommandResultType.SUCCESS;
    }
}
