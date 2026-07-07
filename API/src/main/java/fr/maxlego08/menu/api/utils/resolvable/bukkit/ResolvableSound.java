package fr.maxlego08.menu.api.utils.resolvable.bukkit;

import fr.maxlego08.menu.api.utils.resolvable.ParsableResolvable;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ResolvableSound extends ParsableResolvable<Sound> {

    private ResolvableSound(@Nullable Sound resolvedValue, @Nullable String expression) {
        super(resolvedValue, expression);
    }

    public static @NotNull ResolvableSound of(@NotNull Sound value) {
        return new ResolvableSound(value, null);
    }

    public static @NotNull ResolvableSound of(@NotNull String expression) {
        return new ResolvableSound(null, expression);
    }

    public static @NotNull ResolvableSound auto(@NotNull String value) {
        if (value.contains("%")) {
            return new ResolvableSound(null, value);
        }
        return auto(value, s -> {
            NamespacedKey k = NamespacedKey.fromString(s);
            return k != null ? Registry.SOUNDS.getOrThrow(k) : null;
        }, ResolvableSound::new);
    }

    @Nullable
    @Contract("null -> null; !null -> !null")
    public static ResolvableSound autoOrNull(@Nullable String value) {
        return autoOrNull(value, s -> {
            NamespacedKey k = NamespacedKey.fromString(s);
            return k != null ? Registry.SOUNDS.getOrThrow(k) : null;
        }, ResolvableSound::new);
    }

    @Override
    protected @Nullable Sound parse(@NotNull String value) {
        try {
            NamespacedKey key = NamespacedKey.fromString(value);
            return key != null ? Registry.SOUNDS.getOrThrow(key) : null;
        } catch (Exception e) {
            return null;
        }
    }
}
