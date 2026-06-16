package fr.maxlego08.menu.hooks.dialogs.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.record.dialogs.ActionButtonRecord;
import fr.maxlego08.menu.hooks.dialogs.inventory.ZNoticeDialogInventory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class NoticeDialogInventoryTypeLoader implements DialogInventoryTypeLoader<ZNoticeDialogInventory> {

    @Override
    public ZNoticeDialogInventory load(@NotNull MenuPlugin menuPlugin, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String name, @NotNull String externalTitle) {
        ActionButtonRecord actionButtonRecord = this.loadActionButtonRecord(menuPlugin, configuration, "notice", file);
        if (actionButtonRecord.actions().isEmpty()) {
            List<Requirement> actions = this.loadRequirements(menuPlugin, configuration, "actions", file);
            actionButtonRecord = new ActionButtonRecord(actionButtonRecord.label(), actionButtonRecord.tooltip(), actionButtonRecord.width(), actions, actionButtonRecord.usageLimit(), actionButtonRecord.actionDurationLimit());
        }
        return new ZNoticeDialogInventory(menuPlugin, name, file.getName(), externalTitle, actionButtonRecord);
    }

}
