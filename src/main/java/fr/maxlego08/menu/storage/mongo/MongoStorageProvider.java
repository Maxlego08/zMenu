package fr.maxlego08.menu.storage.mongo;

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
import fr.maxlego08.menu.storage.StorageConfiguration;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Keeps zMenu's data in MongoDB ({@code storage-type: MONGODB}), with the settings of the
 * {@code mongodb} section. Changes reach the other servers through the events collection, unless
 * {@code messaging-type} is {@code NONE} or {@code REDIS}.
 *
 * <p>This class names no MongoDB driver type, so it can be registered on every server; the driver
 * is only needed, and only downloaded, when MongoDB is configured.</p>
 */
public final class MongoStorageProvider implements StorageProvider {

    private static final long CLOSE_TIMEOUT_SECONDS = 30;

    private MongoConnection connection;
    private MongoPollingBroker broker;
    private VariableStore variables;
    private SavedInventoryStore savedInventories;
    private WorldVisitStore worldVisits;
    private AnimationStore animations;
    private InventoryLogStore inventoryLogs;
    private SessionStore sessions;

    @Override
    public @NotNull Key key() {
        return ZMenuKeys.key("mongodb");
    }

    @Override
    public @NotNull Set<StorageCapability> capabilities() {
        return EnumSet.of(StorageCapability.PERSISTENT, StorageCapability.SHARED, StorageCapability.ATOMIC_INCREMENT, StorageCapability.EXPORT);
    }

    @Override
    public @NotNull CompletableFuture<Void> open(@NotNull StorageContext context) {
        MongoSettings settings = MongoSettings.read(context.configuration());
        try {
            this.connection = MongoConnection.open(settings);
        } catch (NoClassDefFoundError error) {
            return CompletableFuture.failedFuture(new IllegalStateException("The MongoDB driver is missing. zMenu downloads it when it starts with storage-type MONGODB: restart the server.", error));
        } catch (RuntimeException exception) {
            return CompletableFuture.failedFuture(exception);
        }

        this.variables = this.connection.variables();
        this.savedInventories = this.connection.savedInventories(context.serverId());
        this.worldVisits = this.connection.worldVisits();
        this.animations = this.connection.animations();
        this.inventoryLogs = this.connection.inventoryLogs();
        this.sessions = this.connection.sessions();

        StorageConfiguration configuration = StorageConfiguration.read(context.configuration());
        if (configuration.messagingType().equalsIgnoreCase("AUTO")) {
            this.broker = this.connection.broker(context.serverId(), context.logger());
            this.broker.start(configuration.pollIntervalMillis());
        }

        context.logger().info("The MongoDB connection is valid ! (" + settings.database() + ")");
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public @NotNull CompletableFuture<Void> close() {
        if (this.connection == null) return CompletableFuture.completedFuture(null);
        if (this.broker != null) this.broker.stop();
        try {
            if (!this.connection.close(CLOSE_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                return CompletableFuture.failedFuture(new IllegalStateException("Some MongoDB writes did not finish within " + CLOSE_TIMEOUT_SECONDS + " seconds"));
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
