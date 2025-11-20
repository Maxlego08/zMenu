package fr.maxlego08.menu.command.commands.bedrock;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandBedrock extends VCommand {

    public CommandBedrock(ZMenuPlugin plugin) {
        super(plugin);
        this.addSubCommand("bedrock", "b");
        this.setDescription(Message.DESCRIPTION_BEDROCK);
        this.addSubCommand(new CommandBedrockOpen(plugin));
        this.addSubCommand(new CommandBedrockReload(plugin));
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {
        sendSyntax();
        return CommandType.SUCCESS;
    }
}
