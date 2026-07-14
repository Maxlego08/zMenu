package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.maxlego08.menu.zcore.enums.Addons;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.jetbrains.annotations.NotNull;

public class CommandAddons extends SubCommand<ZMenuPlugin> {

    public CommandAddons(ZMenuPlugin plugin) {
        super(plugin, "addons");
        this.setPermission(Permission.ZMENU_ADDONS.getPermission());
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.ADDONS_INFORMATION);
        for (Addons addon : Addons.values()) {
            MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), "<white> - <yellow>%pluginName%<white>: made by <red>%authorName% <click:open_url:'%url%'><green>%url%</green></click><white> (%price%)", "%pluginName%", addon.getPluginName(), "%authorName%", addon.getAuthorName(), "%url%", addon.getUrl(), "%price%", addon.getPrice());
        }
        return CommandResultType.SUCCESS;
    }
}
