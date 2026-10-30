package fr.maxlego08.menu.storage.redis;

import fr.maxlego08.menu.api.storage.spi.WorldVisitStore;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The worlds each player entered, in the set {@code <prefix>:worlds:<uuid>}.
 */
final class RedisWorldVisitStore implements WorldVisitStore {

    private final RedisConnection connection;

    RedisWorldVisitStore(RedisConnection connection) {
        this.connection = connection;
    }

    @Override
    public @NotNull CompletableFuture<Set<String>> load(@NotNull UUID playerId) {
        return this.connection.threads().read(() -> new HashSet<>(this.connection.jedis().smembers(this.connection.key("worlds", playerId.toString()))));
    }

    @Override
    public @NotNull CompletableFuture<Void> markVisited(@NotNull UUID playerId, @NotNull String worldName) {
        return this.connection.threads().write(() -> this.connection.jedis().sadd(this.connection.key("worlds", playerId.toString()), worldName));
    }
}
