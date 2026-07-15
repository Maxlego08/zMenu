package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.ItemManager;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.maxlego08.menu.common.utils.command.NonSpaceStringArgumentType;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CommandMenuGiveItem extends SubCommand<ZMenuPlugin> {
    private final ItemManager itemManager;

    public CommandMenuGiveItem(ZMenuPlugin plugin) {
        super(plugin, "giveitem");
        this.itemManager = plugin.getItemManager();
        this.setPermission(Permission.ZMENU_GIVE_ITEM.getPermission());
        this.addRequiredArgument(Commands.argument("item-id", new NonSpaceStringArgumentType()).suggests((context, builder) -> {
            this.itemManager.getItemIds().stream().filter(id -> id.toLowerCase().startsWith(builder.getRemainingLowerCase())).forEach(builder::suggest);
            return builder.buildFuture();
        }));
        this.addOptionalArgument("player", ArgumentTypes.player());

    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        String itemId = commandDispatch.getArgument("item-id", String.class);
        Player target;
        Optional<PlayerSelectorArgumentResolver> optionalPlayerSelector = commandDispatch.getOptionalArgument("player", PlayerSelectorArgumentResolver.class);
        if (optionalPlayerSelector.isPresent()) {
            PlayerSelectorArgumentResolver playerSelector = optionalPlayerSelector.get();
            try {
                target = playerSelector.resolve(commandDispatch.getSource()).getFirst();
            } catch (Exception e) {
                if (Configuration.enableDebug) {
                    Logger.info("Error while resolving player selector: " + e.getMessage());
                }
                return CommandResultType.SUCCESS;
            }
        } else {
            target = commandDispatch.getPlayer();
        }
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
