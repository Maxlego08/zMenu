package fr.maxlego08.menu.hooks.dialogs.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.utils.record.dialogs.ActionButtonRecord;
import fr.maxlego08.menu.hooks.dialogs.inventory.ZConfirmationDialogInventory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class ConfirmationDialogInventoryTypeLoader implements DialogInventoryTypeLoader<ZConfirmationDialogInventory> {

    @Override
    public ZConfirmationDialogInventory load(@NotNull MenuPlugin menuPlugin, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String name, @NotNull String externalTitle) {
        ActionButtonRecord yesActionButtonRecord = this.loadActionButtonRecord(menuPlugin, configuration, "confirmation.yes", file);
        ActionButtonRecord noActionButtonRecord = this.loadActionButtonRecord(menuPlugin, configuration, "confirmation.no", file);
        return new ZConfirmationDialogInventory(menuPlugin, name, file.getName(), externalTitle, yesActionButtonRecord, noActionButtonRecord);
    }
}
