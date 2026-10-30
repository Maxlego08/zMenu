package fr.maxlego08.menu.storage;

import com.github.fppt.jedismock.RedisServer;
import fr.maxlego08.menu.api.storage.spi.StorageCapability;
import fr.maxlego08.menu.api.storage.spi.StorageProvider;
import fr.maxlego08.menu.storage.redis.RedisStorageProvider;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.EnumSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The storage contract on Redis, served by an in-process Redis (jedis-mock). Each test uses its own
 * key prefix.
 */
class RedisStorageProviderTest extends StorageProviderContractTest {

    private static RedisServer server;

    @BeforeAll
    static void startRedis() throws IOException {
        server = RedisServer.newRedisServer();
        server.start();
    }

    @AfterAll
    static void stopRedis() throws IOException {
        server.stop();
    }

    static YamlConfiguration redisConfiguration(RedisServer server) {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("storage-type", "REDIS");
        configuration.set("redis.uri", "redis://" + server.getHost() + ":" + server.getBindPort() + "/0");
        configuration.set("redis.key-prefix", "test-" + UUID.randomUUID());
        return configuration;
    }

    @Override
    StorageProvider createProvider() {
        return new RedisStorageProvider();
    }

    @Override
    YamlConfiguration configuration() {
        return redisConfiguration(server);
    }

    @Test
    void redisIsSharedAndAddsAtomically() {
        assertEquals(EnumSet.of(StorageCapability.PERSISTENT, StorageCapability.SHARED, StorageCapability.ATOMIC_INCREMENT), this.provider.capabilities());
        assertTrue(this.provider.broker().isPresent());
        assertTrue(this.provider.sessions().isPresent());
    }
}
