package fr.maxlego08.menu.command.commands.reload;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.pattern.PatternManager;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuReloadConfig extends VCommand {

    public CommandMenuReloadConfig(ZMenuPlugin plugin) {
        super(plugin);
        this.addSubCommand("config");
        this.setPermission(Permission.ZMENU_RELOAD);
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        plugin.getMessageLoader().load();
        plugin.reloadConfig();
        Configuration config = Configuration.getInstance();
        config.save(plugin.getConfig(),plugin.getConfigFile());
        config.load(plugin.getConfig());

        PatternManager patternManager = plugin.getPatternManager();
        patternManager.loadActionsPatterns();
        patternManager.loadPatterns();

        message(plugin, this.sender, Message.RELOAD_FILES);

        return CommandType.SUCCESS;
    }

}
