package fr.maxlego08.menu.storage.redis;

import fr.maxlego08.menu.api.storage.spi.SavedInventoryStore;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The inventories saved on this server, in the hash {@code <prefix>:inv:<server-id>}.
 */
final class RedisSavedInventoryStore implements SavedInventoryStore {

    private final RedisConnection connection;
    private final String key;

    RedisSavedInventoryStore(RedisConnection connection, String serverId) {
        this.connection = connection;
        this.key = connection.key("inv", serverId);
    }

    @Override
    public @NotNull CompletableFuture<Map<UUID, String>> loadAll() {
        return this.connection.threads().read(() -> {
            Map<UUID, String> inventories = new HashMap<>();
            this.connection.jedis().hgetAll(this.key).forEach((playerId, inventory) -> inventories.put(UUID.fromString(playerId), inventory));
            return inventories;
        });
    }

    @Override
    public @NotNull CompletableFuture<Optional<String>> load(@NotNull UUID playerId) {
        return this.connection.threads().read(() -> Optional.ofNullable(this.connection.jedis().hget(this.key, playerId.toString())));
    }

    @Override
    public @NotNull CompletableFuture<Void> save(@NotNull UUID playerId, @NotNull String inventory) {
        return this.connection.threads().write(() -> this.connection.jedis().hset(this.key, playerId.toString(), inventory));
    }

    @Override
    public @NotNull CompletableFuture<Void> delete(@NotNull UUID playerId) {
        return this.connection.threads().write(() -> this.connection.jedis().hdel(this.key, playerId.toString()));
    }
}
