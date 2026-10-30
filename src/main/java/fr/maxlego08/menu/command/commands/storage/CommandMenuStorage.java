package fr.maxlego08.menu.command.commands.storage;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.common.enums.Permission;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.jetbrains.annotations.NotNull;

/**
 * {@code /zm storage <status|migrate>}.
 */
public class CommandMenuStorage extends SubCommand<ZMenuPlugin> {

    public CommandMenuStorage(ZMenuPlugin plugin) {
        super(plugin, "storage");
        this.setPermission(Permission.ZMENU_STORAGE.getPermission());
        this.addSubCommand(new CommandMenuStorageStatus(plugin));
        this.addSubCommand(new CommandMenuStorageMigrate(plugin));
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        return CommandResultType.SUCCESS;
    }
}
