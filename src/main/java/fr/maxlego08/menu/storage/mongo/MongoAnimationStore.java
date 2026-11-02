package fr.maxlego08.menu.storage.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import fr.maxlego08.menu.api.storage.spi.AnimationStore;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * When each player last saw an animation, in the {@code animations} collection, one document per
 * player and animation: {@code {_id, player, animation, playedAt}}. Animation keys can hold dots,
 * which MongoDB field names cannot, so they are values rather than fields.
 */
final class MongoAnimationStore implements AnimationStore {

    private final MongoConnection connection;

    MongoAnimationStore(MongoConnection connection) {
        this.connection = connection;
    }

    private MongoCollection<Document> collection() {
        return this.connection.collection("animations");
    }

    @Override
    public @NotNull CompletableFuture<Map<String, Long>> load(@NotNull UUID playerId) {
        return this.connection.threads().read(() -> {
            Map<String, Long> played = new HashMap<>();
            for (Document document : this.collection().find(Filters.eq("player", playerId.toString()))) {
                Object playedAt = document.get("playedAt");
                played.put(document.getString("animation"), playedAt instanceof Number number ? number.longValue() : 0L);
            }
            return played;
        });
    }

    @Override
    public @NotNull CompletableFuture<Void> markPlayed(@NotNull UUID playerId, @NotNull String animationKey, long playedAt) {
        String id = playerId + "\u0000" + animationKey;
        Document document = new Document("_id", id).append("player", playerId.toString()).append("animation", animationKey).append("playedAt", playedAt);
        return this.connection.threads().write(() -> this.collection().replaceOne(Filters.eq("_id", id), document, new ReplaceOptions().upsert(true)));
    }

    @Override
    public @NotNull CompletableFuture<Void> clear(@NotNull UUID playerId, @Nullable String animationKey) {
        return this.connection.threads().write(() -> {
            if (animationKey == null) this.collection().deleteMany(Filters.eq("player", playerId.toString()));
            else this.collection().deleteOne(Filters.eq("_id", playerId + "\u0000" + animationKey));
        });
    }

    @Override
    public @NotNull CompletableFuture<Set<UUID>> players() {
        return this.connection.threads().read(() -> {
            Set<String> players = new HashSet<>();
            this.collection().distinct("player", String.class).into(players);
            Set<UUID> uuids = new HashSet<>();
            players.forEach(player -> uuids.add(UUID.fromString(player)));
            return uuids;
        });
    }
}
