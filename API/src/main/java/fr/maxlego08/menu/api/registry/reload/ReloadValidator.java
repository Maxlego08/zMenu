package fr.maxlego08.menu.api.registry.reload;

import fr.maxlego08.menu.api.registry.KeyedRegistry;
import fr.maxlego08.menu.api.registry.RegistryAccess;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

/**
 * Checks a new snapshot once every provider has run, before it replaces the live one. This is the
 * place for checks that need several entries, such as a menu opening another menu.
 *
 * @param <T> the type of the entries
 */
@FunctionalInterface
public interface ReloadValidator<T extends Keyed> {

    /**
     * @param snapshot   the new snapshot
     * @param registries the registries, where the one being reloaded is {@code snapshot}
     * @param reporter   where to report what is wrong; an error rejects the snapshot
     */
    void validate(@NotNull KeyedRegistry<T> snapshot, @NotNull RegistryAccess registries, @NotNull ReloadReporter reporter);
}
