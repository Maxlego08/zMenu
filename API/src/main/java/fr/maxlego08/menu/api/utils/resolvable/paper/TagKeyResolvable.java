package fr.maxlego08.menu.api.utils.resolvable.paper;

import fr.maxlego08.menu.api.utils.resolvable.ParsableResolvable;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

public final class TagKeyResolvable<T extends Keyed> extends ParsableResolvable<TagKey<T>> {

    private final RegistryKey<T> registryKey;

    private TagKeyResolvable(
            @Nullable TagKey<T> resolvedValue,
            @Nullable String expression,
            @NotNull RegistryKey<T> registryKey
    ) {
        super(resolvedValue, expression);
        this.registryKey = registryKey;
    }

    static <T extends Keyed> @NotNull TagKeyResolvable<T> auto(
            @NotNull String value,
            @NotNull RegistryKey<T> registryKey
    ) {
        Function<String, TagKey<T>> parser = v -> {
            NamespacedKey key = NamespacedKey.fromString(v);
            if (key == null) return null;
            return registryKey.tagKey(key);
        };
        BiFunction<TagKey<T>, String, TagKeyResolvable<T>> factory =
                (resolved, expr) -> new TagKeyResolvable<>(resolved, expr, registryKey);
        return auto(value, parser, factory);
    }

    @Override
    protected @Nullable TagKey<T> parse(@NotNull String value) {
        NamespacedKey key = NamespacedKey.fromString(value);
        if (key == null) return null;
        return this.registryKey.tagKey(key);
    }
}