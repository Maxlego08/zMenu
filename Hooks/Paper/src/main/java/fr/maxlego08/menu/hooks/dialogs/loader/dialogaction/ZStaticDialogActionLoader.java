package fr.maxlego08.menu.hooks.dialogs.loader.dialogaction;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.utils.dialogs.ZDialogAction;
import fr.maxlego08.menu.hooks.dialogs.action.*;
import fr.maxlego08.menu.hooks.dialogs.loader.DialogActionLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Locale;

public class ZStaticDialogActionLoader implements DialogActionLoader {

    private static final String MISSING_VALUE = "Missing static action %s in file: %s";

    @Override
    public @NotNull String getType() {
        return "static";
    }

    @Override
    public @NotNull ZDialogAction load(@NotNull MenuPlugin plugin, @NotNull YamlConfiguration configuration, @NotNull String path, @NotNull File file) {
        String raw = configuration.getString(path + ".static-type", "OPEN_URL");
        StaticType type;
        try {
            type = StaticType.valueOf(raw.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid static action type: " + raw + " in file: " + file.getName());
        }
        return switch (type) {
            case OPEN_URL -> this.loadOpenUrlAction(configuration, path, file);
            case OPEN_FILE -> this.loadOpenFileAction(configuration, path, file);
            case RUN_COMMAND -> this.loadRunCommandAction(configuration, path, file);
            case SUGGEST_COMMAND -> this.loadSuggestCommandAction(configuration, path, file);
            case COPY_TO_CLIPBOARD -> this.loadCopyToClipboardAction(configuration, path, file);
        };
    }

    private ZDialogAction loadOpenUrlAction(@NotNull YamlConfiguration configuration, @NotNull String path, @NotNull File file) {
        String url = getRequiredString(configuration, path + ".url", "URL", file);
        return new ZStaticOpenUrlDialogAction(url);
    }

    private ZDialogAction loadOpenFileAction(@NotNull YamlConfiguration configuration, @NotNull String path, @NotNull File file) {
        String filePath = getRequiredString(configuration, path + ".file", "file path", file);
        return new ZStaticOpenFileDialogAction(filePath);
    }

    private ZDialogAction loadRunCommandAction(@NotNull YamlConfiguration configuration, @NotNull String path, @NotNull File file) {
        String command = getRequiredString(configuration, path + ".command", "command", file);
        return new ZStaticRunCommandDialogAction(command);
    }

    private ZDialogAction loadSuggestCommandAction(@NotNull YamlConfiguration configuration, @NotNull String path, @NotNull File file) {
        String command = getRequiredString(configuration, path + ".command", "command", file);
        return new ZStaticSuggestCommandDialogAction(command);
    }

    private ZDialogAction loadCopyToClipboardAction(@NotNull YamlConfiguration configuration, @NotNull String path, @NotNull File file) {
        String text = getRequiredString(configuration, path + ".text", "copy to clipboard text", file);
        return new ZStaticCopyToClipBoardDialogAction(text);
    }

    private static @NotNull String getRequiredString(@NotNull YamlConfiguration configuration, @NotNull String configKey, @NotNull String label, @NotNull File file) {
        String value = configuration.getString(configKey);
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(MISSING_VALUE.formatted(label, file.getName()));
        }
        return value;
    }

    private enum StaticType {
        OPEN_URL,
        OPEN_FILE,
        RUN_COMMAND,
        SUGGEST_COMMAND,
        COPY_TO_CLIPBOARD
    }
}
