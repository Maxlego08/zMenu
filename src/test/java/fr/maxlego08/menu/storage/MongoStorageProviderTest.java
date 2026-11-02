package fr.maxlego08.menu.storage;

import de.bwaldvogel.mongo.MongoServer;
import fr.maxlego08.menu.api.storage.spi.StorageCapability;
import fr.maxlego08.menu.api.storage.spi.StorageProvider;
import fr.maxlego08.menu.storage.mongo.MongoStorageProvider;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The storage contract on MongoDB, served by an in-process MongoDB (mongo-java-server). Each test
 * uses its own database.
 */
class MongoStorageProviderTest extends StorageProviderContractTest {

    private static MongoServer server;
    private static InetSocketAddress address;

    @BeforeAll
    static void startMongo() {
        server = MongoTestServers.newMongoServer();
        address = server.bind();
    }

    @AfterAll
    static void stopMongo() {
        server.shutdownNow();
    }

    @Override
    StorageProvider createProvider() {
        return new MongoStorageProvider();
    }

    @Override
    YamlConfiguration configuration() {
        return MongoTestServers.mongoConfiguration(address);
    }

    @Test
    void mongoIsSharedAndAddsAtomically() {
        assertEquals(EnumSet.of(StorageCapability.PERSISTENT, StorageCapability.SHARED, StorageCapability.ATOMIC_INCREMENT, StorageCapability.EXPORT), this.provider.capabilities());
        assertTrue(this.provider.broker().isPresent());
        assertTrue(this.provider.sessions().isPresent());
    }
}
