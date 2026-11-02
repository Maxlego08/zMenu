package fr.maxlego08.menu.hooks.dialogs.canvas.state;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.dialog.canvas.CanvasManager;
import fr.maxlego08.menu.api.dialog.canvas.CanvasStateService;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Changes the state of a dialog canvas: {@code canvas-set}, {@code canvas-toggle}, {@code canvas-cycle},
 * {@code canvas-add}, {@code canvas-remove} and {@code canvas-refresh}.
 * <p>
 * The dialog is the one the action runs in ({@code %canvas_dialog%}), or {@code dialog} when it runs
 * elsewhere. Only {@code canvas-refresh} opens the dialog again. A changed value is also put in the action's
 * placeholders, so the actions after it read the new {@code %canvas_<key>%}. A value that is a single
 * placeholder of the dialog ({@code value: "%name%"}, an input) is stored as given, without PlaceholderAPI, so a
 * player cannot make the server resolve a placeholder by typing it.
 */
public final class CanvasStateAction extends Action {

    private static final Set<String> REPORTED = ConcurrentHashMap.newKeySet();
    private static final Pattern SINGLE_PLACEHOLDER = Pattern.compile("%[^%\\s]+%");

    /**
     * What the action does.
     */
    public enum Operation {
        SET("canvas-set"),
        TOGGLE("canvas-toggle"),
        CYCLE("canvas-cycle"),
        ADD("canvas-add"),
        REMOVE("canvas-remove"),
        REFRESH("canvas-refresh");

        private final String type;

        Operation(@NotNull String type) {
            this.type = type;
        }

        /**
         * @return The {@code type} of the action in YAML.
         */
        public @NotNull String type() {
            return this.type;
        }
    }

    private final MenuPlugin plugin;
    private final Operation operation;
    private final String dialog;
    private final String key;
    private final String value;
    private final List<String> values;
    private final boolean reverse;
    private final String amount;
    private final Double min;
    private final Double max;
    private final CanvasStateService.Keep keep;

    public CanvasStateAction(@NotNull MenuPlugin plugin, @NotNull Operation operation, @Nullable String dialog, @Nullable String key, @Nullable String value,
                             @NotNull List<String> values, boolean reverse, @Nullable String amount, @Nullable Double min, @Nullable Double max,
                             @NotNull CanvasStateService.Keep keep) {
        this.plugin = plugin;
        this.operation = operation;
        this.dialog = dialog;
        this.key = key;
        this.value = value;
        this.values = List.copyOf(values);
        this.reverse = reverse;
        this.amount = amount;
        this.min = min;
        this.max = max;
        this.keep = keep;
    }

    private @NotNull String parse(@NotNull Player player, @NotNull Placeholders placeholders, @NotNull String text) {
        String local = placeholders.parse(text);
        if (!local.equals(text) && SINGLE_PLACEHOLDER.matcher(text).matches()) return local;
        return this.plugin.parse(player, local);
    }

    @Override
    protected void execute(@NotNull Player player, @Nullable Button button, @NotNull InventoryEngine inventoryEngine, @NotNull Placeholders placeholders) {
        CanvasManager canvasManager = this.plugin.getCanvasManager();
        if (canvasManager == null) return;
        String dialogName = this.dialog == null ? placeholders.get(CanvasStateActions.DIALOG_PLACEHOLDER) : this.parse(player, placeholders, this.dialog);
        if (dialogName == null || dialogName.isBlank()) {
            if (REPORTED.add(this.operation.type() + ":" + this.key)) {
                Logger.info("The action " + this.operation.type() + " runs outside a dialog, so it needs dialog: <dialog file name>", Logger.LogType.WARNING);
            }
            return;
        }

        if (this.operation == Operation.REFRESH) {
            canvasManager.reopen(player, dialogName);
            return;
        }

        CanvasStateService state = canvasManager.getStateService();
        String current = state.get(player.getUniqueId(), dialogName, this.key);
        String next = switch (this.operation) {
            case SET -> this.value == null ? "" : this.parse(player, placeholders, this.value);
            case TOGGLE -> "true".equalsIgnoreCase(current) ? "false" : "true";
            case CYCLE -> this.cycle(current);
            case ADD -> this.add(player, placeholders, current);
            case REMOVE, REFRESH -> null;
        };

        if (next == null) {
            state.remove(player.getUniqueId(), dialogName, this.key);
            placeholders.register(CanvasStateActions.PREFIX + this.key, "");
            return;
        }
        state.set(player.getUniqueId(), dialogName, this.key, next, this.keep);
        placeholders.register(CanvasStateActions.PREFIX + this.key, next);
    }

    private @NotNull String cycle(@Nullable String current) {
        int index = current == null ? -1 : this.values.indexOf(current);
        int size = this.values.size();
        if (index < 0) return this.reverse ? this.values.getLast() : this.values.getFirst();
        return this.values.get(Math.floorMod(index + (this.reverse ? -1 : 1), size));
    }

    private @NotNull String add(@NotNull Player player, @NotNull Placeholders placeholders, @Nullable String current) {
        double base = number(current, 0);
        double result = base + number(this.amount == null ? "1" : this.parse(player, placeholders, this.amount), 0);
        if (this.min != null) result = Math.max(this.min, result);
        if (this.max != null) result = Math.min(this.max, result);
        return format(result);
    }

    /**
     * @param text     A number as text.
     * @param fallback The number when the text is not one.
     * @return The number.
     */
    public static double number(@Nullable String text, double fallback) {
        if (text == null || text.isBlank()) return fallback;
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }

    /**
     * @param value A number.
     * @return The number as text, without decimals when it is whole.
     */
    public static @NotNull String format(double value) {
        if (value == Math.rint(value) && Math.abs(value) < 1e15) return String.valueOf((long) value);
        return String.valueOf(value);
    }

    @Override
    protected void serializeProperties(@NotNull Map<String, Object> map) {
        map.put("type", this.operation.type());
        if (this.dialog != null) map.put("dialog", this.dialog);
        if (this.operation == Operation.REFRESH) return;
        map.put("key", this.key);
        if (this.keep != CanvasStateService.Keep.OPEN) map.put("keep", this.keep.name().toLowerCase(Locale.ROOT));
        switch (this.operation) {
            case SET -> map.put("value", this.value);
            case CYCLE -> {
                map.put("values", this.values);
                if (this.reverse) map.put("reverse", true);
            }
            case ADD -> {
                if (this.amount != null) map.put("amount", this.amount);
                if (this.min != null) map.put("min", this.min);
                if (this.max != null) map.put("max", this.max);
            }
            default -> {
            }
        }
    }
}
