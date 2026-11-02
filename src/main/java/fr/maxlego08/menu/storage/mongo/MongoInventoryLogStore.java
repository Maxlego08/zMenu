package fr.maxlego08.menu.storage.mongo;

import fr.maxlego08.menu.api.storage.spi.InventoryLogStore;
import fr.maxlego08.menu.api.storage.spi.InventoryOpenRecord;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The opened inventories, in the {@code open_inventories} collection.
 */
final class MongoInventoryLogStore implements InventoryLogStore {

    private final MongoConnection connection;

    MongoInventoryLogStore(MongoConnection connection) {
        this.connection = connection;
    }

    @Override
    public @NotNull CompletableFuture<Void> append(@NotNull List<InventoryOpenRecord> records) {
        if (records.isEmpty()) return CompletableFuture.completedFuture(null);
        long now = System.currentTimeMillis();
        List<Document> documents = new ArrayList<>(records.size());
        for (InventoryOpenRecord record : records) {
            documents.add(new Document("player", record.playerId().toString())
                    .append("plugin", record.plugin())
                    .append("inventory", record.inventory())
                    .append("page", record.page())
                    .append("previousInventories", record.previousInventories())
                    .append("openedAt", now));
        }
        return this.connection.threads().write(() -> this.connection.collection("open_inventories").insertMany(documents));
    }
}
