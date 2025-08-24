package fr.maxlego08.menu.command.commands.dialogs;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.api.DialogManager;
import fr.maxlego08.menu.api.DialogInventory;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;

public class CommandDialogOpen extends VCommand {
    public CommandDialogOpen(ZMenuPlugin plugin) {
        super(plugin);
        this.addSubCommand("open", "o");
        DialogManager dialogManager = plugin.getDialogManager();
        this.addRequireArg("dialog name", (a,b)-> dialogManager.getDialogs().stream().map(e-> (e.getPlugin().getName()+":"+e.getFileName().toLowerCase())).toList());

        this.addOptionalArg("player");
        this.addOptionalArg("display message", (a,b)-> Arrays.asList("true", "false"));

        this.setPermission(Permission.ZMENU_OPEN_DIALOG);
        this.setDescription(Message.DESCRIPTION_DIALOGS_OPEN);

    }
    @Override
    protected CommandType perform(ZMenuPlugin plugin) {
        DialogManager dialogManager = plugin.getDialogManager();
        String dialogName = this.argAsString(0);
        Player targetPlayer = this.argAsPlayer(1,this.player);
        boolean displayMessage = this.argAsBoolean(1, true);
        if (targetPlayer == null){
            message(plugin, this.sender, sender instanceof ConsoleCommandSender ? Message.DIALOG_OPEN_ERROR_CONSOLE : Message.INVENTORY_OPEN_ERROR_PLAYER);
            return CommandType.DEFAULT;
        }
        Optional<DialogInventory> optional = dialogManager.getDialog(dialogName);

        if (optional.isEmpty()) {
            message(plugin, this.sender, Message.DIALOG_OPEN_ERROR_NOT_FOUND,"%name%", dialogName);
            return CommandType.DEFAULT;
        }

        if (displayMessage) {
            if (this.sender == targetPlayer) {
                message(plugin, this.sender, Message.DIALOG_OPEN_SUCCESS, "%name%", dialogName);
            } else {
                message(plugin, this.sender, Message.DIALOG_OPEN_SUCCESS_OTHER, "%name%", dialogName, "%player%", targetPlayer.getName());
            }
        }

        DialogInventory dialogInventory = optional.get();
        dialogManager.openDialog(targetPlayer, dialogInventory);


        return CommandType.SUCCESS;
    }
}
