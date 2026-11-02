package fr.maxlego08.menu.storage.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import fr.maxlego08.menu.api.storage.spi.SavedInventoryStore;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The inventories saved on this server, in the {@code saved_inventories} collection:
 * {@code {_id: <server>/<player>, server, player, inventory}}.
 */
final class MongoSavedInventoryStore implements SavedInventoryStore {

    private final MongoConnection connection;
    private final String serverId;

    MongoSavedInventoryStore(MongoConnection connection, String serverId) {
        this.connection = connection;
        this.serverId = serverId;
    }

    private MongoCollection<Document> collection() {
        return this.connection.collection("saved_inventories");
    }

    private String id(UUID playerId) {
        return this.serverId + "/" + playerId;
    }

    @Override
    public @NotNull CompletableFuture<Map<UUID, String>> loadAll() {
        return this.connection.threads().read(() -> {
            Map<UUID, String> inventories = new HashMap<>();
            for (Document document : this.collection().find(Filters.eq("server", this.serverId))) {
                inventories.put(UUID.fromString(document.getString("player")), document.getString("inventory"));
            }
            return inventories;
        });
    }

    @Override
    public @NotNull CompletableFuture<Optional<String>> load(@NotNull UUID playerId) {
        return this.connection.threads().read(() -> Optional.ofNullable(this.collection().find(Filters.eq("_id", this.id(playerId))).first())
                .map(document -> document.getString("inventory")));
    }

    @Override
    public @NotNull CompletableFuture<Void> save(@NotNull UUID playerId, @NotNull String inventory) {
        Document document = new Document("_id", this.id(playerId)).append("server", this.serverId).append("player", playerId.toString()).append("inventory", inventory);
        return this.connection.threads().write(() -> this.collection().replaceOne(Filters.eq("_id", this.id(playerId)), document, new ReplaceOptions().upsert(true)));
    }

    @Override
    public @NotNull CompletableFuture<Void> delete(@NotNull UUID playerId) {
        return this.connection.threads().write(() -> this.collection().deleteOne(Filters.eq("_id", this.id(playerId))));
    }
}
