package fr.maxlego08.menu.hooks.dialogs.canvas.state;

import fr.maxlego08.menu.api.dialog.canvas.CanvasStateService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps the values of canvas widgets in memory, by player, dialog and key.
 */
public final class ZCanvasStateService implements CanvasStateService {

    private final Map<UUID, Map<String, Map<String, Value>>> values = new ConcurrentHashMap<>();
    private final Map<UUID, Set<String>> reopening = new ConcurrentHashMap<>();

    private record Value(@NotNull String value, @NotNull Keep keep) {
    }

    private static @NotNull String dialogKey(@NotNull String dialog) {
        return dialog.toLowerCase(Locale.ROOT);
    }

    private @Nullable Map<String, Value> dialog(@NotNull UUID player, @NotNull String dialog) {
        Map<String, Map<String, Value>> dialogs = this.values.get(player);
        return dialogs == null ? null : dialogs.get(dialogKey(dialog));
    }

    @Override
    public @Nullable String get(@NotNull UUID player, @NotNull String dialog, @NotNull String key) {
        Map<String, Value> values = this.dialog(player, dialog);
        Value value = values == null ? null : values.get(key);
        return value == null ? null : value.value();
    }

    @Override
    public void set(@NotNull UUID player, @NotNull String dialog, @NotNull String key, @NotNull String value, @NotNull Keep keep) {
        this.values.computeIfAbsent(player, uuid -> new ConcurrentHashMap<>())
                .computeIfAbsent(dialogKey(dialog), name -> new ConcurrentHashMap<>())
                .put(key, new Value(value, keep));
    }

    @Override
    public void remove(@NotNull UUID player, @NotNull String dialog, @NotNull String key) {
        Map<String, Value> values = this.dialog(player, dialog);
        if (values != null) values.remove(key);
    }

    @Override
    public @NotNull Map<String, String> all(@NotNull UUID player, @NotNull String dialog) {
        Map<String, Value> values = this.dialog(player, dialog);
        Map<String, String> result = new LinkedHashMap<>();
        if (values != null) values.forEach((key, value) -> result.put(key, value.value()));
        return result;
    }

    @Override
    public @NotNull Map<String, Map<String, String>> all(@NotNull UUID player) {
        Map<String, Map<String, String>> result = new LinkedHashMap<>();
        Map<String, Map<String, Value>> dialogs = this.values.get(player);
        if (dialogs == null) return result;
        dialogs.forEach((dialog, values) -> {
            if (!values.isEmpty()) result.put(dialog, this.all(player, dialog));
        });
        return result;
    }

    @Override
    public void markReopen(@NotNull UUID player, @NotNull String dialog) {
        this.reopening.computeIfAbsent(player, uuid -> ConcurrentHashMap.newKeySet()).add(dialogKey(dialog));
    }

    @Override
    public void cancelReopen(@NotNull UUID player, @NotNull String dialog) {
        Set<String> marked = this.reopening.get(player);
        if (marked != null) marked.remove(dialogKey(dialog));
    }

    @Override
    public void onOpen(@NotNull UUID player, @NotNull String dialog) {
        Set<String> marked = this.reopening.get(player);
        if (marked != null && marked.remove(dialogKey(dialog))) return;
        Map<String, Value> values = this.dialog(player, dialog);
        if (values != null) values.values().removeIf(value -> value.keep() == Keep.OPEN);
    }

    @Override
    public void clear(@NotNull UUID player) {
        this.values.remove(player);
        this.reopening.remove(player);
    }

    @Override
    public void clearAll() {
        this.values.clear();
        this.reopening.clear();
    }
}
