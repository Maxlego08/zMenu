package fr.maxlego08.menu.command.commands.dialogs;

import com.mojang.brigadier.arguments.BoolArgumentType;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.DialogManager;
import fr.maxlego08.menu.api.inventory.dialog.DialogInventory;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.maxlego08.menu.common.utils.command.NonSpaceStringArgumentType;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;

public class CommandDialogOpen extends SubCommand<ZMenuPlugin> {
    private final DialogManager dialogManager;

    public CommandDialogOpen(ZMenuPlugin plugin) {
        super(plugin, "open", "o");
        this.dialogManager = plugin.getDialogManager();
        this.setPermission(Permission.ZMENU_OPEN_DIALOG.getPermission());

        this.addRequiredArgument(Commands.argument("dialog-name", new NonSpaceStringArgumentType()).suggests(((context, builder) -> {
            this.dialogManager.getDialogNames().stream().filter(entry -> entry.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase()))
                    .forEach(builder::suggest);
            return builder.buildFuture();
        })));
        this.addOptionalArgument("player", ArgumentTypes.player());
        this.addOptionalArgument("display-message", BoolArgumentType.bool());
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        String dialogName = commandDispatch.getArgument("dialog-name", String.class);
        Player targetPlayer = commandDispatch.getOptionalArgument("player", Player.class).orElse(commandDispatch.getPlayer());
        boolean displayMessage = commandDispatch.getArgument("display-message", Boolean.class, true);

        if (targetPlayer == null) {
            MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), commandDispatch.getSender() instanceof ConsoleCommandSender ? Message.DIALOG_OPEN_ERROR_CONSOLE : Message.INVENTORY_OPEN_ERROR_PLAYER);
            return CommandResultType.SUCCESS;
        }

        Optional<DialogInventory> optional = this.dialogManager.getDialog(dialogName);

        if (optional.isEmpty()) {
            MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.DIALOG_OPEN_ERROR_NOT_FOUND, "%name%", dialogName);
            return CommandResultType.SUCCESS;
        }

        if (displayMessage) {
            if (commandDispatch.getSender() == targetPlayer) {
                MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.DIALOG_OPEN_SUCCESS, "%name%", dialogName);
            } else {
                MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.DIALOG_OPEN_SUCCESS_OTHER, "%name%", dialogName, "%player%", targetPlayer.getName());
            }
        }

        DialogInventory dialogInventory = optional.get();
        this.dialogManager.openDialog(targetPlayer, dialogInventory);

        return CommandResultType.SUCCESS;
    }
}
