package fr.maxlego08.menu.storage.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import fr.maxlego08.menu.api.storage.spi.AnimationStore;
import fr.maxlego08.menu.api.storage.spi.InventoryLogStore;
import fr.maxlego08.menu.api.storage.spi.SavedInventoryStore;
import fr.maxlego08.menu.api.storage.spi.SessionStore;
import fr.maxlego08.menu.api.storage.spi.VariableStore;
import fr.maxlego08.menu.api.storage.spi.WorldVisitStore;
import fr.maxlego08.menu.storage.StorageThreads;
import org.bson.Document;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * An open MongoDB connection, the threads that use it and the stores built on it.
 *
 * <p>Only {@link MongoStorageProvider} creates one, and only when MongoDB is configured: this class
 * needs the MongoDB driver, which zMenu only downloads then.</p>
 */
public final class MongoConnection {

    private final MongoClient client;
    private final MongoDatabase database;
    private final String prefix;
    private final StorageThreads threads = new StorageThreads("MongoDB");

    private MongoConnection(MongoClient client, MongoDatabase database, String prefix) {
        this.client = client;
        this.database = database;
        this.prefix = prefix;
    }

    /**
     * Connects, checks the server answers and creates the indexes zMenu reads by.
     */
    public static MongoConnection open(MongoSettings settings) {
        MongoClient client = MongoClients.create(settings.uri());
        MongoDatabase database = client.getDatabase(settings.database());
        database.runCommand(new Document("ping", 1));
        MongoConnection connection = new MongoConnection(client, database, settings.collectionPrefix());
        connection.createIndexes();
        return connection;
    }

    private void createIndexes() {
        this.collection("variables").createIndex(Indexes.ascending("scope", "owner"));
        this.collection("variables").createIndex(Indexes.ascending("scope", "name"));
        this.collection("variables").createIndex(Indexes.ascending("expiresAt"));
        this.collection("saved_inventories").createIndex(Indexes.ascending("server"));
        this.collection("animations").createIndex(Indexes.ascending("player"));
        this.collection("sessions").createIndex(Indexes.ascending("server", "online"));
        this.collection("events").createIndex(Indexes.ascending("createdAt"));
    }

    MongoCollection<Document> collection(String name) {
        return this.database.getCollection(this.prefix + name);
    }

    StorageThreads threads() {
        return this.threads;
    }

    public VariableStore variables() {
        return new MongoVariableStore(this);
    }

    public SavedInventoryStore savedInventories(String serverId) {
        return new MongoSavedInventoryStore(this, serverId);
    }

    public WorldVisitStore worldVisits() {
        return new MongoWorldVisitStore(this);
    }

    public AnimationStore animations() {
        return new MongoAnimationStore(this);
    }

    public InventoryLogStore inventoryLogs() {
        return new MongoInventoryLogStore(this);
    }

    public SessionStore sessions() {
        return new MongoSessionStore(this);
    }

    /**
     * @return a broker on the events collection; {@link MongoPollingBroker#start(long)} must be
     * called to start reading
     */
    public MongoPollingBroker broker(String serverId, Logger logger) {
        return new MongoPollingBroker(this, serverId, logger);
    }

    /**
     * Waits for the queued writes, then closes the connection.
     *
     * @return whether every write finished before the timeout
     */
    public boolean close(long timeout, TimeUnit unit) throws InterruptedException {
        boolean finished = this.threads.close(timeout, unit);
        this.client.close();
        return finished;
    }
}
