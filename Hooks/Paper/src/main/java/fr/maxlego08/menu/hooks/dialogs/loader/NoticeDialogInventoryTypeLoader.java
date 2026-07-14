package fr.maxlego08.menu.hooks.dialogs.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.utils.record.dialogs.ActionButtonRecord;
import fr.maxlego08.menu.hooks.dialogs.inventory.ZNoticeDialogInventory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class NoticeDialogInventoryTypeLoader implements DialogInventoryTypeLoader<ZNoticeDialogInventory> {

    @Override
    public ZNoticeDialogInventory load(@NotNull MenuPlugin menuPlugin, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String name, @NotNull String externalTitle) {
        ActionButtonRecord actionButtonRecord = this.loadActionButtonRecord(menuPlugin, configuration, "notice", file);
        return new ZNoticeDialogInventory(menuPlugin, name, file.getName(), externalTitle, actionButtonRecord);
    }

}
