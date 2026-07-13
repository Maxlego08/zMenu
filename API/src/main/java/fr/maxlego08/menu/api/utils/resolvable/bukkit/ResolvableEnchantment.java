package fr.maxlego08.menu.api.utils.resolvable.bukkit;

import fr.maxlego08.menu.api.utils.resolvable.ParsableResolvable;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public final class ResolvableEnchantment extends ParsableResolvable<Enchantment> {

    private ResolvableEnchantment(@Nullable Enchantment resolvedValue, @Nullable String expression) {
        super(resolvedValue, expression);
    }

    public static @NotNull ResolvableEnchantment of(@NotNull Enchantment value) {
        return new ResolvableEnchantment(value, null);
    }

    public static @NotNull ResolvableEnchantment of(@NotNull String expression) {
        return new ResolvableEnchantment(null, expression);
    }

    public static @NotNull ResolvableEnchantment auto(@NotNull String value) {
        if (value.contains("%")) {
            return new ResolvableEnchantment(null, value);
        }
        return auto(value, s -> {
            NamespacedKey k = NamespacedKey.fromString(s.toLowerCase(Locale.ROOT));
            return k != null ? Registry.ENCHANTMENT.getOrThrow(k) : null;
        }, ResolvableEnchantment::new);
    }

    @Nullable
    @Contract("null -> null; !null -> !null")
    public static ResolvableEnchantment autoOrNull(@Nullable String value) {
        return autoOrNull(value, s -> {
            NamespacedKey k = NamespacedKey.fromString(s.toLowerCase(Locale.ROOT));
            return k != null ? Registry.ENCHANTMENT.getOrThrow(k) : null;
        }, ResolvableEnchantment::new);
    }

    @Override
    protected @Nullable Enchantment parse(@NotNull String value) {
        try {
            NamespacedKey key = NamespacedKey.fromString(value.toLowerCase(Locale.ROOT));
            return key != null ? Registry.ENCHANTMENT.getOrThrow(key) : null;
        } catch (Exception e) {
            return null;
        }
    }
}
