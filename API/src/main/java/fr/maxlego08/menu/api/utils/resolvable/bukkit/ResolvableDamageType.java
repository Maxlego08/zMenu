package fr.maxlego08.menu.api.utils.resolvable.bukkit;

import fr.maxlego08.menu.api.utils.resolvable.ParsableResolvable;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.damage.DamageType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ResolvableDamageType extends ParsableResolvable<DamageType> {

    private ResolvableDamageType(@Nullable DamageType resolvedValue, @Nullable String expression) {
        super(resolvedValue, expression);
    }

    public static @NotNull ResolvableDamageType of(@NotNull DamageType value) {
        return new ResolvableDamageType(value, null);
    }

    public static @NotNull ResolvableDamageType of(@NotNull String expression) {
        return new ResolvableDamageType(null, expression);
    }

    public static @NotNull ResolvableDamageType auto(@NotNull String value) {
        if (value.contains("%")) {
            return new ResolvableDamageType(null, value);
        }
        return auto(value, s -> {
            NamespacedKey k = NamespacedKey.fromString(s);
            return k != null ? Registry.DAMAGE_TYPE.getOrThrow(k) : null;
        }, ResolvableDamageType::new);
    }

    @Nullable
    @Contract("null -> null; !null -> !null")
    public static ResolvableDamageType autoOrNull(@Nullable String value) {
        return autoOrNull(value, s -> {
            NamespacedKey k = NamespacedKey.fromString(s);
            return k != null ? Registry.DAMAGE_TYPE.getOrThrow(k) : null;
        }, ResolvableDamageType::new);
    }

    @Override
    protected @Nullable DamageType parse(@NotNull String value) {
        try {
            NamespacedKey key = NamespacedKey.fromString(value);
            return key != null ? Registry.DAMAGE_TYPE.getOrThrow(key) : null;
        } catch (Exception e) {
            return null;
        }
    }
}
