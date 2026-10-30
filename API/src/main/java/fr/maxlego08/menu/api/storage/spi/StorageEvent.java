package fr.maxlego08.menu.api.storage.spi;

import fr.maxlego08.menu.api.variable.VariableRef;
import fr.maxlego08.menu.api.variable.VariableScope;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * A change one server tells the others about through a {@link MessageBroker}.
 *
 * <p>Events say what changed, never the new value: a server that receives one reads the value
 * again from the store. A lost event can only leave a value out of date until its next change,
 * never wrong.</p>
 */
@ApiStatus.Experimental
public sealed interface StorageEvent {

    /**
     * @return the {@code server-id} of the server that made the change
     */
    @NotNull
    String origin();

    record VariableChanged(@NotNull String origin, @NotNull VariableRef ref) implements StorageEvent {
    }

    record NameCleared(@NotNull String origin, @NotNull VariableScope scope, @NotNull String name) implements StorageEvent {
    }

    record OwnerCleared(@NotNull String origin, @NotNull VariableScope scope, @NotNull String owner) implements StorageEvent {
    }

    record ScopeCleared(@NotNull String origin, @NotNull VariableScope scope) implements StorageEvent {
    }

    /**
     * A player left a server and everything they changed there is stored, so the server they join
     * next can read their variables.
     */
    record SessionReleased(@NotNull String origin, @NotNull UUID playerId) implements StorageEvent {
    }
}
