package fr.maxlego08.menu.command.commands.website;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.api.website.WebsiteManager;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuLogin extends VCommand {

    public CommandMenuLogin(ZMenuPlugin plugin) {
        super(plugin);
        this.setDescription(Message.DESCRIPTION_LOGIN);
        this.addSubCommand("login");
        this.setPermission(Permission.ZMENU_LOGIN);
        this.addRequireArg("token");
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        String token = this.argAsString(0);

        WebsiteManager manager = plugin.getWebsiteManager();
        manager.login(this.sender, token);

        return CommandType.SUCCESS;
    }

}
