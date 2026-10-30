package fr.maxlego08.menu.storage.redis;

import fr.maxlego08.menu.api.storage.spi.AnimationStore;
import fr.maxlego08.menu.api.storage.spi.InventoryLogStore;
import fr.maxlego08.menu.api.storage.spi.SavedInventoryStore;
import fr.maxlego08.menu.api.storage.spi.SessionStore;
import fr.maxlego08.menu.api.storage.spi.VariableStore;
import fr.maxlego08.menu.api.storage.spi.WorldVisitStore;
import fr.maxlego08.menu.storage.StorageThreads;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.util.JedisURIHelper;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * An open Redis connection, the threads that use it and the stores built on it.
 *
 * <p>Only {@link RedisStorageProvider} and {@code ZStorageManager} create one, and only when Redis
 * is configured: this class needs the Redis client, which zMenu only downloads then.</p>
 */
public final class RedisConnection {

    private final UnifiedJedis jedis;
    private final String prefix;
    private final StorageThreads threads = new StorageThreads("Redis");

    RedisConnection(UnifiedJedis jedis, String prefix) {
        this.jedis = jedis;
        this.prefix = prefix;
    }

    /**
     * Connects to Redis and checks it answers.
     *
     * @throws IllegalStateException when Redis does not write its data to disk and
     *                               {@code redis.require-aof} is on
     */
    public static RedisConnection open(RedisSettings settings, Logger logger) {
        UnifiedJedis jedis = connect(settings);
        jedis.ping();
        RedisConnection connection = new RedisConnection(jedis, settings.keyPrefix());
        connection.checkPersistence(settings, logger);
        return connection;
    }

    private static UnifiedJedis connect(RedisSettings settings) {
        URI uri = URI.create(settings.uri());
        if (!settings.cluster()) return new JedisPooled(uri);

        JedisClientConfig config = DefaultJedisClientConfig.builder()
                .user(JedisURIHelper.getUser(uri))
                .password(JedisURIHelper.getPassword(uri))
                .ssl(JedisURIHelper.isRedisSSLScheme(uri))
                .build();
        return new JedisCluster(Set.of(new HostAndPort(uri.getHost(), uri.getPort() == -1 ? 6379 : uri.getPort())), config);
    }

    /**
     * Warns when Redis keeps its data only in memory: a restart of Redis would then lose every
     * variable. Some hosted Redis refuse {@code CONFIG GET}; the check is skipped there.
     */
    private void checkPersistence(RedisSettings settings, Logger logger) {
        String appendOnly = null;
        try {
            Object reply = this.jedis.sendCommand(Protocol.Command.CONFIG, "GET", "appendonly");
            if (reply instanceof List<?> values && values.size() >= 2) {
                Object value = values.get(1);
                appendOnly = value instanceof byte[] bytes ? new String(bytes, StandardCharsets.UTF_8) : String.valueOf(value);
            }
        } catch (RuntimeException exception) {
            return;
        }
        if (appendOnly == null || appendOnly.equalsIgnoreCase("yes")) return;

        String message = "Redis does not write its data to an append only file (appendonly no): a restart of Redis can lose zMenu's data. Set appendonly yes in redis.conf.";
        if (settings.requireAof()) throw new IllegalStateException(message);
        logger.warning(message);
    }

    UnifiedJedis jedis() {
        return this.jedis;
    }

    StorageThreads threads() {
        return this.threads;
    }

    /**
     * @return {@code <prefix>:<part>:<part>...}
     */
    String key(String... parts) {
        return this.prefix + ":" + String.join(":", parts);
    }

    public VariableStore variables() {
        return new RedisVariableStore(this);
    }

    public SavedInventoryStore savedInventories(String serverId) {
        return new RedisSavedInventoryStore(this, serverId);
    }

    public WorldVisitStore worldVisits() {
        return new RedisWorldVisitStore(this);
    }

    public AnimationStore animations() {
        return new RedisAnimationStore(this);
    }

    public InventoryLogStore inventoryLogs() {
        return new RedisInventoryLogStore(this);
    }

    public SessionStore sessions() {
        return new RedisSessionStore(this);
    }

    /**
     * @return a broker on this connection's pub/sub; {@link RedisMessageBroker#start()} must be
     * called once listeners are added
     */
    public RedisMessageBroker broker(String channel, String serverId, Logger logger) {
        return new RedisMessageBroker(this, channel, serverId, logger);
    }

    /**
     * Waits for the queued writes, then closes the connection.
     *
     * @return whether every write finished before the timeout
     */
    public boolean close(long timeout, TimeUnit unit) throws InterruptedException {
        boolean finished = this.threads.close(timeout, unit);
        this.jedis.close();
        return finished;
    }
}
