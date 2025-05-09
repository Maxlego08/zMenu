package fr.maxlego08.menu.command.commands.website;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuMarketplace extends VCommand {

    public CommandMenuMarketplace(ZMenuPlugin plugin) {
        super(plugin);
        this.setDescription(Message.DESCRIPTION_MARKETPLACE);
        this.addSubCommand("marketplace", "market", "shop");
        this.setPermission(Permission.ZMENU_MARKETPLACE);
        this.setConsoleCanUse(false);
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        plugin.getWebsiteManager().openMarketplace(this.player);
        return CommandType.SUCCESS;
    }

}
