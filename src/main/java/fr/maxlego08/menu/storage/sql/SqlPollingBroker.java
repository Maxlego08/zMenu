package fr.maxlego08.menu.storage.sql;

import fr.maxlego08.menu.api.storage.Tables;
import fr.maxlego08.menu.api.storage.spi.MessageBroker;
import fr.maxlego08.menu.api.storage.spi.StorageEvent;
import fr.maxlego08.menu.api.variable.VariableRef;
import fr.maxlego08.menu.api.variable.VariableScope;
import fr.maxlego08.sarah.SchemaBuilder;
import fr.maxlego08.sarah.database.Schema;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sends events through a table of the shared database, which every server reads every few
 * seconds. It needs nothing but the database, at the cost of that delay.
 *
 * <p>Events are written by the same thread as the data, after it, so a server that reads an event
 * also reads the change it announces. Events older than {@link #RETENTION_MILLIS} are deleted.</p>
 */
final class SqlPollingBroker implements MessageBroker {

    private static final int PAGE = 500;
    private static final int OVERLAP = 200;
    private static final int SEEN_LIMIT = 4000;
    private static final long RETENTION_MILLIS = TimeUnit.MINUTES.toMillis(10);
    private static final long PRUNE_INTERVAL_MILLIS = TimeUnit.MINUTES.toMillis(5);

    private final SqlDatabase database;
    private final String serverId;
    private final Logger logger;
    private final List<Consumer<StorageEvent>> listeners = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService poller = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "zMenu Storage Events");
        thread.setDaemon(true);
        return thread;
    });
    private final Set<Long> seen = new LinkedHashSet<>();
    private long startId;
    private long lastSeen;
    private long lastPrune;

    SqlPollingBroker(SqlDatabase database, String serverId, Logger logger) {
        this.database = database;
        this.serverId = serverId;
        this.logger = logger;
    }

    /**
     * Starts reading the events published from now on.
     */
    void start(long intervalMillis) {
        List<Long> max = this.database.requestHelper().query("SELECT MAX(`id`) FROM " + Tables.STORAGE_EVENTS, resultSet -> resultSet.getLong(1));
        this.lastSeen = max.isEmpty() ? 0 : max.get(0);
        this.startId = this.lastSeen;
        this.lastPrune = System.currentTimeMillis();
        this.poller.scheduleWithFixedDelay(this::poll, intervalMillis, intervalMillis, TimeUnit.MILLISECONDS);
    }

    void stop() {
        this.poller.shutdownNow();
    }

    @Override
    public @NotNull CompletableFuture<Void> publish(@NotNull List<StorageEvent> events) {
        if (events.isEmpty()) return CompletableFuture.completedFuture(null);

        long now = System.currentTimeMillis();
        List<Schema> schemas = new ArrayList<>(events.size());
        for (StorageEvent event : events) {
            schemas.add(SchemaBuilder.insert(Tables.STORAGE_EVENTS, table -> {
                table.string("origin", event.origin());
                switch (event) {
                    case StorageEvent.VariableChanged changed -> {
                        table.string("type", "variable");
                        table.string("scope", changed.ref().scope().id());
                        table.string("owner", changed.ref().owner());
                        table.string("name", changed.ref().name());
                    }
                    case StorageEvent.NameCleared cleared -> {
                        table.string("type", "name");
                        table.string("scope", cleared.scope().id());
                        table.object("owner", null);
                        table.string("name", cleared.name());
                    }
                    case StorageEvent.OwnerCleared cleared -> {
                        table.string("type", "owner");
                        table.string("scope", cleared.scope().id());
                        table.string("owner", cleared.owner());
                        table.object("name", null);
                    }
                    case StorageEvent.ScopeCleared cleared -> {
                        table.string("type", "scope");
                        table.string("scope", cleared.scope().id());
                        table.object("owner", null);
                        table.object("name", null);
                    }
                    case StorageEvent.SessionReleased released -> {
                        table.string("type", "session");
                        table.object("scope", null);
                        table.string("owner", released.playerId().toString());
                        table.object("name", null);
                    }
                }
                table.bigInt("created_at", now);
            }));
        }
        return this.database.write(() -> this.database.requestHelper().insertMultiple(schemas));
    }

    @Override
    public void subscribe(@NotNull Consumer<StorageEvent> listener) {
        this.listeners.add(listener);
    }

    /**
     * Reads the events after the last one seen, and the {@link #OVERLAP} before it: when several
     * servers insert at once, a lower id can be committed after a higher one has been read.
     */
    private void poll() {
        try {
            List<Map<String, Object>> rows;
            long from = Math.max(0, this.lastSeen - OVERLAP);
            do {
                long start = from;
                rows = this.database.requestHelper().select(Tables.STORAGE_EVENTS, table -> {
                    table.where("id", ">", start);
                    table.orderBy("`id`");
                    table.limit(PAGE);
                });
                for (Map<String, Object> row : rows) {
                    long id = SqlRows.number(row, "id");
                    from = Math.max(from, id);
                    this.lastSeen = Math.max(this.lastSeen, id);
                    if (id <= this.startId || !this.seen.add(id)) continue;
                    if (this.seen.size() > SEEN_LIMIT) this.seen.remove(this.seen.iterator().next());
                    if (this.serverId.equals(SqlRows.text(row, "origin"))) continue;
                    StorageEvent event = toEvent(row);
                    if (event != null) this.listeners.forEach(listener -> listener.accept(event));
                }
            } while (rows.size() == PAGE);

            long now = System.currentTimeMillis();
            if (now - this.lastPrune > PRUNE_INTERVAL_MILLIS) {
                this.lastPrune = now;
                this.database.write(() -> this.database.requestHelper().delete(Tables.STORAGE_EVENTS, table -> table.where("created_at", "<", now - RETENTION_MILLIS)));
            }
        } catch (Exception exception) {
            this.logger.log(Level.WARNING, "Unable to read the storage events", exception);
        }
    }

    private static StorageEvent toEvent(Map<String, Object> row) {
        String origin = SqlRows.text(row, "origin");
        String type = SqlRows.text(row, "type");
        VariableScope scope = VariableScope.parse(SqlRows.text(row, "scope"));
        String owner = SqlRows.text(row, "owner");
        String name = SqlRows.text(row, "name");
        if (origin == null || type == null) return null;
        return switch (type) {
            case "variable" -> scope == null || owner == null || name == null ? null : new StorageEvent.VariableChanged(origin, new VariableRef(scope, owner, name));
            case "name" -> scope == null || name == null ? null : new StorageEvent.NameCleared(origin, scope, name);
            case "owner" -> scope == null || owner == null ? null : new StorageEvent.OwnerCleared(origin, scope, owner);
            case "scope" -> scope == null ? null : new StorageEvent.ScopeCleared(origin, scope);
            case "session" -> owner == null ? null : new StorageEvent.SessionReleased(origin, UUID.fromString(owner));
            default -> null;
        };
    }
}
