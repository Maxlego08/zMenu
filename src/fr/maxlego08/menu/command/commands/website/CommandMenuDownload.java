package fr.maxlego08.menu.command.commands.website;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

import java.util.Arrays;

public class CommandMenuDownload extends VCommand {

    public CommandMenuDownload(MenuPlugin plugin) {
        super(plugin);
        this.setDescription(Message.DESCRIPTION_DOWNLOAD);
        this.addSubCommand("download", "dl");
        this.setPermission(Permission.ZMENU_DOWNLOAD);
        this.addRequireArg("link");
        this.addOptionalArg("force", (a, b) -> Arrays.asList("true", "false"));
    }

    @Override
    protected CommandType perform(MenuPlugin plugin) {

        String link = this.argAsString(0);
        boolean force = this.argAsBoolean(1, false);

        /*DownloadFile downloadFile = new DownloadFile();
        runAsync(plugin, () -> downloadFile.download(plugin, this.sender, link));*/
        plugin.getWebsiteManager().downloadFromUrl(this.sender, link, force);

        return CommandType.SUCCESS;
    }

}
