package fr.maxlego08.menu.api.storage.spi;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The values of player data ({@code /zm players}, the {@code data} action, {@code %zmenu_player_value_<key>%}).
 */
@ApiStatus.Experimental
public interface PlayerDataStore {

    /**
     * @return every value of every player
     */
    @NotNull
    CompletableFuture<List<PlayerDataEntry>> loadAll();

    /**
     * @return the values of one player
     */
    @NotNull
    CompletableFuture<List<PlayerDataEntry>> load(@NotNull UUID playerId);

    /**
     * @return every key used by at least one player
     */
    @NotNull
    CompletableFuture<Set<String>> keys();

    /**
     * Creates or replaces values, matched by player and key.
     */
    @NotNull
    CompletableFuture<Void> save(@NotNull List<PlayerDataEntry> entries);

    @NotNull
    CompletableFuture<Void> delete(@NotNull UUID playerId, @NotNull String key);

    /**
     * Deletes every value of a player.
     */
    @NotNull
    CompletableFuture<Void> deletePlayer(@NotNull UUID playerId);

    /**
     * Deletes a key for every player.
     */
    @NotNull
    CompletableFuture<Void> deleteKey(@NotNull String key);

    /**
     * Deletes every value of every player.
     */
    @NotNull
    CompletableFuture<Void> deleteAll();

    /**
     * Deletes the values that expired before a time. Values that never expire are kept.
     *
     * @param now the time, in milliseconds since the epoch
     */
    @NotNull
    CompletableFuture<Void> deleteExpired(long now);
}
