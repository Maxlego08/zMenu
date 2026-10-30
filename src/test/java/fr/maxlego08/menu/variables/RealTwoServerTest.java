package fr.maxlego08.menu.variables;

import fr.maxlego08.menu.api.storage.spi.StorageProvider;
import fr.maxlego08.menu.storage.RealDatabases;
import fr.maxlego08.menu.storage.mongo.MongoStorageProvider;
import fr.maxlego08.menu.storage.redis.RedisStorageProvider;
import fr.maxlego08.menu.storage.sql.SqlStorageProvider;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

/**
 * Two servers on real database servers, each run only when {@link RealDatabases} names its server.
 */
class RealTwoServerTest {

    abstract static class Sql extends TwoServerContractTest {

        abstract String storageType();

        abstract String property();

        @Override
        void prepare() throws Exception {
            RealDatabases.dropSqlTables(this.storageType(), this.property());
        }

        @Override
        StorageProvider newProvider() {
            return new SqlStorageProvider();
        }

        @Override
        YamlConfiguration configuration() {
            return RealDatabases.sql(this.storageType(), this.property());
        }
    }

    @Nested
    @EnabledIfSystemProperty(named = RealDatabases.MYSQL, matches = ".+")
    class MySql extends Sql {
        @Override
        String storageType() {
            return "MYSQL";
        }

        @Override
        String property() {
            return RealDatabases.MYSQL;
        }
    }

    @Nested
    @EnabledIfSystemProperty(named = RealDatabases.MARIADB, matches = ".+")
    class MariaDb extends Sql {
        @Override
        String storageType() {
            return "MARIADB";
        }

        @Override
        String property() {
            return RealDatabases.MARIADB;
        }
    }

    @Nested
    @EnabledIfSystemProperty(named = RealDatabases.POSTGRESQL, matches = ".+")
    class PostgreSql extends Sql {
        @Override
        String storageType() {
            return "POSTGRESQL";
        }

        @Override
        String property() {
            return RealDatabases.POSTGRESQL;
        }
    }

    @Nested
    @EnabledIfSystemProperty(named = RealDatabases.REDIS, matches = ".+")
    class Redis extends TwoServerContractTest {
        @Override
        StorageProvider newProvider() {
            return new RedisStorageProvider();
        }

        @Override
        YamlConfiguration configuration() {
            return RealDatabases.redis();
        }

        @Override
        void awaitBrokers() throws Exception {
            RedisMultiServerTest.awaitSubscriptions(this.providerA, this.providerB);
        }
    }

    @Nested
    @EnabledIfSystemProperty(named = RealDatabases.MONGODB, matches = ".+")
    class MongoDb extends TwoServerContractTest {
        @Override
        StorageProvider newProvider() {
            return new MongoStorageProvider();
        }

        @Override
        YamlConfiguration configuration() {
            return RealDatabases.mongodb();
        }
    }
}
