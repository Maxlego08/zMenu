package fr.maxlego08.menu.command.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.ItemManager;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandMenuGiveItem extends SubCommand<ZMenuPlugin> {
    private final ItemManager itemManager;

    public CommandMenuGiveItem(ZMenuPlugin plugin) {
        super(plugin, "giveitem");
        this.itemManager = plugin.getItemManager();
        this.setPermission(Permission.ZMENU_GIVE_ITEM.getPermission());
        this.addRequiredArgument(Commands.argument("item-id", StringArgumentType.string()).suggests((context, builder) -> {
            this.itemManager.getItemIds().stream().filter(id -> id.toLowerCase().startsWith(builder.getRemainingLowerCase())).forEach(builder::suggest);
            return builder.buildFuture();
        }));
        this.addOptionalArgument("player", ArgumentTypes.player());

    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        String itemId = commandDispatch.getArgument("item-id", String.class);
        Player target = commandDispatch.getOptionalArgument("player", Player.class).orElse(commandDispatch.getPlayer());
        if (target == null) {
            MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.COMMAND_PLAYER_NOT_FOUND, "%player%", commandDispatch.getArgument("player", String.class));
            return CommandResultType.FAILURE;
        }

        if (!this.itemManager.isCustomItem(itemId)) {
            MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.GIVE_ITEM_NOT_FOUND, "%itemId%", itemId);
            return CommandResultType.FAILURE;
        }

        this.itemManager.giveItem(target, itemId);
        if (target.equals(commandDispatch.getPlayer())) {
            MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.GIVE_ITEM_SUCCESS_SELF, "%itemId%", itemId);
        } else {
            MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.GIVE_ITEM_SUCCESS_OTHER, "%itemId%", itemId, "%player%", target.getName());
        }
        return CommandResultType.SUCCESS;
    }
}
