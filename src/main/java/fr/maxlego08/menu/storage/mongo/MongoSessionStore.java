package fr.maxlego08.menu.storage.mongo;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Updates;
import fr.maxlego08.menu.api.storage.spi.SessionStore;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Sessions in the {@code sessions} collection ({@code {_id: <player>, server, online, heartbeat}})
 * and the {@code servers} collection ({@code {_id: <server>, instance, heartbeat}}).
 */
final class MongoSessionStore implements SessionStore {

    private final MongoConnection connection;

    MongoSessionStore(MongoConnection connection) {
        this.connection = connection;
    }

    private static long number(Object value) {
        return value instanceof Number number ? number.longValue() : 0;
    }

    @Override
    public @NotNull CompletableFuture<Optional<PlayerSession>> load(@NotNull UUID playerId) {
        return this.connection.threads().read(() -> Optional.ofNullable(this.connection.collection("sessions").find(Filters.eq("_id", playerId.toString())).first())
                .map(document -> new PlayerSession(playerId, document.getString("server"), Boolean.TRUE.equals(document.getBoolean("online")), number(document.get("heartbeat")))));
    }

    @Override
    public @NotNull CompletableFuture<Void> claim(@NotNull UUID playerId, @NotNull String serverId, long now) {
        Document document = new Document("_id", playerId.toString()).append("server", serverId).append("online", true).append("heartbeat", now);
        return this.connection.threads().write(() -> this.connection.collection("sessions").replaceOne(Filters.eq("_id", playerId.toString()), document, new ReplaceOptions().upsert(true)));
    }

    @Override
    public @NotNull CompletableFuture<Void> release(@NotNull UUID playerId, @NotNull String serverId) {
        return this.connection.threads().write(() -> this.connection.collection("sessions").updateOne(
                Filters.and(Filters.eq("_id", playerId.toString()), Filters.eq("server", serverId)), Updates.set("online", false)));
    }

    @Override
    public @NotNull CompletableFuture<Optional<ServerInstance>> loadServer(@NotNull String serverId) {
        return this.connection.threads().read(() -> Optional.ofNullable(this.connection.collection("servers").find(Filters.eq("_id", serverId)).first())
                .map(document -> new ServerInstance(serverId, document.getString("instance"), number(document.get("heartbeat")))));
    }

    @Override
    public @NotNull CompletableFuture<Void> heartbeat(@NotNull String serverId, @NotNull String instanceId, long now) {
        Document server = new Document("_id", serverId).append("instance", instanceId).append("heartbeat", now);
        return this.connection.threads().write(() -> {
            this.connection.collection("servers").replaceOne(Filters.eq("_id", serverId), server, new ReplaceOptions().upsert(true));
            this.connection.collection("sessions").updateMany(Filters.and(Filters.eq("server", serverId), Filters.eq("online", true)), Updates.set("heartbeat", now));
        });
    }
}
