package fr.maxlego08.menu.hooks.dialogs.loader;

import fr.maxlego08.menu.api.enums.dialog.DialogType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Finds a {@code canvas-refresh} in the button Escape runs. The client runs a dialog's exit action when the
 * player presses Escape: the only button of a notice, the "no" button of a confirmation, the exit button of
 * the other types. A {@code canvas-refresh} there opens the dialog again, so Escape never closes it.
 */
public final class EscapeActionCheck {

    private static final Set<String> REFRESH = Set.of("canvas-refresh", "canvas_refresh");

    private EscapeActionCheck() {
    }

    /**
     * @param configuration The dialog file.
     * @param type          The dialog type.
     * @return The path of the button Escape runs, or null when it has none.
     */
    public static @Nullable String escapeButton(@NotNull YamlConfiguration configuration, @NotNull DialogType type) {
        return switch (type) {
            case NOTICE -> "notice";
            case CONFIRMATION -> "confirmation.no";
            case MULTI_ACTION, DIALOG_LIST -> configuration.isConfigurationSection("exit-button") ? "exit-button" : null;
            case SERVER_LINKS -> configuration.isConfigurationSection("server-links") ? "server-links" : null;
        };
    }

    /**
     * @param configuration The dialog file.
     * @param type          The dialog type.
     * @return The path of the button Escape runs when its actions hold a {@code canvas-refresh}.
     */
    public static @NotNull Optional<String> refreshOnEscape(@NotNull YamlConfiguration configuration, @NotNull DialogType type) {
        if (!configuration.getBoolean("can-close-with-escape", true)) return Optional.empty();
        String button = escapeButton(configuration, type);
        if (button == null) return Optional.empty();
        String last = button.contains(".") ? button.substring(button.lastIndexOf('.') + 1) : button;
        for (String path : List.of(button + ".actions", last + "-actions", "actions")) {
            Object actions = configuration.get(path);
            if (actions == null || isEmpty(actions)) continue;
            return holdsRefresh(actions) ? Optional.of(button) : Optional.empty();
        }
        return Optional.empty();
    }

    private static boolean isEmpty(@NotNull Object value) {
        if (value instanceof ConfigurationSection section) return section.getKeys(false).isEmpty();
        if (value instanceof List<?> list) return list.isEmpty();
        return false;
    }

    private static boolean holdsRefresh(@Nullable Object value) {
        if (value instanceof ConfigurationSection section) {
            if (isRefresh(section.get("type"))) return true;
            return section.getKeys(false).stream().anyMatch(key -> holdsRefresh(section.get(key)));
        }
        if (value instanceof Map<?, ?> map) {
            if (isRefresh(map.get("type"))) return true;
            return map.values().stream().anyMatch(EscapeActionCheck::holdsRefresh);
        }
        if (value instanceof List<?> list) return list.stream().anyMatch(EscapeActionCheck::holdsRefresh);
        return false;
    }

    private static boolean isRefresh(@Nullable Object type) {
        return type != null && REFRESH.contains(String.valueOf(type).toLowerCase(Locale.ROOT));
    }
}
