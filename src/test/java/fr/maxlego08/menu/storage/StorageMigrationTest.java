package fr.maxlego08.menu.storage;

import com.github.fppt.jedismock.RedisServer;
import de.bwaldvogel.mongo.MongoServer;
import fr.maxlego08.menu.api.storage.spi.StorageContext;
import fr.maxlego08.menu.api.storage.spi.StorageProvider;
import fr.maxlego08.menu.api.storage.spi.VariableMutation;
import fr.maxlego08.menu.api.variable.Variable;
import fr.maxlego08.menu.api.variable.VariableRef;
import fr.maxlego08.menu.api.variable.VariableScope;
import fr.maxlego08.menu.storage.memory.MemoryStorageProvider;
import fr.maxlego08.menu.storage.mongo.MongoStorageProvider;
import fr.maxlego08.menu.storage.redis.RedisStorageProvider;
import fr.maxlego08.menu.storage.sql.SqlStorageProvider;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@code /zm storage migrate} copies everything from one backend to another: SQLite to Redis, Redis
 * to MongoDB, then MongoDB to memory, without losing or changing anything.
 */
class StorageMigrationTest {

    private static final UUID PLAYER = UUID.fromString("00000000-0000-0000-0000-00000000000a");

    @TempDir
    File folder;

    private StorageProvider open(StorageProvider provider, ConfigurationSection configuration) {
        provider.open(new StorageContext(null, configuration, this.folder, Logger.getLogger("zMenu-test"), "lobby")).join();
        return provider;
    }

    private static Map<String, String> snapshot(StorageProvider provider) {
        Map<String, String> values = new TreeMap<>();
        for (VariableScope scope : VariableScope.values()) {
            for (String owner : provider.variables().owners(scope).join()) {
                for (Variable variable : provider.variables().loadOwner(scope, owner).join()) {
                    values.put(scope.id() + "/" + owner + "/" + variable.ref().name(), variable.text() + "@" + variable.expiresAt());
                }
            }
        }
        provider.savedInventories().loadAll().join().forEach((playerId, inventory) -> values.put("inventory/" + playerId, inventory));
        for (UUID playerId : provider.worldVisits().players().join()) {
            values.put("worlds/" + playerId, new TreeMap<>(Map.of("worlds", provider.worldVisits().load(playerId).join().toString())).toString());
        }
        for (UUID playerId : provider.animations().players().join()) {
            values.put("animations/" + playerId, new TreeMap<>(provider.animations().load(playerId).join()).toString());
        }
        return values;
    }

    @Test
    void everythingIsCopiedThroughEveryBackend() throws Exception {
        YamlConfiguration sqliteConfiguration = new YamlConfiguration();
        sqliteConfiguration.set("storage-type", "SQLITE");
        StorageProvider sqlite = this.open(new SqlStorageProvider(), sqliteConfiguration);

        List<VariableMutation> mutations = new ArrayList<>();
        for (int index = 0; index < 1200; index++) {
            mutations.add(new VariableMutation.Set(Variable.of(VariableRef.player(UUID.randomUUID(), "coins"), String.valueOf(index), 0)));
        }
        mutations.add(new VariableMutation.Set(Variable.of(VariableRef.player(PLAYER, "rank"), "vip", 4_102_444_800_000L)));
        mutations.add(new VariableMutation.Set(Variable.of(VariableRef.player(PLAYER, "balance"), "12.75", 0)));
        mutations.add(new VariableMutation.Set(Variable.of(VariableRef.global("event"), "started", 0)));
        mutations.add(new VariableMutation.Set(Variable.of(VariableRef.server("lobby", "motd"), "hello", 0)));
        sqlite.variables().apply(mutations).join();
        sqlite.savedInventories().save(PLAYER, "0:abc;8:def").join();
        sqlite.worldVisits().markVisited(PLAYER, "world").join();
        sqlite.worldVisits().markVisited(PLAYER, "world_nether").join();
        sqlite.animations().markPlayed(PLAYER, "shop:open", 1234L).join();

        RedisServer redisServer = RedisServer.newRedisServer();
        redisServer.start();
        MongoServer mongoServer = MongoTestServers.newMongoServer();
        InetSocketAddress mongoAddress = mongoServer.bind();
        try {
            YamlConfiguration redisConfiguration = RedisStorageProviderTest.redisConfiguration(redisServer);
            redisConfiguration.set("messaging-type", "NONE");
            YamlConfiguration mongoConfiguration = MongoTestServers.mongoConfiguration(mongoAddress);
            mongoConfiguration.set("messaging-type", "NONE");
            StorageProvider redis = this.open(new RedisStorageProvider(), redisConfiguration);
            StorageProvider mongo = this.open(new MongoStorageProvider(), mongoConfiguration);
            StorageProvider memory = this.open(new MemoryStorageProvider(), new YamlConfiguration());

            StorageMigration.Result toRedis = new StorageMigration(sqlite, redis, step -> {
            }).run();
            new StorageMigration(redis, mongo, step -> {
            }).run();
            redis.close().join();
            new StorageMigration(mongo, memory, step -> {
            }).run();
            mongo.close().join();

            assertEquals(1204, toRedis.variables());
            assertEquals(1, toRedis.inventories());
            assertEquals(2, toRedis.worlds());
            assertEquals(1, toRedis.animations());

            Map<String, String> original = snapshot(sqlite);
            assertEquals(original, snapshot(this.open(new RedisStorageProvider(), redisConfiguration)));
            assertEquals(original, snapshot(this.open(new MongoStorageProvider(), mongoConfiguration)));
            assertEquals(original, snapshot(memory));
            assertEquals(Set.of("coins", "rank", "balance"), memory.variables().names(VariableScope.PLAYER).join());
        } finally {
            sqlite.close().join();
            redisServer.stop();
            mongoServer.shutdownNow();
        }
    }
}
