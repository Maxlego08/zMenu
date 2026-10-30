package fr.maxlego08.menu.storage.redis;

import fr.maxlego08.menu.api.storage.spi.InventoryLogStore;
import fr.maxlego08.menu.api.storage.spi.InventoryOpenRecord;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.params.XAddParams;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * The opened inventories, in the stream {@code <prefix>:open-log}, which keeps about the last
 * {@link #MAX_ENTRIES} entries.
 */
final class RedisInventoryLogStore implements InventoryLogStore {

    private static final long MAX_ENTRIES = 100_000;

    private final RedisConnection connection;

    RedisInventoryLogStore(RedisConnection connection) {
        this.connection = connection;
    }

    @Override
    public @NotNull CompletableFuture<Void> append(@NotNull List<InventoryOpenRecord> records) {
        if (records.isEmpty()) return CompletableFuture.completedFuture(null);
        return this.connection.threads().write(() -> {
            String key = this.connection.key("open-log");
            for (InventoryOpenRecord record : records) {
                this.connection.jedis().xadd(key, XAddParams.xAddParams().id(StreamEntryID.NEW_ENTRY).maxLen(MAX_ENTRIES).approximateTrimming(), Map.of(
                        "player_id", record.playerId().toString(),
                        "plugin", record.plugin(),
                        "inventory", record.inventory(),
                        "page", String.valueOf(record.page()),
                        "old_inventories", String.join(",", record.previousInventories())));
            }
        });
    }
}
