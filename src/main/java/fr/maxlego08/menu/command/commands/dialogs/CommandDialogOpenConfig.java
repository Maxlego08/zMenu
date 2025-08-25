package fr.maxlego08.menu.command.commands.dialogs;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.configuration.ConfigManagerInt;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandDialogOpenConfig extends VCommand {
    public CommandDialogOpenConfig(ZMenuPlugin plugin) {
        super(plugin);
        this.addSubCommand("config");
        ConfigManagerInt configManager = plugin.getConfigManager();
        this.addRequireArg("plugin name",(a,b)-> configManager.getRegisteredConfigs());
        this.addOptionalArg("player");
    }
    @Override
    protected CommandType perform(ZMenuPlugin plugin){
        ConfigManagerInt configManager = plugin.getConfigManager();
        String pluginName = this.argAsString(0);
        Player targetPlayer = this.argAsPlayer(1,this.player);
        if (targetPlayer == null){
            message(plugin, this.sender, sender instanceof ConsoleCommandSender ? "§cYou must be a player to open a config gui." : "§cUnable to find the player.");
            return CommandType.DEFAULT;
        }
        if (!configManager.getRegisteredConfigs().contains(pluginName)){
            message(plugin, this.sender, "§cThe config '"+pluginName+"' does not exist.");
            return CommandType.DEFAULT;
        }
        configManager.openConfig(pluginName, targetPlayer);

        return CommandType.SUCCESS;
    }
}
