package fr.maxlego08.menu.command.commands.reload;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.save.Config;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuReloadConfig extends VCommand {

    public CommandMenuReloadConfig(MenuPlugin plugin) {
        super(plugin);
        this.addSubCommand("config");
        this.setPermission(Permission.ZMENU_RELOAD);
    }

    @Override
    protected CommandType perform(MenuPlugin plugin) {

        plugin.getMessageLoader().load();
        Config.getInstance().load(plugin.getPersist());

        plugin.getPatternManager().loadPatterns();

        message(this.sender, Message.RELOAD_FILES);

        return CommandType.SUCCESS;
    }

}
