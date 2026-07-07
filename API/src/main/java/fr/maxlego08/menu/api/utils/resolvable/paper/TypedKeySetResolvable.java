package fr.maxlego08.menu.api.utils.resolvable.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import org.bukkit.Keyed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record TypedKeySetResolvable<T extends Keyed>(
        @NotNull RegistryKey<T> registryKey,
        @NotNull List<Resolvable<TypedKey<T>>> keys
) implements Resolvable<RegistryKeySet<T>> {

    @Override
    public @Nullable RegistryKeySet<T> resolve(@NotNull BuildContext context) {
        List<TypedKey<T>> resolved = new ArrayList<>();
        for (Resolvable<TypedKey<T>> key : this.keys) {
            TypedKey<T> resolvedKey = key.resolve(context);
            if (resolvedKey != null) {
                resolved.add(resolvedKey);
            }
        }
        if (resolved.isEmpty()) return null;
        return RegistrySet.keySet(this.registryKey, resolved);
    }
}