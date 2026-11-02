package fr.maxlego08.menu.command.commands.storage;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.maxlego08.menu.storage.ZStorageManager;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.jetbrains.annotations.NotNull;

/**
 * {@code /zm storage status}: the storage in use, the server id, how changes reach the other
 * servers, and what is in memory.
 */
public class CommandMenuStorageStatus extends SubCommand<ZMenuPlugin> {

    public CommandMenuStorageStatus(ZMenuPlugin plugin) {
        super(plugin, "status");
        this.setPermission(Permission.ZMENU_STORAGE_STATUS.getPermission());
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        for (String line : ((ZStorageManager) this.plugin.getStorageManager()).status()) {
            MessageUtils.message(this.plugin, commandDispatch.getSender(), "§7" + line);
        }
        return CommandResultType.SUCCESS;
    }
}
