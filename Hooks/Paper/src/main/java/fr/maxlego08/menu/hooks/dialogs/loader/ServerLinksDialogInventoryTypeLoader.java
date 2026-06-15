package fr.maxlego08.menu.hooks.dialogs.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.dialogs.record.ActionButtonRecord;
import fr.maxlego08.menu.hooks.dialogs.inventory.ZServerLinksDialogInventory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.List;

public class ServerLinksDialogInventoryTypeLoader implements DialogInventoryTypeLoader<ZServerLinksDialogInventory> {
    @Override
    public @Nullable ZServerLinksDialogInventory load(@NotNull MenuPlugin menuPlugin, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String name, @NotNull String externalTitle) {
        ActionButtonRecord record = this.loadActionButtonRecord(menuPlugin, configuration, "server-links", file);
        int numberOfColumns = configuration.getInt("server-links.number-of-columns", 1);
        return new ZServerLinksDialogInventory(menuPlugin, name, file.getName(), externalTitle, record, numberOfColumns);
    }
}
