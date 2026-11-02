package fr.maxlego08.menu.storage;

import fr.maxlego08.menu.api.storage.spi.StorageContext;
import fr.maxlego08.menu.api.storage.spi.StorageProvider;
import fr.maxlego08.menu.api.storage.spi.VariableMutation;
import fr.maxlego08.menu.api.variable.Variable;
import fr.maxlego08.menu.api.variable.VariableRef;
import fr.maxlego08.menu.storage.memory.MemoryStorageProvider;
import fr.maxlego08.menu.storage.mongo.MongoStorageProvider;
import fr.maxlego08.menu.storage.redis.RedisStorageProvider;
import fr.maxlego08.menu.storage.sql.SqlStorageProvider;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The storage contract on real database servers, each run only when {@link RealDatabases} names
 * its server.
 */
class RealStorageProviderTest {

    @Nested
    @EnabledIfSystemProperty(named = RealDatabases.MYSQL, matches = ".+")
    class MySql extends StorageProviderContractTest {

        @Override
        StorageProvider createProvider() {
            try {
                RealDatabases.dropSqlTables("MYSQL", RealDatabases.MYSQL);
            } catch (Exception exception) {
                throw new IllegalStateException(exception);
            }
            return new SqlStorageProvider();
        }

        @Override
        YamlConfiguration configuration() {
            return RealDatabases.sql("MYSQL", RealDatabases.MYSQL);
        }
    }

    @Nested
    @EnabledIfSystemProperty(named = RealDatabases.MARIADB, matches = ".+")
    class MariaDb extends StorageProviderContractTest {

        @Override
        StorageProvider createProvider() {
            try {
                RealDatabases.dropSqlTables("MARIADB", RealDatabases.MARIADB);
            } catch (Exception exception) {
                throw new IllegalStateException(exception);
            }
            return new SqlStorageProvider();
        }

        @Override
        YamlConfiguration configuration() {
            return RealDatabases.sql("MARIADB", RealDatabases.MARIADB);
        }
    }

    @Nested
    @EnabledIfSystemProperty(named = RealDatabases.POSTGRESQL, matches = ".+")
    class PostgreSql extends StorageProviderContractTest {

        @Override
        StorageProvider createProvider() {
            try {
                RealDatabases.dropSqlTables("POSTGRESQL", RealDatabases.POSTGRESQL);
            } catch (Exception exception) {
                throw new IllegalStateException(exception);
            }
            return new SqlStorageProvider();
        }

        @Override
        YamlConfiguration configuration() {
            return RealDatabases.sql("POSTGRESQL", RealDatabases.POSTGRESQL);
        }
    }

    @Nested
    @EnabledIfSystemProperty(named = RealDatabases.REDIS, matches = ".+")
    class Redis extends StorageProviderContractTest {

        @Override
        StorageProvider createProvider() {
            return new RedisStorageProvider();
        }

        @Override
        YamlConfiguration configuration() {
            return RealDatabases.redis();
        }
    }

    @Nested
    @EnabledIfSystemProperty(named = RealDatabases.MONGODB, matches = ".+")
    class MongoDb extends StorageProviderContractTest {

        @Override
        StorageProvider createProvider() {
            return new MongoStorageProvider();
        }

        @Override
        YamlConfiguration configuration() {
            return RealDatabases.mongodb();
        }
    }

    @TempDir
    File folder;

    private StorageProvider open(StorageProvider provider, ConfigurationSection configuration) {
        configuration.set("messaging-type", "NONE");
        provider.open(new StorageContext(null, configuration, this.folder, Logger.getLogger("zMenu-test"), "lobby")).join();
        return provider;
    }

    /**
     * Copies a storage through every real backend and back into memory, with {@code /zm storage
     * migrate}'s copier.
     */
    @Test
    @EnabledIfSystemProperty(named = RealDatabases.MYSQL, matches = ".+")
    @EnabledIfSystemProperty(named = RealDatabases.MARIADB, matches = ".+")
    @EnabledIfSystemProperty(named = RealDatabases.POSTGRESQL, matches = ".+")
    @EnabledIfSystemProperty(named = RealDatabases.REDIS, matches = ".+")
    @EnabledIfSystemProperty(named = RealDatabases.MONGODB, matches = ".+")
    void everythingIsCopiedThroughEveryRealBackend() throws Exception {
        RealDatabases.dropSqlTables("MYSQL", RealDatabases.MYSQL);
        RealDatabases.dropSqlTables("MARIADB", RealDatabases.MARIADB);
        RealDatabases.dropSqlTables("POSTGRESQL", RealDatabases.POSTGRESQL);

        StorageProvider source = this.open(new MemoryStorageProvider(), new YamlConfiguration());
        UUID player = UUID.randomUUID();
        List<VariableMutation> mutations = new ArrayList<>();
        for (int index = 0; index < 700; index++) {
            mutations.add(new VariableMutation.Set(Variable.of(VariableRef.player(UUID.randomUUID(), "coins"), String.valueOf(index), 0)));
        }
        mutations.add(new VariableMutation.Set(Variable.of(VariableRef.player(player, "rank"), "vip", 4_102_444_800_000L)));
        mutations.add(new VariableMutation.Set(Variable.of(VariableRef.player(player, "balance"), "12.75", 0)));
        mutations.add(new VariableMutation.Set(Variable.of(VariableRef.global("event"), "started", 0)));
        mutations.add(new VariableMutation.Set(Variable.of(VariableRef.server("lobby", "motd"), "hello", 0)));
        source.variables().apply(mutations).join();
        source.savedInventories().save(player, "0:abc").join();
        source.worldVisits().markVisited(player, "world").join();
        source.animations().markPlayed(player, "shop:open", 1234L).join();

        List<StorageProvider> chain = List.of(
                this.open(new SqlStorageProvider(), RealDatabases.sql("MYSQL", RealDatabases.MYSQL)),
                this.open(new SqlStorageProvider(), RealDatabases.sql("MARIADB", RealDatabases.MARIADB)),
                this.open(new SqlStorageProvider(), RealDatabases.sql("POSTGRESQL", RealDatabases.POSTGRESQL)),
                this.open(new RedisStorageProvider(), RealDatabases.redis()),
                this.open(new MongoStorageProvider(), RealDatabases.mongodb()),
                this.open(new MemoryStorageProvider(), new YamlConfiguration()));

        Map<String, String> original = StorageMigrationTest.snapshot(source);
        StorageProvider previous = source;
        for (StorageProvider next : chain) {
            StorageMigration.Result result = new StorageMigration(previous, next, step -> {
            }).run();
            assertEquals(704, result.variables());
            previous.close().join();
            assertEquals(original, StorageMigrationTest.snapshot(next), "after copying into " + next.key().asString());
            previous = next;
        }
        previous.close().join();
    }
}
