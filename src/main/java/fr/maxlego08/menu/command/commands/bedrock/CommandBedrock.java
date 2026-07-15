package fr.maxlego08.menu.command.commands.bedrock;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.common.enums.Permission;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.jetbrains.annotations.NotNull;

public class CommandBedrock extends SubCommand<ZMenuPlugin> {

    public CommandBedrock(ZMenuPlugin plugin) {
        super(plugin, "bedrock", "b");
        this.setPermission(Permission.ZMENU_BEDROCK.getPermission());
        this.addSubCommand(new CommandBedrockOpen(plugin));
        this.addSubCommand(new CommandBedrockReload(plugin));
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        return CommandResultType.FAILURE;
    }
}
