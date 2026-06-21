package fr.maxlego08.menu.api.utils.resolvable.bukkit;

import fr.maxlego08.menu.api.utils.resolvable.ParsableResolvable;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class RegistryEntry<T extends Keyed> extends ParsableResolvable<T> {

    private final @NotNull Function<NamespacedKey, @Nullable T> resolver;

    private RegistryEntry(
            @Nullable T resolvedValue,
            @Nullable String expression,
            @NotNull Function<NamespacedKey, @Nullable T> resolver
    ) {
        super(resolvedValue, expression);
        this.resolver = resolver;
    }

    @Override
    protected @Nullable T parse(@NotNull String value) {
        NamespacedKey key = NamespacedKey.fromString(value);
        if (key == null) return null;
        try {
            return this.resolver.apply(key);
        } catch (Exception e) {
            return null;
        }
    }

    // ── Internal builders ───────────────────────────────────────────────────

    @NotNull
    public static <T extends Keyed> RegistryEntry<T> ofValue(
            @NotNull T value,
            @NotNull Function<NamespacedKey, @Nullable T> resolver
    ) {
        return new RegistryEntry<>(value, null, resolver);
    }

    @NotNull
    public static <T extends Keyed> RegistryEntry<T> ofExpression(
            @NotNull String expression,
            @NotNull Function<NamespacedKey, @Nullable T> resolver
    ) {
        return new RegistryEntry<>(null, expression, resolver);
    }

    @NotNull
    public static <T extends Keyed> RegistryEntry<T> auto(
            @NotNull String value,
            @NotNull Function<NamespacedKey, @Nullable T> resolver
    ) {
        NamespacedKey key = NamespacedKey.fromString(value);
        if (key != null) {
            try {
                T resolved = resolver.apply(key);
                if (resolved != null) return ofValue(resolved, resolver);
            } catch (Exception ignored) {}
        }
        return ofExpression(value, resolver);
    }

    @Nullable
    public static <T extends Keyed> RegistryEntry<T> autoOrNull(
            @Nullable String value,
            @NotNull Function<NamespacedKey, @Nullable T> resolver
    ) {
        if (value == null) return null;
        return auto(value, resolver);
    }
}