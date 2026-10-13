package fr.maxlego08.menu.hooks.dialogs.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.utils.record.dialogs.ActionButtonRecord;
import fr.maxlego08.menu.api.utils.record.dialogs.DialogListBackButton;
import fr.maxlego08.menu.api.utils.record.dialogs.DialogListEntry;
import fr.maxlego08.menu.hooks.dialogs.inventory.ZDialogListDialogInventory;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DialogListDialogInventoryTypeLoader implements DialogInventoryTypeLoader<ZDialogListDialogInventory> {

    @Override
    public ZDialogListDialogInventory load(@NotNull MenuPlugin menuPlugin, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String name, @NotNull String externalTitle) {
        List<DialogListEntry> entries = this.loadEntries(configuration.getList("dialogs", List.of()), file);
        if (entries.isEmpty()) {
            Logger.info("The dialog list " + file.getName() + " has no dialogs, add them under 'dialogs'.", Logger.LogType.WARNING);
        }

        int numberOfColumns = Math.max(1, configuration.getInt("number-of-columns", 2));
        int buttonWidth = Math.clamp(configuration.getInt("button-width", 150), 1, 1024);

        ActionButtonRecord exitButton = null;
        if (configuration.isConfigurationSection("exit-button")) {
            exitButton = this.loadActionButtonRecord(menuPlugin, configuration, "exit-button", file);
        }

        DialogListBackButton defaults = DialogListBackButton.DEFAULT;
        DialogListBackButton backButton = new DialogListBackButton(
                configuration.getBoolean("back-button.enabled", defaults.enabled()),
                configuration.getString("back-button.label", defaults.label()),
                configuration.getString("back-button.tooltip", defaults.tooltip()),
                Math.clamp(configuration.getInt("back-button.width", defaults.width()), 1, 1024)
        );

        return new ZDialogListDialogInventory(menuPlugin, name, file.getName(), externalTitle, entries, numberOfColumns, buttonWidth, exitButton, backButton);
    }

    private List<DialogListEntry> loadEntries(List<?> values, File file) {
        List<DialogListEntry> entries = new ArrayList<>(values.size());
        for (Object value : values) {
            DialogListEntry entry = switch (value) {
                case String dialog when !dialog.isBlank() -> DialogListEntry.dialog(dialog);
                case Map<?, ?> map when map.get("dialog") instanceof String dialog -> DialogListEntry.dialog(dialog);
                case Map<?, ?> map when map.get("registry-key") instanceof String key -> NamespacedKey.fromString(key) != null ? DialogListEntry.registry(key) : null;
                case null, default -> null;
            };
            if (entry == null) {
                Logger.info("Invalid entry " + value + " in 'dialogs' of the dialog list " + file.getName() + ", expected a dialog name, 'dialog: <name>' or 'registry-key: <namespace:key>'.", Logger.LogType.WARNING);
                continue;
            }
            entries.add(entry);
        }
        return entries;
    }
}
