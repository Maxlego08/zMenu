package fr.maxlego08.menu.api.registry.reload;

import fr.maxlego08.menu.api.registry.KeyedRegistry;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.ApiStatus;

/**
 * A registry rebuilt from its providers on every reload. Reads always see one complete snapshot.
 *
 * @param <T> the type of the entries
 */
@ApiStatus.NonExtendable
public interface ReloadableRegistry<T extends Keyed> extends KeyedRegistry<T> {

    /**
     * @return how many snapshots were published, so that a cache can tell it is stale
     */
    long generation();
}
