package fr.maxlego08.menu.registry;

import fr.maxlego08.menu.api.registry.reload.ReloadContext;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

/**
 * Changes a copy of the live snapshot of a reloadable registry, for
 * {@link ZRegistryManager#update}. Unlike a full reload, the entries already there stay unless
 * they are removed.
 *
 * @param <T> the type of the entries
 */
public interface SnapshotEditor<T extends Keyed> extends ReloadContext<T> {

    /**
     * Removes an entry from the copy.
     *
     * @return whether it was there
     */
    boolean remove(@NotNull Key key);
}
