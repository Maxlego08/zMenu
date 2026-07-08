package fr.maxlego08.menu.command.commands.players;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.api.utils.OfflinePlayerCache;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.maxlego08.menu.common.utils.command.OfflinePlayerArgument;
import fr.maxlego08.menu.players.ZData;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class CommandMenuPlayersSubtract extends SubCommand<ZMenuPlugin> {

    public CommandMenuPlayersSubtract(ZMenuPlugin plugin) {
        super(plugin, "subtract", "sub");
        this.setPermission(Permission.ZMENU_PLAYERS_SUBTRACT.getPermission());
        this.addRequiredArgument("player", new OfflinePlayerArgument());
        this.addRequiredArgument(Commands.argument("key", StringArgumentType.string()).suggests((ctx, builder) -> {
            UUID targetId = ctx.getArgument("player", UUID.class);
            this.plugin.getDataManager().getKeys(targetId).stream().filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                    .forEach(builder::suggest);
            return builder.buildFuture();
        }));
        this.addRequiredArgument(Commands.argument("number", IntegerArgumentType.integer()).suggests((ctx, builder) -> {
            for (int i = 1; i <= 10; i++) {
                if (String.valueOf(i).startsWith(builder.getRemainingLowerCase())) {
                    builder.suggest(String.valueOf(i));
                }
            }
            return builder.buildFuture();
        }));
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        UUID targetId = commandDispatch.getArgument("player", UUID.class);
        String key = commandDispatch.getArgument("key", String.class);
        int value = commandDispatch.getArgument("number", Integer.class);

        DataManager dataManager = commandDispatch.getPlugin().getDataManager();
        Optional<Data> optional = dataManager.getData(targetId, key);
        if (optional.isEmpty()) {
            Data data = new ZData(key, -value, 0);
            dataManager.addData(targetId, data);
        } else {
            Data data = optional.get();
            data.remove(value);
            commandDispatch.getPlugin().getStorageManager().upsertData(targetId, data);
        }

        MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.PLAYERS_DATA_SUBTRACT, "%player%", OfflinePlayerCache.getName(targetId), "%key%", key);

        return CommandResultType.SUCCESS;
    }
}
