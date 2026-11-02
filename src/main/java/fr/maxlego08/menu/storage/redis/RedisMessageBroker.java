package fr.maxlego08.menu.storage.redis;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.maxlego08.menu.api.storage.spi.MessageBroker;
import fr.maxlego08.menu.api.storage.spi.StorageEvent;
import fr.maxlego08.menu.storage.StorageEvents;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sends events on a Redis pub/sub channel: the other servers receive them at once. Usable with
 * any store, a Redis one or a SQL one ({@code messaging-type: REDIS}).
 *
 * <p>Pub/sub keeps nothing: a server that is disconnected when an event is sent misses it, and
 * only reads that value again on its next change. The subscription runs on its own thread and is
 * opened again after a disconnection.</p>
 */
public final class RedisMessageBroker implements MessageBroker {

    private static final long RECONNECT_DELAY_MILLIS = 2000;

    private final RedisConnection connection;
    private final String channel;
    private final String serverId;
    private final Logger logger;
    private final List<Consumer<StorageEvent>> listeners = new CopyOnWriteArrayList<>();
    private final JedisPubSub subscription = new JedisPubSub() {
        @Override
        public void onMessage(String channel, String message) {
            RedisMessageBroker.this.receive(message);
        }
    };
    private volatile boolean running;
    private Thread thread;

    RedisMessageBroker(RedisConnection connection, String channel, String serverId, Logger logger) {
        this.connection = connection;
        this.channel = channel;
        this.serverId = serverId;
        this.logger = logger;
    }

    /**
     * Starts listening to the channel.
     */
    public void start() {
        this.running = true;
        this.thread = new Thread(this::listen, "zMenu Redis Events");
        this.thread.setDaemon(true);
        this.thread.start();
    }

    public void stop() {
        this.running = false;
        try {
            if (this.subscription.isSubscribed()) this.subscription.unsubscribe();
        } catch (RuntimeException ignored) {
        }
        if (this.thread != null) this.thread.interrupt();
    }

    /**
     * @return whether the subscription is open, so an event published now is received
     */
    public boolean isSubscribed() {
        return this.subscription.isSubscribed();
    }

    private void listen() {
        while (this.running) {
            try {
                this.connection.jedis().subscribe(this.subscription, this.channel);
            } catch (RuntimeException exception) {
                if (!this.running) return;
                this.logger.log(Level.WARNING, "Lost the Redis event subscription, reconnecting", exception);
            }
            try {
                Thread.sleep(RECONNECT_DELAY_MILLIS);
            } catch (InterruptedException exception) {
                return;
            }
        }
    }

    @Override
    public @NotNull CompletableFuture<Void> publish(@NotNull List<StorageEvent> events) {
        if (events.isEmpty()) return CompletableFuture.completedFuture(null);
        return this.connection.threads().write(() -> {
            for (StorageEvent event : events) {
                this.connection.jedis().publish(this.channel, encode(event));
            }
        });
    }

    @Override
    public void subscribe(@NotNull Consumer<StorageEvent> listener) {
        this.listeners.add(listener);
    }

    private void receive(String message) {
        try {
            StorageEvent event = decode(message);
            if (event == null || event.origin().equals(this.serverId)) return;
            this.listeners.forEach(listener -> listener.accept(event));
        } catch (RuntimeException exception) {
            this.logger.log(Level.WARNING, "Unable to read the Redis event " + message, exception);
        }
    }

    static String encode(StorageEvent event) {
        JsonObject json = new JsonObject();
        StorageEvents.toFields(event).forEach((key, value) -> {
            if (value != null) json.addProperty(key, value);
        });
        return json.toString();
    }

    static StorageEvent decode(String message) {
        JsonObject json = JsonParser.parseString(message).getAsJsonObject();
        Map<String, String> fields = new HashMap<>();
        json.entrySet().forEach(entry -> {
            if (!entry.getValue().isJsonNull()) fields.put(entry.getKey(), entry.getValue().getAsString());
        });
        return StorageEvents.fromFields(fields);
    }
}
