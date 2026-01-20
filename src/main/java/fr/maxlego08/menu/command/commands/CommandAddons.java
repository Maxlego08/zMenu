package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.zcore.enums.Addons;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandAddons extends VCommand {
    public CommandAddons(ZMenuPlugin plugin) {
        super(plugin);
        this.addSubCommand("addons");
        this.setPermission(Permission.ZMENU_ADDONS);
        this.setDescription(Message.DESCRIPTION_ADDONS);
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {
        message(plugin, this.sender, Message.ADDONS_INFORMATION);
        String messageType = plugin.isSpigot() ? "§f - §e%pluginName%§f: made by §c%authorName% §a%url%§f (%price%)" : "<white> - <yellow>%pluginName%<white>: made by <red>%authorName% <click:open_url:'%url%'><green>%url%</green></click><white> (%price%)";
        for (Addons addon : Addons.values()) {
            message(plugin, this.sender, messageType, "%pluginName%", addon.getPluginName(), "%authorName%", addon.getAuthorName(), "%url%", addon.getUrl(), "%price%", addon.getPrice());
        }
        return CommandType.SUCCESS;
    }
}
