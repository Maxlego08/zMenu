package fr.maxlego08.menu.storage.redis;

import fr.maxlego08.menu.api.storage.spi.MessageBroker;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Redis used only to carry changes between servers, next to another store
 * ({@code messaging-type: REDIS} with a MySQL or MariaDB {@code storage-type}): changes reach the
 * other servers at once instead of at the next read of the event table.
 */
public final class RedisMessaging {

    private final RedisConnection connection;
    private final RedisMessageBroker broker;

    private RedisMessaging(RedisConnection connection, RedisMessageBroker broker) {
        this.connection = connection;
        this.broker = broker;
    }

    /**
     * Connects with the settings of the {@code redis} section and starts listening.
     *
     * @throws IllegalStateException when the Redis client is missing
     */
    public static @NotNull RedisMessaging open(@NotNull ConfigurationSection configuration, @NotNull String serverId, @NotNull Logger logger) {
        RedisSettings settings = RedisSettings.read(configuration);
        RedisConnection connection;
        try {
            connection = RedisConnection.open(settings, logger);
        } catch (NoClassDefFoundError error) {
            throw new IllegalStateException("The Redis client is missing. zMenu downloads it when it starts with messaging-type REDIS: restart the server.", error);
        }
        RedisMessageBroker broker = connection.broker(settings.channel(), serverId, logger);
        broker.start();
        return new RedisMessaging(connection, broker);
    }

    public @NotNull MessageBroker broker() {
        return this.broker;
    }

    public void close() throws InterruptedException {
        this.broker.stop();
        this.connection.close(10, TimeUnit.SECONDS);
    }
}
