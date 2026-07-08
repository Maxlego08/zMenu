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

import java.util.UUID;

public class CommandMenuPlayersSet extends SubCommand<ZMenuPlugin> {

    public CommandMenuPlayersSet(ZMenuPlugin plugin) {
        super(plugin, "set");
        this.setPermission(Permission.ZMENU_PLAYERS_SET.getPermission());

        this.addRequiredArgument(Commands.argument("player", new OfflinePlayerArgument()));
        this.addRequiredArgument(Commands.argument("key", StringArgumentType.string()).suggests((ctx, builder) -> {
            UUID targetId = ctx.getArgument("player", UUID.class);
            this.plugin.getDataManager().getKeys(targetId).stream().filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                    .forEach(builder::suggest);
            return builder.buildFuture();
        }));
        this.addRequiredArgument(Commands.argument("expire-after", IntegerArgumentType.integer(0)).suggests((ctx, builder) -> {
            builder.suggest("0").suggest("60").suggest("120").suggest("300").suggest("600").suggest("900").suggest("1800").suggest("3600");
            return builder.buildFuture();
        }));
        this.addRequiredArgument(Commands.argument("value", StringArgumentType.greedyString()   ));
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        UUID targetId = commandDispatch.getArgument("player", UUID.class);
        String key = commandDispatch.getArgument("key", String.class);
        int expireAfter = commandDispatch.getArgument("expire-after", Integer.class);
        String value = commandDispatch.getArgument("value", String.class);

        long expiredAt = expireAfter <= 0 ? 0 : System.currentTimeMillis() + (1000L * expireAfter);
        Data data = new ZData(key, value, expiredAt);

        DataManager dataManager = commandDispatch.getPlugin().getDataManager();
        dataManager.addData(targetId, data);

        MessageUtils.message(this.plugin, commandDispatch.getSender(), Message.PLAYERS_DATA_SET, "%player%", OfflinePlayerCache.getName(targetId), "%key%", key);
        return CommandResultType.SUCCESS;
    }
}
