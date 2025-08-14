package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.command.commands.dialogs.CommandDialog;
import fr.maxlego08.menu.command.commands.players.CommandMenuPlayers;
import fr.maxlego08.menu.command.commands.reload.CommandMenuReload;
import fr.maxlego08.menu.command.commands.website.CommandMenuDisconnect;
import fr.maxlego08.menu.command.commands.website.CommandMenuDownload;
import fr.maxlego08.menu.command.commands.website.CommandMenuInventories;
import fr.maxlego08.menu.command.commands.website.CommandMenuLogin;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenu extends VCommand {

    public CommandMenu(ZMenuPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZMENU_USE);
        this.addSubCommand(new CommandMenuOpen(plugin));
        this.addSubCommand(new CommandMenuList(plugin));
        this.addSubCommand(new CommandMenuCreate(plugin));
        this.addSubCommand(new CommandMenuReload(plugin));
        this.addSubCommand(new CommandMenuVersion(plugin));
        this.addSubCommand(new CommandMenuPlayers(plugin));
        this.addSubCommand(new CommandMenuOpenMainMenu(plugin));
        this.addSubCommand(new CommandMenuTestDupe(plugin));
        this.addSubCommand(new CommandMenuSave(plugin));
        this.addSubCommand(new CommandMenuGiveOpenItem(plugin));
        this.addSubCommand(new CommandMenuEditor(plugin));
        this.addSubCommand(new CommandMenuDocumentation(plugin));

        // Disable website connexion for beta
        this.addSubCommand(new CommandMenuDownload(plugin));
        this.addSubCommand(new CommandMenuLogin(plugin));
        this.addSubCommand(new CommandMenuDisconnect(plugin));
        this.addSubCommand(new CommandMenuInventories(plugin));
        // this.addSubCommand(new CommandMenuMarketplace(plugin));

        if (plugin.isPaper()) {
            this.addSubCommand(new CommandDialog(plugin));
        }
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {
        this.sender.sendMessage("§fInventory Builder/Marketplace§8: §ahttps://minecraft-inventory-builder.com/");
        sendSyntax();
        return CommandType.SUCCESS;
    }

}
