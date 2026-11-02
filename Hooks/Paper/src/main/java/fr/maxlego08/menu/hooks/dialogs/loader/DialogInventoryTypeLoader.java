package fr.maxlego08.menu.hooks.dialogs.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.loader.DialogActionLoader;
import fr.maxlego08.menu.api.registry.Registries;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.record.dialogs.ActionButtonRecord;
import fr.maxlego08.menu.hooks.dialogs.inventory.AbstractDialogInventory;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
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

        String type = configuration.getString(path + ".type", "custom-click");
        DialogActionLoader dialogActionLoader = findActionLoader(plugin, type).orElseThrow(() -> new IllegalArgumentException("Dialog action loader not found for type: " + type + " in file: " + file.getName()));

        return new ActionButtonRecord(label, tooltip, width, dialogActionLoader.load(plugin, configuration, path, file));
    }

    /**
     * Finds the loader of a dialog action type, read like any registry key: {@code custom-click} is
     * {@code zmenu:custom_click}, and an addon's type is written with its namespace ({@code myaddon:type}).
     *
     * @param plugin The plugin.
     * @param type   The type, as written in the file.
     * @return The loader, or empty when the type is unknown or not a valid key.
     */
    static @NotNull Optional<DialogActionLoader> findActionLoader(@NotNull MenuPlugin plugin, @NotNull String type) {
        Key key;
        try {
            key = ZMenuKeys.parse(type);
        } catch (InvalidKeyException exception) {
            return Optional.empty();
        }
        return plugin.getInventoryManager().getRegistries().registry(Registries.DIALOG_ACTION).get(key);
    }
}
