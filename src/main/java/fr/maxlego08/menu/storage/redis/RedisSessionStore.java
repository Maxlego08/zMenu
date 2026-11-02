package fr.maxlego08.menu.storage.redis;

import fr.maxlego08.menu.api.storage.spi.SessionStore;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Sessions in Redis: the hash {@code <prefix>:session:<uuid>} per player, the hash
 * {@code <prefix>:server:<server-id>} per server, and the set {@code <prefix>:server-players:<server-id>}
 * of the players online on each server, refreshed by its heartbeat.
 */
final class RedisSessionStore implements SessionStore {

    /**
     * Marks the player offline only if this server still holds them.
     */
    private static final String RELEASE = """
            if redis.call('HGET', KEYS[1], 'server') == ARGV[1] then
                redis.call('HSET', KEYS[1], 'online', '0')
            end
            return 1
            """;

    /**
     * Refreshes a session only if this server still holds it.
     */
    private static final String REFRESH = """
            if redis.call('HGET', KEYS[1], 'server') == ARGV[1] then
                redis.call('HSET', KEYS[1], 'heartbeat', ARGV[2])
                return 1
            end
            return 0
            """;

    private final RedisConnection connection;

    RedisSessionStore(RedisConnection connection) {
        this.connection = connection;
    }

    private String sessionKey(UUID playerId) {
        return this.connection.key("session", playerId.toString());
    }

    private String serverKey(String serverId) {
        return this.connection.key("server", serverId);
    }

    private String playersKey(String serverId) {
        return this.connection.key("server-players", serverId);
    }

    private static long number(String value) {
        return value == null ? 0 : Long.parseLong(value);
    }

    @Override
    public @NotNull CompletableFuture<Optional<PlayerSession>> load(@NotNull UUID playerId) {
        return this.connection.threads().read(() -> {
            Map<String, String> session = this.connection.jedis().hgetAll(this.sessionKey(playerId));
            if (session.isEmpty() || session.get("server") == null) return Optional.empty();
            return Optional.of(new PlayerSession(playerId, session.get("server"), "1".equals(session.get("online")), number(session.get("heartbeat"))));
        });
    }

    @Override
    public @NotNull CompletableFuture<Void> claim(@NotNull UUID playerId, @NotNull String serverId, long now) {
        return this.connection.threads().write(() -> {
            this.connection.jedis().hset(this.sessionKey(playerId), Map.of("server", serverId, "online", "1", "heartbeat", String.valueOf(now)));
            this.connection.jedis().sadd(this.playersKey(serverId), playerId.toString());
        });
    }

    @Override
    public @NotNull CompletableFuture<Void> release(@NotNull UUID playerId, @NotNull String serverId) {
        return this.connection.threads().write(() -> {
            this.connection.jedis().eval(RELEASE, List.of(this.sessionKey(playerId)), List.of(serverId));
            this.connection.jedis().srem(this.playersKey(serverId), playerId.toString());
        });
    }

    @Override
    public @NotNull CompletableFuture<Optional<ServerInstance>> loadServer(@NotNull String serverId) {
        return this.connection.threads().read(() -> {
            Map<String, String> server = this.connection.jedis().hgetAll(this.serverKey(serverId));
            if (server.isEmpty() || server.get("instance") == null) return Optional.empty();
            return Optional.of(new ServerInstance(serverId, server.get("instance"), number(server.get("heartbeat"))));
        });
    }

    @Override
    public @NotNull CompletableFuture<Void> heartbeat(@NotNull String serverId, @NotNull String instanceId, long now) {
        return this.connection.threads().write(() -> {
            this.connection.jedis().hset(this.serverKey(serverId), Map.of("instance", instanceId, "heartbeat", String.valueOf(now)));
            for (String playerId : this.connection.jedis().smembers(this.playersKey(serverId))) {
                Object refreshed = this.connection.jedis().eval(REFRESH, List.of(this.sessionKey(UUID.fromString(playerId))), List.of(serverId, String.valueOf(now)));
                if (refreshed instanceof Long value && value == 0) this.connection.jedis().srem(this.playersKey(serverId), playerId);
            }
        });
    }
}
