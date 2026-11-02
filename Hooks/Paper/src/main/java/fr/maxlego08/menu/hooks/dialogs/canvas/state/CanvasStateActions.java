package fr.maxlego08.menu.hooks.dialogs.canvas.state;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.dialog.canvas.CanvasStateService;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The placeholders of the canvas state and the loaders of the {@code canvas-*} actions:
 *
 * <pre>
 * - type: canvas-set        # key, value (placeholders allowed)
 * - type: canvas-toggle     # key: true &lt;-&gt; false
 * - type: canvas-cycle      # key, values: [a, b, c], reverse: false
 * - type: canvas-add        # key, amount (1 by default, may be negative), min, max
 * - type: canvas-remove     # key
 * - type: canvas-refresh    # opens the dialog again to show the changes
 * </pre>
 * Every action also takes {@code dialog} (the dialog file name, needed outside a dialog) and the setters take
 * {@code keep: open|session}.
 */
public final class CanvasStateActions {

    /**
     * The prefix of a state value in the placeholders of a dialog: {@code %canvas_<key>%}.
     */
    public static final String PREFIX = "canvas_";

    /**
     * The placeholder holding the file name of the open dialog: {@code %canvas_dialog%}.
     */
    public static final String DIALOG_PLACEHOLDER = PREFIX + "dialog";

    private CanvasStateActions() {
    }

    /**
     * The value of {@code %canvas_<key>%}: empty when the key is not set, so the placeholder never shows as
     * written. A key holding a dot is a key followed by modifiers ({@code %canvas_tab.default:shop%}): it gives
     * null, so the modifiers are applied to the value of the key before the dot.
     *
     * @param value The value of the key, or null.
     * @param key   The key, as written after {@code canvas_}.
     * @return The value of the placeholder, or null.
     */
    public static @Nullable String lookup(@Nullable String value, @NotNull String key) {
        if (key.indexOf('.') >= 0) return null;
        return value == null ? "" : value;
    }

    /**
     * @param key A state key.
     * @return True if the key can be used: not blank, not {@code dialog}, without placeholders or dots.
     */
    public static boolean isValidKey(@NotNull String key) {
        return !key.isBlank() && !key.equals("dialog") && key.indexOf('%') < 0 && key.indexOf('.') < 0 && key.indexOf(' ') < 0;
    }

    /**
     * @param plugin The plugin.
     * @return One loader per action.
     */
    public static @NotNull List<ActionLoader> loaders(@NotNull MenuPlugin plugin) {
        List<ActionLoader> loaders = new ArrayList<>();
        for (CanvasStateAction.Operation operation : CanvasStateAction.Operation.values()) loaders.add(new Loader(plugin, operation));
        return loaders;
    }

    private static final class Loader extends ActionLoader {

        private final MenuPlugin plugin;
        private final CanvasStateAction.Operation operation;

        private Loader(@NotNull MenuPlugin plugin, @NotNull CanvasStateAction.Operation operation) {
            super(operation.type(), operation.type().replace('-', '_'));
            this.plugin = plugin;
            this.operation = operation;
        }

        @Override
        public @Nullable Action load(@NotNull String path, @NotNull TypedMapAccessor accessor, @NotNull File file) {
            String dialog = accessor.contains("dialog") ? accessor.getString("dialog") : null;
            if (this.operation == CanvasStateAction.Operation.REFRESH) {
                return new CanvasStateAction(this.plugin, this.operation, dialog, null, null, List.of(), false, null, null, null, CanvasStateService.Keep.OPEN);
            }

            String key = accessor.getString("key", "");
            if (!isValidKey(key)) {
                return this.error(path, file, "needs a key: any name but dialog, without spaces, dots or placeholders");
            }
            List<String> values = this.operation == CanvasStateAction.Operation.CYCLE ? accessor.getStringList("values") : List.of();
            if (this.operation == CanvasStateAction.Operation.CYCLE && values.isEmpty()) return this.error(path, file, "needs values: [a, b, ...]");
            Object value = accessor.getObject("value", null);
            if (this.operation == CanvasStateAction.Operation.SET && value == null) return this.error(path, file, "needs a value");
            Object amount = accessor.getObject("amount", null);

            return new CanvasStateAction(this.plugin, this.operation, dialog, key, value == null ? null : String.valueOf(value), values,
                    accessor.getBoolean("reverse", false), amount == null ? null : String.valueOf(amount),
                    accessor.contains("min") ? accessor.getDouble("min") : null, accessor.contains("max") ? accessor.getDouble("max") : null,
                    CanvasStateService.Keep.parse(accessor.getString("keep", "open")));
        }

        private @Nullable Action error(@NotNull String path, @NotNull File file, @NotNull String message) {
            Logger.info("The action " + this.operation.type() + " at " + path + " of " + file.getName() + " " + message, Logger.LogType.ERROR);
            return null;
        }
    }
}
