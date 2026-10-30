package fr.maxlego08.menu.storage.redis;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code redis} section of {@code config.yml}.
 *
 * @param uri        where Redis is, such as {@code redis://user:password@localhost:6379/0}; use
 *                   {@code rediss://} for TLS
 * @param cluster    whether it is a Redis Cluster, reached through the node in {@code uri}
 * @param keyPrefix  the start of every key zMenu writes
 * @param requireAof whether to refuse a Redis that does not write its data to disk (append only file)
 * @param channel    the pub/sub channel servers send their changes on
 */
public record RedisSettings(@NotNull String uri, boolean cluster, @NotNull String keyPrefix, boolean requireAof, @NotNull String channel) {

    public static @NotNull RedisSettings read(@NotNull ConfigurationSection configuration) {
        String prefix = configuration.getString("redis.key-prefix", "zmenu");
        return new RedisSettings(
                configuration.getString("redis.uri", "redis://localhost:6379/0"),
                configuration.getBoolean("redis.cluster", false),
                prefix,
                configuration.getBoolean("redis.require-aof", false),
                prefix + ":events"
        );
    }
}
