package fr.maxlego08.menu.command.commands.website;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.command.commands.*;
import fr.maxlego08.menu.command.commands.bedrock.CommandBedrock;
import fr.maxlego08.menu.command.commands.dialogs.CommandDialog;
import fr.maxlego08.menu.command.commands.players.CommandMenuPlayers;
import fr.maxlego08.menu.command.commands.reload.CommandMenuReload;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuWebsite extends VCommand {

    public CommandMenuWebsite(ZMenuPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZMENU_WEBSITE);
        this.setDescription(Message.DESCRIPTION_WEBSITE);
        this.addSubCommand("website", "w");

        this.addSubCommand(new CommandMenuDownload(plugin));
        this.addSubCommand(new CommandMenuLogin(plugin));
        this.addSubCommand(new CommandMenuConnect(plugin));
        this.addSubCommand(new CommandMenuUnlink(plugin));
        this.addSubCommand(new CommandMenuInventories(plugin));
        // this.addSubCommand(new CommandMenuMarketplace(plugin));
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {
        this.sendSyntax();
        return CommandType.SUCCESS;
    }

}
