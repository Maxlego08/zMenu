package fr.maxlego08.menu.hooks.dialogs.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.utils.record.dialogs.ActionButtonRecord;
import fr.maxlego08.menu.hooks.dialogs.inventory.ZNoticeDialogInventory;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

public class NoticeDialogInventoryTypeLoader implements DialogInventoryTypeLoader<ZNoticeDialogInventory> {

    @Override
    public ZNoticeDialogInventory load(@NotNull MenuPlugin menuPlugin, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String name, @NotNull String externalTitle) {
        ActionButtonRecord actionButtonRecord = this.loadActionButtonRecord(menuPlugin, configuration, "notice", file);
        return new ZNoticeDialogInventory(menuPlugin, name, file.getName(), externalTitle, actionButtonRecord);
    }

}
