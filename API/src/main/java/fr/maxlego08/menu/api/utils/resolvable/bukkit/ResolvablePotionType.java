package fr.maxlego08.menu.api.utils.resolvable.bukkit;

import fr.maxlego08.menu.api.utils.resolvable.ParsableResolvable;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ResolvablePotionType extends ParsableResolvable<PotionType> {

    private ResolvablePotionType(@Nullable PotionType resolvedValue, @Nullable String expression) {
        super(resolvedValue, expression);
    }

    public static @NotNull ResolvablePotionType of(@NotNull PotionType value) {
        return new ResolvablePotionType(value, null);
    }

    public static @NotNull ResolvablePotionType of(@NotNull String expression) {
        return new ResolvablePotionType(null, expression);
    }

    public static @NotNull ResolvablePotionType auto(@NotNull String value) {
        if (value.contains("%")) {
            return new ResolvablePotionType(null, value);
        }
        return auto(value, s -> {
            NamespacedKey k = NamespacedKey.fromString(s);
            return k != null ? Registry.POTION.getOrThrow(k) : null;
        }, ResolvablePotionType::new);
    }

    @Nullable
    @Contract("null -> null; !null -> !null")
    public static ResolvablePotionType autoOrNull(@Nullable String value) {
        return autoOrNull(value, s -> {
            NamespacedKey k = NamespacedKey.fromString(s);
            return k != null ? Registry.POTION.getOrThrow(k) : null;
        }, ResolvablePotionType::new);
    }

    @Override
    protected @Nullable PotionType parse(@NotNull String value) {
        try {
            NamespacedKey key = NamespacedKey.fromString(value);
            return key != null ? Registry.POTION.getOrThrow(key) : null;
        } catch (Exception e) {
            return null;
        }
    }
}
