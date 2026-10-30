package fr.maxlego08.menu.storage.redis;

import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.storage.spi.AnimationStore;
import fr.maxlego08.menu.api.storage.spi.InventoryLogStore;
import fr.maxlego08.menu.api.storage.spi.MessageBroker;
import fr.maxlego08.menu.api.storage.spi.SavedInventoryStore;
import fr.maxlego08.menu.api.storage.spi.SessionStore;
import fr.maxlego08.menu.api.storage.spi.StorageCapability;
import fr.maxlego08.menu.api.storage.spi.StorageContext;
import fr.maxlego08.menu.api.storage.spi.StorageProvider;
import fr.maxlego08.menu.api.storage.spi.VariableStore;
import fr.maxlego08.menu.api.storage.spi.WorldVisitStore;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Keeps zMenu's data in Redis ({@code storage-type: REDIS}), with the settings of the {@code redis}
 * section. Changes are sent to the other servers on a pub/sub channel, unless
 * {@code messaging-type} is {@code NONE}.
 *
 * <p>This class names no Redis client type, so it can be registered on every server; the client
 * is only needed, and only downloaded, when Redis is configured.</p>
 */
public final class RedisStorageProvider implements StorageProvider {

    private static final long CLOSE_TIMEOUT_SECONDS = 30;

    private RedisConnection connection;
    private RedisMessageBroker broker;
    private VariableStore variables;
    private SavedInventoryStore savedInventories;
    private WorldVisitStore worldVisits;
    private AnimationStore animations;
    private InventoryLogStore inventoryLogs;
    private SessionStore sessions;

    @Override
    public @NotNull Key key() {
        return ZMenuKeys.key("redis");
    }

    @Override
    public @NotNull Set<StorageCapability> capabilities() {
        return EnumSet.of(StorageCapability.PERSISTENT, StorageCapability.SHARED, StorageCapability.ATOMIC_INCREMENT);
    }

    @Override
    public @NotNull CompletableFuture<Void> open(@NotNull StorageContext context) {
        RedisSettings settings = RedisSettings.read(context.configuration());
        try {
            this.connection = RedisConnection.open(settings, context.logger());
        } catch (NoClassDefFoundError error) {
            return CompletableFuture.failedFuture(new IllegalStateException("The Redis client is missing. zMenu downloads it when it starts with storage-type or messaging-type REDIS: restart the server.", error));
        } catch (RuntimeException exception) {
            return CompletableFuture.failedFuture(exception);
        }

        this.variables = this.connection.variables();
        this.savedInventories = this.connection.savedInventories(context.serverId());
        this.worldVisits = this.connection.worldVisits();
        this.animations = this.connection.animations();
        this.inventoryLogs = this.connection.inventoryLogs();
        this.sessions = this.connection.sessions();

        if (!context.configuration().getString("messaging-type", "AUTO").equalsIgnoreCase("NONE")) {
            this.broker = this.connection.broker(settings.channel(), context.serverId(), context.logger());
            this.broker.start();
        }

        context.logger().info("The Redis connection is valid ! (" + (settings.cluster() ? "cluster" : "standalone") + ")");
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public @NotNull CompletableFuture<Void> close() {
        if (this.connection == null) return CompletableFuture.completedFuture(null);
        if (this.broker != null) this.broker.stop();
        try {
            if (!this.connection.close(CLOSE_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                return CompletableFuture.failedFuture(new IllegalStateException("Some Redis writes did not finish within " + CLOSE_TIMEOUT_SECONDS + " seconds"));
            }
            return CompletableFuture.completedFuture(null);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(exception);
        }
    }

    @Override
    public @NotNull VariableStore variables() {
        return this.variables;
    }

    @Override
    public @NotNull SavedInventoryStore savedInventories() {
        return this.savedInventories;
    }

    @Override
    public @NotNull WorldVisitStore worldVisits() {
        return this.worldVisits;
    }

    @Override
    public @NotNull AnimationStore animations() {
        return this.animations;
    }

    @Override
    public @NotNull Optional<InventoryLogStore> inventoryLogs() {
        return Optional.ofNullable(this.inventoryLogs);
    }

    @Override
    public @NotNull Optional<MessageBroker> broker() {
        return Optional.ofNullable(this.broker);
    }

    @Override
    public @NotNull Optional<SessionStore> sessions() {
        return Optional.ofNullable(this.sessions);
    }
}
