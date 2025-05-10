package fr.maxlego08.menu.command.commands.website;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

import java.util.Arrays;

public class CommandMenuDownload extends VCommand {

    public CommandMenuDownload(ZMenuPlugin plugin) {
        super(plugin);
        this.setDescription(Message.DESCRIPTION_DOWNLOAD);
        this.addSubCommand("download", "dl");
        this.setPermission(Permission.ZMENU_DOWNLOAD);
        this.addRequireArg("link");
        this.addOptionalArg("force", (a, b) -> Arrays.asList("true", "false"));
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        String link = this.argAsString(0);
        boolean force = this.argAsBoolean(1, false);

        if (Config.enableDownloadCommand) {

            plugin.getLogger().warning(sender.getName() + " try to download the link '" + link + "' (force: " + force + ") while the command is disable !");
            message(plugin, sender, "&cThe command is disable for safety reason, you need to enable it in config.json.");

            return CommandType.DEFAULT;
        }

        /*DownloadFile downloadFile = new DownloadFile();
        runAsync(plugin, () -> downloadFile.download(plugin, this.sender, link));*/
        plugin.getWebsiteManager().downloadFromUrl(this.sender, link, force);

        return CommandType.SUCCESS;
    }

}
