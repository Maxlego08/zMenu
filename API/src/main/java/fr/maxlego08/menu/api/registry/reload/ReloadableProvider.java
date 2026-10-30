package fr.maxlego08.menu.api.registry.reload;

import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

/**
 * Contributes entries to a reloadable registry, typically by reading files. It runs on every
 * reload, so it must not keep state between two runs.
 *
 * @param <T> the type of the entries
 */
@FunctionalInterface
public interface ReloadableProvider<T extends Keyed> {

    void provide(@NotNull ReloadContext<T> context);
}
