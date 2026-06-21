package fr.maxlego08.menu.api.utils.resolvable.bukkit;

import fr.maxlego08.menu.api.utils.resolvable.ParsableResolvable;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.damage.DamageType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public final class ResolvableDamageTypeTag extends ParsableResolvable<Tag<DamageType>> {

    private ResolvableDamageTypeTag(@Nullable Tag<DamageType> resolvedValue, @Nullable String expression) {
        super(resolvedValue, expression);
    }

    public static @NotNull ResolvableDamageTypeTag of(@NotNull Tag<DamageType> value) {
        return new ResolvableDamageTypeTag(value, null);
    }

    public static @NotNull ResolvableDamageTypeTag of(@NotNull String expression) {
        return new ResolvableDamageTypeTag(null, expression);
    }

    public static @NotNull ResolvableDamageTypeTag auto(@NotNull String value) {
        if (value.contains("%")) {
            return new ResolvableDamageTypeTag(null, value);
        }
        return auto(value, s -> {
            NamespacedKey k = NamespacedKey.fromString(s.toLowerCase(Locale.ROOT));
            return k != null ? Bukkit.getTag("damage-types", k, DamageType.class) : null;
        }, ResolvableDamageTypeTag::new);
    }

    @Nullable
    @Contract("null -> null; !null -> !null")
    public static ResolvableDamageTypeTag autoOrNull(@Nullable String value) {
        return autoOrNull(value, s -> {
            NamespacedKey k = NamespacedKey.fromString(s.toLowerCase(Locale.ROOT));
            return k != null ? Bukkit.getTag("damage-types", k, DamageType.class) : null;
        }, ResolvableDamageTypeTag::new);
    }

    @Override
    protected @Nullable Tag<DamageType> parse(@NotNull String value) {
        NamespacedKey key = NamespacedKey.fromString(value.toLowerCase(Locale.ROOT));
        if (key == null) return null;
        return Bukkit.getTag("damage-types", key, DamageType.class);
    }
}
