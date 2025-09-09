package fr.maxlego08.menu.command.commands.bedrock;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.BedrockManager;
import fr.maxlego08.menu.api.DialogManager;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandBedrockReload extends VCommand {
    public CommandBedrockReload(ZMenuPlugin plugin) {
        super(plugin);
        this.addSubCommand("reload","rl");
        this.setPermission(Permission.ZMENU_RELOAD_BEDROCK);
        this.setDescription(Message.DESCRIPTION_BEDROCK_RELOAD);

    }
    @Override
    protected CommandType perform(ZMenuPlugin plugin){
        BedrockManager bedrockManager = plugin.getBedrockManager();
        bedrockManager.reloadBedrockInventory();

        message(plugin, this.sender, Message.RELOAD_BEDROCK,"%bedrock%", bedrockManager.getBedrockInventory().size());
        return CommandType.SUCCESS;
    }
}
