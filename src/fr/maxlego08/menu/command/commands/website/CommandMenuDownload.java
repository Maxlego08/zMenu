package fr.maxlego08.menu.command.commands.website;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.website.DownloadFile;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuDownload extends VCommand {

	public CommandMenuDownload(MenuPlugin plugin) {
		super(plugin);
		this.setDescription(Message.DESCRIPTION_DOWNLOAD);
		this.addSubCommand("download", "dl");
		this.setPermission(Permission.ZMENU_DOWNLOAD);
		this.addRequireArg("link");
	}

	@Override
	protected CommandType perform(MenuPlugin plugin) {

		DownloadFile downloadFile = new DownloadFile();
		String link = this.argAsString(0);
		runAsync(plugin, () -> downloadFile.download(plugin, this.sender, link));

		return CommandType.SUCCESS;
	}

}
