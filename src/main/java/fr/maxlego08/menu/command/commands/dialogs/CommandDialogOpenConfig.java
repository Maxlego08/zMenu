package fr.maxlego08.menu.command.commands.dialogs;

import com.mojang.brigadier.arguments.StringArgumentType;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.configuration.ConfigManagerInt;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandDialogOpenConfig extends SubCommand<ZMenuPlugin> {
    private final ConfigManagerInt configManager;

    public CommandDialogOpenConfig(ZMenuPlugin plugin) {
        super(plugin, "config");
        this.configManager = plugin.getDialogManager().getConfigManager();
        this.setPermission(Permission.ZMENU_OPEN_DIALOG_CONFIG.getPermission());
        this.addRequiredArgument(Commands.argument("plugin-name", StringArgumentType.string()).suggests((context, builder) -> {
            this.configManager.getRegisteredConfigs().stream().filter(configName -> configName.toLowerCase().startsWith(builder.getRemaining().toLowerCase())).forEach(builder::suggest);
            return builder.buildFuture();
        }));
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        String pluginName = commandDispatch.getArgument("plugin-name", String.class);
        Player targetPlayer = commandDispatch.getOptionalArgument("player", Player.class).orElse(commandDispatch.getPlayer());
        if (targetPlayer == null) {
            MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), "§cYou must be a player to open a config gui.");
            return CommandResultType.FAILURE;
        }
        if (!this.configManager.getRegisteredConfigs().contains(pluginName)) {
            MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), "§cThe config '" + pluginName + "' does not exist.");
            return CommandResultType.FAILURE;
        }

        this.configManager.openConfig(pluginName, targetPlayer);
        return CommandResultType.SUCCESS;
    }
}
