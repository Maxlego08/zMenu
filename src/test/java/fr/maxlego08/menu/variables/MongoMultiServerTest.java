package fr.maxlego08.menu.variables;

import de.bwaldvogel.mongo.MongoServer;
import fr.maxlego08.menu.api.storage.spi.StorageProvider;
import fr.maxlego08.menu.storage.MongoTestServers;
import fr.maxlego08.menu.storage.mongo.MongoStorageProvider;
import org.bukkit.configuration.file.YamlConfiguration;

import java.net.InetSocketAddress;

/**
 * Two servers sharing one in-process MongoDB (mongo-java-server): changes travel through the
 * events collection.
 */
class MongoMultiServerTest extends TwoServerContractTest {

    private MongoServer mongo;
    private InetSocketAddress address;

    @Override
    void prepare() {
        this.mongo = MongoTestServers.newMongoServer();
        this.address = this.mongo.bind();
    }

    @Override
    void cleanup() {
        this.mongo.shutdownNow();
    }

    @Override
    StorageProvider newProvider() {
        return new MongoStorageProvider();
    }

    @Override
    YamlConfiguration configuration() {
        return MongoTestServers.mongoConfiguration(this.address);
    }
}
