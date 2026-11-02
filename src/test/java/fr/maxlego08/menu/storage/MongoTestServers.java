package fr.maxlego08.menu.storage;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.MongoVersion;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.bukkit.configuration.file.YamlConfiguration;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;

/**
 * In-process MongoDB servers (mongo-java-server) for the tests.
 */
public final class MongoTestServers {

    private MongoTestServers() {
    }

    /**
     * An in-memory MongoDB that reports version 4.2, the oldest the 5.x driver connects to.
     */
    public static MongoServer newMongoServer() {
        MongoVersion version = new MongoVersion() {
            @Override
            public List<Integer> getVersionArray() {
                return List.of(4, 2, 0);
            }

            @Override
            public int getWireVersion() {
                return 8;
            }
        };
        return new MongoServer(new MemoryBackend().version(version));
    }

    /**
     * A configuration using a database of its own on the server, read every 250 ms for events.
     */
    public static YamlConfiguration mongoConfiguration(InetSocketAddress address) {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("storage-type", "MONGODB");
        configuration.set("mongodb.uri", "mongodb://" + address.getHostString() + ":" + address.getPort());
        configuration.set("mongodb.database", "test_" + UUID.randomUUID().toString().replace("-", ""));
        configuration.set("messaging-poll-interval", 0.25);
        return configuration;
    }
}
