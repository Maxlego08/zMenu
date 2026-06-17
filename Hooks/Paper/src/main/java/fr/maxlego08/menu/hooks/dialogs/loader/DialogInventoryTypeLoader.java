package fr.maxlego08.menu.hooks.dialogs.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.record.dialogs.ActionButtonRecord;
import fr.maxlego08.menu.hooks.dialogs.DialogActionLoaderRegistry;
import fr.maxlego08.menu.hooks.dialogs.inventory.AbstractDialogInventory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface DialogInventoryTypeLoader<T extends AbstractDialogInventory> {

    @Nullable
    T load(@NotNull MenuPlugin menuPlugin, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String name, @NotNull String externalTitle);

    static @NotNull List<Requirement> loadRequirements(@NotNull MenuPlugin menuPlugin, @NotNull YamlConfiguration configuration, @NotNull String path, @NotNull File file) {
        try {
            return menuPlugin.getButtonManager().loadRequirements(configuration, path, file);
        } catch (Exception e) {
            return List.of();
        }
    }

    default @NotNull ActionButtonRecord loadActionButtonRecord(
            @NotNull MenuPlugin plugin,
            @NotNull YamlConfiguration configuration,
            @NotNull String path,
            @NotNull File file
    ) {
        String label = configuration.getString(path + ".label", configuration.getString(path + ".text", configuration.getString(path + "-text", "")));
        String tooltip = configuration.getString(path + ".tooltip", configuration.getString(path + ".tooltip", configuration.getString(path + "-tooltip", "")));
        int width = configuration.getInt(path + ".width", configuration.getInt(path + ".width", configuration.getInt(path + "-width", 100)));

        DialogActionLoaderRegistry instance = DialogActionLoaderRegistry.getInstance();
        String type = configuration.getString(path + ".type", "custom-click");
        Optional<DialogActionLoader> dialogActionLoader = instance.get(type);
        if (dialogActionLoader.isEmpty()) {
            throw new IllegalArgumentException("Dialog action loader not found for type: " + type + " in file: " + file.getName());
        }

        return new ActionButtonRecord(label, tooltip, width, dialogActionLoader.get().load(plugin, configuration, path, file));
    }
}
