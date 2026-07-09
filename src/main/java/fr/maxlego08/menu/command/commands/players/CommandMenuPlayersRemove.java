package fr.maxlego08.menu.command.commands.players;

import com.mojang.brigadier.arguments.StringArgumentType;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.players.PlayerData;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.robie.paperdispatch.argument.OfflinePlayerArgument;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class CommandMenuPlayersRemove extends SubCommand<ZMenuPlugin> {

    public CommandMenuPlayersRemove(ZMenuPlugin plugin) {
        super(plugin, "remove", "r");
        this.setPermission(Permission.ZMENU_PLAYERS_REMOVE.getPermission());
        this.addRequiredArgument("player", new OfflinePlayerArgument());
        this.addRequiredArgument(Commands.argument("key", StringArgumentType.string()).suggests((ctx, builder) -> {
            UUID targetId = ctx.getArgument("player", UUID.class);
            this.plugin.getDataManager().getKeys(targetId).stream().filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                    .forEach(builder::suggest);
            return builder.buildFuture();
        }));
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        DataManager dataManager = commandDispatch.getPlugin().getDataManager();

        UUID targetId = commandDispatch.getArgument("player", UUID.class);
        String key = commandDispatch.getArgument("key", String.class);

        Optional<PlayerData> optional = dataManager.getPlayer(targetId);
        if (optional.isEmpty()) {
            MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.PLAYERS_DATA_REMOVE_ERROR, "%key%", key);
            return CommandResultType.SUCCESS;
        }
        PlayerData playerData = optional.get();
        if (!playerData.containsKey(key)) {
            MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.PLAYERS_DATA_REMOVE_ERROR, "%key%", key);
            return CommandResultType.SUCCESS;
        }
        playerData.removeData(key);
        MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.PLAYERS_DATA_REMOVE_SUCCESS, "%key%", key, "%player%", commandDispatch.getArgument("player", OfflinePlayer.class).getName());
        return CommandResultType.SUCCESS;
    }
}
