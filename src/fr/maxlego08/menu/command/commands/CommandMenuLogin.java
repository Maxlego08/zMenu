package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.website.WebsiteManager;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuLogin extends VCommand {

	public CommandMenuLogin(MenuPlugin plugin) {
		super(plugin);
		this.setDescription(Message.DESCRIPTION_LOGIN);
		this.addSubCommand("login");
		this.setPermission(Permission.ZMENU_LOGIN);
		this.addRequireArg("email");
		this.addRequireArg("password");
	}

	@Override
	protected CommandType perform(MenuPlugin plugin) {

		String email = this.argAsString(0);
		String password = this.argAsString(1);
		
		WebsiteManager manager = plugin.getWebsiteManager();
		manager.login(this.sender, email, password);

		return CommandType.SUCCESS;
	}

}
