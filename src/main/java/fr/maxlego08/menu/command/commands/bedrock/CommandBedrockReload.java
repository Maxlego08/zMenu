package fr.maxlego08.menu.command.commands.bedrock;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.BedrockManager;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.jetbrains.annotations.NotNull;

public class CommandBedrockReload extends SubCommand<ZMenuPlugin> {
    public CommandBedrockReload(ZMenuPlugin plugin) {
        super(plugin, "reload", "rl");
        this.setPermission(Permission.ZMENU_RELOAD_BEDROCK.getPermission());
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        BedrockManager bedrockManager = this.plugin.getBedrockManager();
        bedrockManager.reloadBedrockInventory();

        MessageUtils.message(this.plugin, commandDispatch.getSender(), Message.RELOAD_BEDROCK, "%bedrock%", bedrockManager.getBedrockInventory().size());
        return CommandResultType.SUCCESS;
    }
}
