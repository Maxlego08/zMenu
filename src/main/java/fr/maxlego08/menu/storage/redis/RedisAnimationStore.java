package fr.maxlego08.menu.storage.redis;

import fr.maxlego08.menu.api.storage.spi.AnimationStore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * When each player last saw an animation, in the hash {@code <prefix>:anim:<uuid>}.
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
        return this.connection.threads().write(() -> this.connection.jedis().hset(this.key(playerId), animationKey, String.valueOf(playedAt)));
    }

    @Override
    public @NotNull CompletableFuture<Void> clear(@NotNull UUID playerId, @Nullable String animationKey) {
        return this.connection.threads().write(() -> {
            if (animationKey == null) this.connection.jedis().del(this.key(playerId));
            else this.connection.jedis().hdel(this.key(playerId), animationKey);
        });
    }
}
