package fr.maxlego08.menu.hooks.dialogs.loader.dialogaction;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.dialogs.ZDialogAction;
import fr.maxlego08.menu.hooks.dialogs.action.ZCustomClickDialogAction;
import fr.maxlego08.menu.hooks.dialogs.loader.DialogActionLoader;
import fr.maxlego08.menu.hooks.dialogs.loader.DialogInventoryTypeLoader;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ZCustomClickDialogActionLoader implements DialogActionLoader {

    @Override
    public @NotNull String getType() {
        return "custom-click";
    }

    @Override
    public @NotNull ZDialogAction load(@NotNull MenuPlugin plugin, @NotNull YamlConfiguration configuration, @NotNull String path, @NotNull File file) {
        List<Requirement> actions = DialogInventoryTypeLoader.loadRequirements(plugin, configuration, path + ".actions", file);
        if (actions.isEmpty()) {
            String legacyPath = path.contains(".") ? path.substring(path.lastIndexOf(".") + 1) : path;
            actions = DialogInventoryTypeLoader.loadRequirements(plugin, configuration, legacyPath + "-actions", file);
        }
        if (actions.isEmpty()) {
            actions = DialogInventoryTypeLoader.loadRequirements(plugin, configuration, "actions", file);
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
        Map<String, Boolean> enablePlaceholders = new HashMap<>();
        String configKey = path + ".enable-placeholders";
        if (configuration.contains(configKey)) {
            if (configuration.isBoolean(configKey)) {
                boolean global = configuration.getBoolean(configKey);
                enablePlaceholders.put("", global);
            } else if (configuration.isConfigurationSection(configKey)) {
                ConfigurationSection section = configuration.getConfigurationSection(configKey);
                for (String key : section.getKeys(false)) {
                    enablePlaceholders.put(key, section.getBoolean(key));
                }
            }
        }
        return new ZCustomClickDialogAction(actions, usageLimit, actionDurationLimit, enablePlaceholders);
    }
}
