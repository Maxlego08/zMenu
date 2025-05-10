package fr.maxlego08.menu.command.commands.website;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuInventories extends VCommand {

    public CommandMenuInventories(ZMenuPlugin plugin) {
        super(plugin);
        this.setDescription(Message.DESCRIPTION_INVENTORIES);
        this.addSubCommand("inventories");
        this.setPermission(Permission.ZMENU_INVENTORIES);
        this.setConsoleCanUse(false);
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        plugin.getWebsiteManager().fetchInventories(this.player);
        return CommandType.SUCCESS;
    }

}
