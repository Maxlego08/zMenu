package fr.maxlego08.menu.api.storage.spi;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The worlds each player has entered, for the items given on the first visit of a world.
 */
@ApiStatus.Experimental
public interface WorldVisitStore {

    @NotNull
    CompletableFuture<Set<String>> load(@NotNull UUID playerId);

    @NotNull
    CompletableFuture<Void> markVisited(@NotNull UUID playerId, @NotNull String worldName);

    /**
     * @return every player who visited a world, for {@link StorageCapability#EXPORT}
     */
    @NotNull
    default CompletableFuture<Set<UUID>> players() {
        return CompletableFuture.failedFuture(new UnsupportedOperationException("This store cannot list its players"));
    }
}
