package fr.maxlego08.menu.api.utils.resolvable.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.Keyed;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record TagKeySetResolvable<T extends Keyed>(
        @NotNull RegistryKey<T> registryKey,
        @NotNull TagKeyResolvable<T> tag
) implements Resolvable<RegistryKeySet<T>> {

    /**
     * @param value The tag, with or without its leading {@code #}.
     */
    public static <T extends Keyed> @NotNull TagKeySetResolvable<T> of(@NotNull RegistryKey<T> registryKey, @NotNull String value) {
        String key = value.startsWith("#") ? value.substring(1) : value;
        return new TagKeySetResolvable<>(registryKey, ResolvableRegistryKey.tagKey(registryKey, key));
    }

    @Override
    public @Nullable RegistryKeySet<T> resolve(@NotNull BuildContext context) {
        TagKey<T> tagKey = this.tag.resolve(context);
        if (tagKey == null) return null;
        Registry<T> registry = RegistryAccess.registryAccess().getRegistry(this.registryKey);
        return registry.hasTag(tagKey) ? registry.getTag(tagKey) : null;
    }

    @Override
    public @NotNull Object serialize() {
        return "#" + this.tag.serialize();
    }
}
