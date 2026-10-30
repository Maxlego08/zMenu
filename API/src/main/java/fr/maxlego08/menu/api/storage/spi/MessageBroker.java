package fr.maxlego08.menu.api.storage.spi;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Carries {@link StorageEvent}s between the servers that share a store.
 *
 * <p>A provider offers its broker through {@link StorageProvider#broker()}; it is opened and
 * closed with the provider.</p>
 */
@ApiStatus.Experimental
public interface MessageBroker {

    /**
     * Sends events to the other servers. They are sent after every write made before this call,
     * so a server receiving an event reads the new value.
     */
    @NotNull
    CompletableFuture<Void> publish(@NotNull List<StorageEvent> events);

    /**
     * Receives the events of the other servers, on a background thread. Events of this server are
     * not delivered.
     */
    void subscribe(@NotNull Consumer<StorageEvent> listener);
}
