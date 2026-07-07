package fr.maxlego08.menu.api.utils.resolvable.bukkit;

import fr.maxlego08.menu.api.utils.resolvable.ParsableResolvable;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public final class ResolvableEntityTypeTag extends ParsableResolvable<Tag<EntityType>> {

    private ResolvableEntityTypeTag(@Nullable Tag<EntityType> resolvedValue, @Nullable String expression) {
        super(resolvedValue, expression);
    }

    public static @NotNull ResolvableEntityTypeTag of(@NotNull Tag<EntityType> value) {
        return new ResolvableEntityTypeTag(value, null);
    }

    public static @NotNull ResolvableEntityTypeTag of(@NotNull String expression) {
        return new ResolvableEntityTypeTag(null, expression);
    }

    public static @NotNull ResolvableEntityTypeTag auto(@NotNull String value) {
        if (value.contains("%")) {
            return new ResolvableEntityTypeTag(null, value);
        }
        return auto(value, s -> {
            NamespacedKey k = NamespacedKey.fromString(s.toLowerCase(Locale.ROOT));
            return k != null ? Bukkit.getTag("entity-types", k, EntityType.class) : null;
        }, ResolvableEntityTypeTag::new);
    }

    @Nullable
    @Contract("null -> null; !null -> !null")
    public static ResolvableEntityTypeTag autoOrNull(@Nullable String value) {
        return autoOrNull(value, s -> {
            NamespacedKey k = NamespacedKey.fromString(s.toLowerCase(Locale.ROOT));
            return k != null ? Bukkit.getTag("entity-types", k, EntityType.class) : null;
        }, ResolvableEntityTypeTag::new);
    }

    @Override
    protected @Nullable Tag<EntityType> parse(@NotNull String value) {
        NamespacedKey key = NamespacedKey.fromString(value.toLowerCase(Locale.ROOT));
        if (key == null) return null;
        return Bukkit.getTag("entity-types", key, EntityType.class);
    }
}
