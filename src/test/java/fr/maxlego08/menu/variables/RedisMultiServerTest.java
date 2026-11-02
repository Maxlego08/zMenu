package fr.maxlego08.menu.variables;

import com.github.fppt.jedismock.RedisServer;
import fr.maxlego08.menu.api.storage.spi.StorageProvider;
import fr.maxlego08.menu.storage.redis.RedisMessageBroker;
import fr.maxlego08.menu.storage.redis.RedisStorageProvider;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Two servers sharing one in-process Redis (jedis-mock): changes travel on pub/sub.
 */
class RedisMultiServerTest extends TwoServerContractTest {

    private RedisServer redis;

    @Override
    void prepare() throws Exception {
        this.redis = RedisServer.newRedisServer();
        this.redis.start();
    }

    @Override
    void cleanup() throws Exception {
        this.redis.stop();
    }

    @Override
    StorageProvider newProvider() {
        return new RedisStorageProvider();
    }

    @Override
    YamlConfiguration configuration() {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("storage-type", "REDIS");
        configuration.set("redis.uri", "redis://" + this.redis.getHost() + ":" + this.redis.getBindPort() + "/0");
        return configuration;
    }

    @Override
    void awaitBrokers() throws Exception {
        awaitSubscriptions(this.providerA, this.providerB);
    }

    static void awaitSubscriptions(StorageProvider... providers) throws InterruptedException {
        eventually(() -> {
            for (StorageProvider provider : providers) {
                if (!((RedisMessageBroker) provider.broker().orElseThrow()).isSubscribed()) return false;
            }
            return true;
        });
    }
}
