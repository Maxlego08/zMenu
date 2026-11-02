package fr.maxlego08.menu.storage.mongo;

import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import fr.maxlego08.menu.api.storage.spi.MessageBroker;
import fr.maxlego08.menu.api.storage.spi.StorageEvent;
import fr.maxlego08.menu.storage.StorageEvents;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sends events through the {@code events} collection, which every server reads every
 * {@code messaging-poll-interval} seconds. It works on any MongoDB, a single node included; use
 * {@code messaging-type: REDIS} for changes that arrive at once.
 *
 * <p>Each event takes the next number of a counter document, so servers read them in order. A
 * number taken by one server can be written after a higher one taken by another, so the last
 * {@link #OVERLAP} numbers are read again. Events are written by the same thread as the data,
 * after it.</p>
 */
public final class MongoPollingBroker implements MessageBroker {

    private static final int PAGE = 500;
    private static final int OVERLAP = 200;
    private static final int SEEN_LIMIT = 4000;
    private static final long RETENTION_MILLIS = TimeUnit.MINUTES.toMillis(10);
    private static final long PRUNE_INTERVAL_MILLIS = TimeUnit.MINUTES.toMillis(5);

    private final MongoConnection connection;
    private final String serverId;
    private final Logger logger;
    private final List<Consumer<StorageEvent>> listeners = new CopyOnWriteArrayList<>();
    private final Set<Long> seen = new LinkedHashSet<>();
    private final ScheduledExecutorService poller = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "zMenu MongoDB Events");
        thread.setDaemon(true);
        return thread;
    });
    private long startId;
    private long lastSeen;
    private long lastPrune;

    MongoPollingBroker(MongoConnection connection, String serverId, Logger logger) {
        this.connection = connection;
        this.serverId = serverId;
        this.logger = logger;
    }

    /**
     * Starts reading the events published from now on.
     */
    public void start(long intervalMillis) {
        Document last = this.connection.collection("events").find().sort(Sorts.descending("_id")).first();
        this.lastSeen = last == null ? 0 : ((Number) last.get("_id")).longValue();
        this.startId = this.lastSeen;
        this.lastPrune = System.currentTimeMillis();
        this.poller.scheduleWithFixedDelay(this::poll, intervalMillis, intervalMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * Stops reading, waiting for a read in progress so it does not use the database after it is
     * closed.
     */
    public void stop() {
        this.poller.shutdown();
        try {
            if (!this.poller.awaitTermination(5, TimeUnit.SECONDS)) this.poller.shutdownNow();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    private long nextId() {
        Document counter = this.connection.collection("counters").findOneAndUpdate(Filters.eq("_id", "events"), Updates.inc("value", 1L),
                new FindOneAndUpdateOptions().upsert(true).returnDocument(ReturnDocument.AFTER));
        return ((Number) counter.get("value")).longValue();
    }

    @Override
    public @NotNull CompletableFuture<Void> publish(@NotNull List<StorageEvent> events) {
        if (events.isEmpty()) return CompletableFuture.completedFuture(null);
        return this.connection.threads().write(() -> {
            long now = System.currentTimeMillis();
            for (StorageEvent event : events) {
                Document document = new Document("_id", this.nextId()).append("createdAt", now);
                StorageEvents.toFields(event).forEach(document::append);
                this.connection.collection("events").insertOne(document);
            }
        });
    }

    @Override
    public void subscribe(@NotNull Consumer<StorageEvent> listener) {
        this.listeners.add(listener);
    }

    private void poll() {
        try {
            int read;
            long from = Math.max(0, this.lastSeen - OVERLAP);
            do {
                read = 0;
                for (Document document : this.connection.collection("events").find(Filters.gt("_id", from)).sort(Sorts.ascending("_id")).limit(PAGE)) {
                    read++;
                    long id = ((Number) document.get("_id")).longValue();
                    from = Math.max(from, id);
                    this.lastSeen = Math.max(this.lastSeen, id);
                    if (id <= this.startId || !this.seen.add(id)) continue;
                    if (this.seen.size() > SEEN_LIMIT) this.seen.remove(this.seen.iterator().next());
                    if (this.serverId.equals(document.getString("origin"))) continue;

                    Map<String, String> fields = new HashMap<>();
                    for (String field : new String[]{"origin", "type", "scope", "owner", "name"}) {
                        fields.put(field, document.getString(field));
                    }
                    StorageEvent event = StorageEvents.fromFields(fields);
                    if (event != null) this.listeners.forEach(listener -> listener.accept(event));
                }
            } while (read == PAGE);

            long now = System.currentTimeMillis();
            if (now - this.lastPrune > PRUNE_INTERVAL_MILLIS) {
                this.lastPrune = now;
                this.connection.threads().write(() -> this.connection.collection("events").deleteMany(Filters.lt("createdAt", now - RETENTION_MILLIS)));
            }
        } catch (Exception exception) {
            this.logger.log(Level.WARNING, "Unable to read the storage events", exception);
        }
    }
}
