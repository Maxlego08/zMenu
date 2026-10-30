package fr.maxlego08.menu.command.commands.storage;

import com.mojang.brigadier.arguments.StringArgumentType;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.maxlego08.menu.storage.StorageMigration;
import fr.maxlego08.menu.storage.ZStorageManager;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@code /zm storage migrate <target>}: copies every zMenu data of the storage in use to another
 * one. It asks for a second run within 30 seconds, then copies in the background.
 */
public class CommandMenuStorageMigrate extends SubCommand<ZMenuPlugin> {

    private static final long CONFIRM_MILLIS = 30_000;
    private static final String[] TARGETS = {"SQLITE", "MYSQL", "MARIADB", "POSTGRESQL", "REDIS", "MONGODB"};

    private final Map<String, Long> confirmations = new ConcurrentHashMap<>();

    public CommandMenuStorageMigrate(ZMenuPlugin plugin) {
        super(plugin, "migrate");
        this.setPermission(Permission.ZMENU_STORAGE_MIGRATE.getPermission());
        this.addRequiredArgument(Commands.argument("target", StringArgumentType.word()).suggests((ctx, builder) -> {
            for (String target : TARGETS) {
                if (target.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase())) builder.suggest(target);
            }
            return builder.buildFuture();
        }));
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        CommandSender sender = commandDispatch.getSender();
        String target = commandDispatch.getArgument("target", String.class).toUpperCase(Locale.ROOT);
        String confirmationKey = sender.getName() + ":" + target;

        Long askedAt = this.confirmations.remove(confirmationKey);
        if (askedAt == null || System.currentTimeMillis() - askedAt > CONFIRM_MILLIS) {
            this.confirmations.put(confirmationKey, System.currentTimeMillis());
            MessageUtils.message(this.plugin, sender, Message.STORAGE_MIGRATE_CONFIRM, "%target%", target);
            return CommandResultType.SUCCESS;
        }

        MessageUtils.message(this.plugin, sender, Message.STORAGE_MIGRATE_START, "%target%", target);
        this.plugin.getScheduler().runAsync(task -> {
            try {
                StorageMigration.Result result = ((ZStorageManager) this.plugin.getStorageManager()).migrateTo(target,
                        step -> this.reply(sender, Message.STORAGE_MIGRATE_PROGRESS, "%step%", step));
                this.reply(sender, Message.STORAGE_MIGRATE_DONE, "%target%", target, "%variables%", result.variables(), "%inventories%", result.inventories(),
                        "%worlds%", result.worlds(), "%animations%", result.animations());
            } catch (Exception exception) {
                Logger.error("Unable to copy the zMenu data to " + target, exception);
                Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
                this.reply(sender, Message.STORAGE_MIGRATE_ERROR, "%target%", target, "%error%", String.valueOf(cause.getMessage()));
            }
        });
        return CommandResultType.SUCCESS;
    }

    private void reply(CommandSender sender, Message message, Object... args) {
        this.plugin.getScheduler().runNextTick(task -> MessageUtils.message(this.plugin, sender, message, args));
    }
}
