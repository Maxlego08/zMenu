package fr.maxlego08.menu.command.commands.website;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.jetbrains.annotations.NotNull;

public class CommandMenuDownload extends SubCommand<ZMenuPlugin> {

    public CommandMenuDownload(ZMenuPlugin plugin) {
        super(plugin, "download", "dl");
        this.setPermission(Permission.ZMENU_DOWNLOAD.getPermission());
        this.addRequiredArgument("link", StringArgumentType.string());
        this.addOptionalArgument("force", BoolArgumentType.bool());
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {

        String link = commandDispatch.getArgument("link", String.class);
        boolean force = commandDispatch.getArgument("force", Boolean.class, false);

        if (Configuration.enableDownloadCommand) {

            Logger.info(commandDispatch.getSender().getName() + " try to download the link '" + link + "' (force: " + force + ") while the command is disable !", Logger.LogType.WARNING);
            MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), "&cThe command is disable for safety reason, you need to enable it in config.json.");
            return CommandResultType.SUCCESS;
        }

        /*DownloadFile downloadFile = new DownloadFile();
        runAsync(plugin, () -> downloadFile.download(plugin, this.sender, link));*/
        this.plugin.getWebsiteManager().downloadFromUrl(commandDispatch.getSender(), link, force);
        return CommandResultType.SUCCESS;
    }
}
