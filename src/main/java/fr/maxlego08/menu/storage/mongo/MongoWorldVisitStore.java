package fr.maxlego08.menu.storage.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import fr.maxlego08.menu.api.storage.spi.WorldVisitStore;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The worlds each player entered, in the {@code world_visits} collection:
 * {@code {_id: <player>, worlds: [...]}}.
 */
final class MongoWorldVisitStore implements WorldVisitStore {

    private final MongoConnection connection;

    MongoWorldVisitStore(MongoConnection connection) {
        this.connection = connection;
    }

    private MongoCollection<Document> collection() {
        return this.connection.collection("world_visits");
    }

    @Override
    public @NotNull CompletableFuture<Set<String>> load(@NotNull UUID playerId) {
        return this.connection.threads().read(() -> {
            Document document = this.collection().find(Filters.eq("_id", playerId.toString())).first();
            List<String> worlds = document == null ? null : document.getList("worlds", String.class);
            return worlds == null ? new HashSet<>() : new HashSet<>(worlds);
        });
    }

    @Override
    public @NotNull CompletableFuture<Void> markVisited(@NotNull UUID playerId, @NotNull String worldName) {
        return this.connection.threads().write(() -> this.collection().updateOne(Filters.eq("_id", playerId.toString()), Updates.addToSet("worlds", worldName), new UpdateOptions().upsert(true)));
    }

    @Override
    public @NotNull CompletableFuture<Set<UUID>> players() {
        return this.connection.threads().read(() -> {
            Set<UUID> players = new HashSet<>();
            for (Document document : this.collection().find()) {
                players.add(UUID.fromString(document.getString("_id")));
            }
            return players;
        });
    }
}
