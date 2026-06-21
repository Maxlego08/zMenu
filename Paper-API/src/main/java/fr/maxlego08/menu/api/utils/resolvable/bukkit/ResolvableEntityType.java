package fr.maxlego08.menu.api.utils.resolvable.bukkit;

import fr.maxlego08.menu.api.utils.resolvable.ParsableResolvable;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public final class ResolvableEntityType extends ParsableResolvable<EntityType> {

    private ResolvableEntityType(@Nullable EntityType resolvedValue, @Nullable String expression) {
        super(resolvedValue, expression);
    }

    public static @NotNull ResolvableEntityType of(@NotNull EntityType value) {
        return new ResolvableEntityType(value, null);
    }

    public static @NotNull ResolvableEntityType of(@NotNull String expression) {
        return new ResolvableEntityType(null, expression);
    }

    public static @NotNull ResolvableEntityType auto(@NotNull String value) {
        if (value.contains("%")) {
            return new ResolvableEntityType(null, value);
        }
        return auto(value, s -> {
            NamespacedKey k = NamespacedKey.fromString(s.toLowerCase(Locale.ROOT));
            return k != null ? Registry.ENTITY_TYPE.getOrThrow(k) : null;
        }, ResolvableEntityType::new);
    }

    @Nullable
    @Contract("null -> null; !null -> !null")
    public static ResolvableEntityType autoOrNull(@Nullable String value) {
        return autoOrNull(value, s -> {
            NamespacedKey k = NamespacedKey.fromString(s.toLowerCase(Locale.ROOT));
            return k != null ? Registry.ENTITY_TYPE.getOrThrow(k) : null;
        }, ResolvableEntityType::new);
    }

    @Override
    protected @Nullable EntityType parse(@NotNull String value) {
        try {
            NamespacedKey key = NamespacedKey.fromString(value.toLowerCase(Locale.ROOT));
            return key != null ? Registry.ENTITY_TYPE.getOrThrow(key) : null;
        } catch (Exception e) {
            return null;
        }
    }
}
