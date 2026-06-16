package fr.maxlego08.menu.command.commands.bedrock;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.BedrockManager;
import fr.maxlego08.menu.api.inventory.bedrock.BedrockInventory;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public class CommandBedrockOpen extends VCommand {
    public CommandBedrockOpen(ZMenuPlugin plugin) {
        super(plugin);
        this.addSubCommand("open", "o");
        BedrockManager dialogManager = plugin.getBedrockManager();
        this.addRequireArg("bedrock name", (a,b)-> dialogManager.getBedrockInventory().stream().map(e-> (e.getPlugin().getName()+":"+e.getFileName().toLowerCase(Locale.ROOT))).toList());

        this.addOptionalArg("player");
        this.addOptionalArg("display message", (a,b)-> Arrays.asList("true", "false"));

        this.setPermission(Permission.ZMENU_OPEN_BEDROCK);
        this.setDescription(Message.DESCRIPTION_BEDROCK_OPEN);

    }
    @Override
    protected CommandType perform(ZMenuPlugin plugin) {
        BedrockManager bedrockManager = plugin.getBedrockManager();
        String dialogName = this.argAsString(0);
        Player targetPlayer = this.argAsPlayer(1,this.player);
        boolean displayMessage = this.argAsBoolean(1, true);
        if (targetPlayer == null){
            this.message(plugin, this.sender, this.sender instanceof ConsoleCommandSender ? Message.BEDROCK_OPEN_ERROR_CONSOLE : Message.INVENTORY_OPEN_ERROR_PLAYER);
            return CommandType.DEFAULT;
        }
        Optional<BedrockInventory> optional = bedrockManager.getBedrockInventory(dialogName);

        if (optional.isEmpty()) {
            this.message(plugin, this.sender, Message.BEDROCK_OPEN_ERROR_NOT_FOUND,"%name%", dialogName);
            return CommandType.DEFAULT;
        }

        if (displayMessage) {
            if (this.sender == targetPlayer) {
                this.message(plugin, this.sender, Message.BEDROCK_OPEN_SUCCESS, "%name%", dialogName);
            } else {
                this.message(plugin, this.sender, Message.BEDROCK_OPEN_SUCCESS_OTHER, "%name%", dialogName, "%player%", targetPlayer.getName());
            }
        }

        BedrockInventory bedrockInventory = optional.get();
        bedrockManager.openBedrockInventory(targetPlayer, bedrockInventory);


        return CommandType.SUCCESS;
    }
}
