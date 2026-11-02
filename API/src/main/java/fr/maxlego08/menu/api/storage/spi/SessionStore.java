package fr.maxlego08.menu.api.storage.spi;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Which server each player is on, and which servers are running, for stores several servers
 * share.
 *
 * <p>When a player moves from one server to another, the new server waits for the old one to
 * release the player, which happens once everything the player changed there is stored.</p>
 */
@ApiStatus.Experimental
public interface SessionStore {

    @NotNull
    CompletableFuture<Optional<PlayerSession>> load(@NotNull UUID playerId);

    /**
     * Records that a player is online on a server.
     */
    @NotNull
    CompletableFuture<Void> claim(@NotNull UUID playerId, @NotNull String serverId, long now);

    /**
     * Records that a player left a server, unless another server claimed them since.
     */
    @NotNull
    CompletableFuture<Void> release(@NotNull UUID playerId, @NotNull String serverId);

    @NotNull
    CompletableFuture<Optional<ServerInstance>> loadServer(@NotNull String serverId);

    /**
     * Records that a server is running, and refreshes the sessions of its online players.
     *
     * @param instanceId identifies this run of the server, to tell two servers using the same id
     *                   apart
     */
    @NotNull
    CompletableFuture<Void> heartbeat(@NotNull String serverId, @NotNull String instanceId, long now);

    /**
     * @param playerId  the player
     * @param serverId  the server that last claimed them
     * @param online    whether they are still on it
     * @param heartbeat when that server last said it was running, in milliseconds since the epoch
     */
    record PlayerSession(@NotNull UUID playerId, @NotNull String serverId, boolean online, long heartbeat) {
    }

    /**
     * @param serverId   the {@code server-id}
     * @param instanceId the run of the server that last sent a heartbeat
     * @param heartbeat  when, in milliseconds since the epoch
     */
    record ServerInstance(@NotNull String serverId, @NotNull String instanceId, long heartbeat) {
    }
}
