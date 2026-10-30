package fr.maxlego08.menu.storage.redis;

import fr.maxlego08.menu.api.storage.spi.AnimationStore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * When each player last saw an animation, in the hash {@code <prefix>:anim:<uuid>}, and the players
 * having one in {@code <prefix>:anim-players}.
 */
final class RedisAnimationStore implements AnimationStore {

    private final RedisConnection connection;

    RedisAnimationStore(RedisConnection connection) {
        this.connection = connection;
    }

    private String key(UUID playerId) {
        return this.connection.key("anim", playerId.toString());
    }

    @Override
    public @NotNull CompletableFuture<Map<String, Long>> load(@NotNull UUID playerId) {
        return this.connection.threads().read(() -> {
            Map<String, Long> played = new HashMap<>();
            this.connection.jedis().hgetAll(this.key(playerId)).forEach((animation, playedAt) -> played.put(animation, Long.parseLong(playedAt)));
            return played;
        });
    }

    @Override
    public @NotNull CompletableFuture<Void> markPlayed(@NotNull UUID playerId, @NotNull String animationKey, long playedAt) {
        return this.connection.threads().write(() -> {
            this.connection.jedis().hset(this.key(playerId), animationKey, String.valueOf(playedAt));
            this.connection.jedis().sadd(this.connection.key("anim-players"), playerId.toString());
        });
    }

    @Override
    public @NotNull CompletableFuture<Set<UUID>> players() {
        return this.connection.threads().read(() -> {
            Set<UUID> players = new HashSet<>();
            this.connection.jedis().smembers(this.connection.key("anim-players")).forEach(playerId -> players.add(UUID.fromString(playerId)));
            return players;
        });
    }

    @Override
    public @NotNull CompletableFuture<Void> clear(@NotNull UUID playerId, @Nullable String animationKey) {
        return this.connection.threads().write(() -> {
            if (animationKey == null) this.connection.jedis().del(this.key(playerId));
            else this.connection.jedis().hdel(this.key(playerId), animationKey);
        });
    }
}
