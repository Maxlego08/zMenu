package fr.maxlego08.menu.command.commands.players;

import com.mojang.brigadier.arguments.StringArgumentType;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;

public class CommandMenuPlayersRemoveAll extends SubCommand<ZMenuPlugin> {

    public CommandMenuPlayersRemoveAll(ZMenuPlugin plugin) {
        super(plugin, "removeall");
        this.setPermission(Permission.ZMENU_PLAYERS_REMOVE_ALL.getPermission());
        this.addRequiredArgument(Commands.argument("key", StringArgumentType.string()).suggests((ctx, builder) -> {
            this.plugin.getDataManager().getKeys().stream().filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                    .forEach(builder::suggest);
            return builder.buildFuture();
        }));
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        DataManager dataManager = commandDispatch.getPlugin().getDataManager();
        String key = commandDispatch.getArgument("key", String.class);
        dataManager.clearKey(key);
        MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.PLAYERS_DATA_REMOVE_ALL_SUCCESS, "%key%", key);
        return CommandResultType.SUCCESS;
    }
}
