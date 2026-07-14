package fr.maxlego08.menu.api.utils.resolvable.bukkit;

import fr.maxlego08.menu.api.utils.resolvable.ParsableResolvable;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public final class ResolvableNamespacedKey extends ParsableResolvable<NamespacedKey> {

    private ResolvableNamespacedKey(@Nullable NamespacedKey resolvedValue, @Nullable String expression) {
        super(resolvedValue, expression);
    }

    public static ResolvableNamespacedKey of(@NotNull NamespacedKey value) {
        return new ResolvableNamespacedKey(value, null);
    }

    public static ResolvableNamespacedKey of(@NotNull String expression) {
        return new ResolvableNamespacedKey(null, expression);
    }

    public static @NotNull ResolvableNamespacedKey auto(@NotNull String value) {
        return auto(value, s -> NamespacedKey.fromString(s.toLowerCase(Locale.ROOT)), ResolvableNamespacedKey::new);
    }

    @Nullable
    @Contract("null -> null; !null -> !null")
    public static ResolvableNamespacedKey autoOrNull(@Nullable String value) {
        if (value == null) return null;
        return auto(value);
    }

    @Override
    protected @Nullable NamespacedKey parse(@NotNull String value) {
        try {
            return NamespacedKey.fromString(value.toLowerCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
