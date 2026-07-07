package fr.maxlego08.menu.command.commands.dialogs;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.configuration.ConfigManagerInt;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandDialogOpenConfig extends VCommand {
    public CommandDialogOpenConfig(ZMenuPlugin plugin) {
        super(plugin);
        this.addSubCommand("config");
        ConfigManagerInt configManager = plugin.getDialogManager().getConfigManager();
        this.addRequireArg("plugin name",(a,b)-> configManager.getRegisteredConfigs());
        this.addOptionalArg("player");
        this.setPermission(Permission.ZMENU_DIALOG_CONFIG);
        this.setDescription(Message.DESCRIPTION_DIALOGS_CONFIG);
    }
    @Override
    protected CommandType perform(ZMenuPlugin plugin){
        ConfigManagerInt configManager = plugin.getDialogManager().getConfigManager();
        String pluginName = this.argAsString(0);
        Player targetPlayer = this.argAsPlayer(1,this.player);
        if (targetPlayer == null){
            this.message(plugin, this.sender, this.sender instanceof ConsoleCommandSender ? "§cYou must be a player to open a config gui." : "§cUnable to find the player.");
            return CommandType.DEFAULT;
        }
        if (!configManager.getRegisteredConfigs().contains(pluginName)){
            this.message(plugin, this.sender, "§cThe config '"+pluginName+"' does not exist.");
            return CommandType.DEFAULT;
        }
        configManager.openConfig(pluginName, targetPlayer);

        return CommandType.SUCCESS;
    }
}
