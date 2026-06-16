package fr.maxlego08.menu.hooks.dialogs.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.record.dialogs.ActionButtonRecord;
import fr.maxlego08.menu.hooks.dialogs.inventory.AbstractDialogInventory;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public interface DialogInventoryTypeLoader<T extends AbstractDialogInventory> {

    @Nullable
    T load(@NotNull MenuPlugin menuPlugin, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String name, @NotNull String externalTitle);

    default @NotNull List<Requirement> loadRequirements(@NotNull MenuPlugin menuPlugin, @NotNull YamlConfiguration configuration, @NotNull String path, @NotNull File file) {
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
        List<Requirement> actions = this.loadRequirements(plugin, configuration, path + ".actions", file);
        if (actions.isEmpty()) {
            String legacyPath = path.contains(".") ? path.substring(path.lastIndexOf(".") + 1) : path;
            actions = this.loadRequirements(plugin, configuration, legacyPath + "-actions", file);
        }
        int usageLimit = configuration.getInt(path + ".usage-limit", ClickCallback.UNLIMITED_USES);

        TemporalAmount actionDurationLimit = null;
        if (configuration.contains(path + ".duration-limit")) {
            if (configuration.isConfigurationSection(path + ".duration-limit")) {
                ConfigurationSection section = configuration.getConfigurationSection(path + ".duration-limit");
                if (section != null) {
                    long value = section.getLong("value", 0);
                    String typeStr = section.getString("type", "SECONDS").toUpperCase(Locale.ROOT);

                    TimeUnit timeUnit = TimeUnit.SECONDS;
                    try {
                        String enumName = typeStr.endsWith("S") ? typeStr : typeStr + "S";
                        timeUnit = TimeUnit.valueOf(enumName);
                    } catch (IllegalArgumentException ignored) {
                    }

                    actionDurationLimit = Duration.ofMillis(timeUnit.toMillis(value));
                }
            } else {
                actionDurationLimit = Duration.ofSeconds(configuration.getLong(path + ".duration-limit"));
            }
        }

        return new ActionButtonRecord(label, tooltip, width, actions, usageLimit, actionDurationLimit);
    }
}
