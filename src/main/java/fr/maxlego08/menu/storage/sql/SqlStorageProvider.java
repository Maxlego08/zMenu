package fr.maxlego08.menu.storage.sql;

import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.storage.spi.AnimationStore;
import fr.maxlego08.menu.api.storage.spi.InventoryLogStore;
import fr.maxlego08.menu.api.storage.spi.PlayerDataStore;
import fr.maxlego08.menu.api.storage.spi.SavedInventoryStore;
import fr.maxlego08.menu.api.storage.spi.StorageCapability;
import fr.maxlego08.menu.api.storage.spi.StorageContext;
import fr.maxlego08.menu.api.storage.spi.StorageProvider;
import fr.maxlego08.menu.api.storage.spi.WorldVisitStore;
import fr.maxlego08.menu.storage.StorageConfiguration;
import fr.maxlego08.menu.storage.migrations.FirstWorldJoinMigration;
import fr.maxlego08.menu.storage.migrations.PlayerAnimationMigration;
import fr.maxlego08.menu.storage.migrations.PlayerDataMigration;
import fr.maxlego08.menu.storage.migrations.PlayerInventoriesMigration;
import fr.maxlego08.menu.storage.migrations.PlayerOpenInventoryMigration;
import fr.maxlego08.sarah.DatabaseConfiguration;
import fr.maxlego08.sarah.DatabaseConnection;
import fr.maxlego08.sarah.HikariDatabaseConnection;
import fr.maxlego08.sarah.MigrationManager;
import fr.maxlego08.sarah.RequestHelper;
import fr.maxlego08.sarah.SqliteConnection;
import fr.maxlego08.sarah.database.DatabaseType;
import fr.maxlego08.sarah.logger.JULogger;
import fr.maxlego08.sarah.logger.Logger;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Keeps zMenu's data in SQLite, MySQL or MariaDB, chosen by {@code storage-type}, with the
 * connection settings of {@code database-configuration}.
 */
public final class SqlStorageProvider implements StorageProvider {

    private static final long CLOSE_TIMEOUT_SECONDS = 30;

    /**
     * Sarah keeps its migrations in a static list, so they are registered once even if a provider
     * is opened again.
     */
    private static final AtomicBoolean MIGRATIONS_REGISTERED = new AtomicBoolean(false);

    private SqlDatabase database;
    private DatabaseType databaseType;
    private PlayerDataStore playerData;
    private SavedInventoryStore savedInventories;
    private WorldVisitStore worldVisits;
    private AnimationStore animations;
    private InventoryLogStore inventoryLogs;

    /**
     * @return the database type a {@code storage-type} value selects, or null when the value does
     * not name a SQL database
     */
    public static DatabaseType databaseType(@NotNull String storageType) {
        return switch (storageType.toUpperCase(Locale.ROOT)) {
            case "SQLITE" -> DatabaseType.SQLITE;
            case "MYSQL" -> DatabaseType.MYSQL;
            case "MARIADB" -> DatabaseType.MARIADB;
            default -> null;
        };
    }

    @Override
    public @NotNull Key key() {
        return ZMenuKeys.key("sql");
    }

    @Override
    public @NotNull Set<StorageCapability> capabilities() {
        return this.databaseType == null || this.databaseType == DatabaseType.SQLITE
                ? EnumSet.of(StorageCapability.PERSISTENT)
                : EnumSet.of(StorageCapability.PERSISTENT, StorageCapability.SHARED);
    }

    @Override
    public @NotNull CompletableFuture<Void> open(@NotNull StorageContext context) {
        StorageConfiguration configuration = StorageConfiguration.read(context.configuration());
        DatabaseType type = databaseType(configuration.storageType());
        if (type == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("storage-type " + configuration.storageType() + " is not a SQL database"));
        }

        Logger logger = JULogger.from(context.logger());
        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration(configuration.tablePrefix(), configuration.user(), configuration.password(),
                configuration.port(), configuration.host(), configuration.database(), configuration.debug(), type);
        if (configuration.maxLifetimeMillis() != null) {
            databaseConfiguration = databaseConfiguration.withMaxLifetime(configuration.maxLifetimeMillis());
        }

        DatabaseConnection connection = type == DatabaseType.SQLITE
                ? new SqliteConnection(databaseConfiguration, context.dataFolder(), logger)
                : new HikariDatabaseConnection(databaseConfiguration, logger);

        if (!connection.isValid()) {
            return CompletableFuture.failedFuture(new IllegalStateException("Unable to connect to the " + type + " database"));
        }

        if (MIGRATIONS_REGISTERED.compareAndSet(false, true)) {
            MigrationManager.setMigrationTableName("zmenu_migrations");
            MigrationManager.registerMigration(new PlayerOpenInventoryMigration());
            MigrationManager.registerMigration(new PlayerDataMigration());
            MigrationManager.registerMigration(new PlayerInventoriesMigration());
            MigrationManager.registerMigration(new FirstWorldJoinMigration());
            MigrationManager.registerMigration(new PlayerAnimationMigration());
        }
        MigrationManager.setDatabaseConfiguration(connection.getDatabaseConfiguration());
        MigrationManager.execute(connection, logger);

        this.databaseType = type;
        this.database = new SqlDatabase(connection, new RequestHelper(connection, logger));
        this.playerData = new SqlPlayerDataStore(this.database);
        this.savedInventories = new SqlSavedInventoryStore(this.database);
        this.worldVisits = new SqlWorldVisitStore(this.database);
        this.animations = new SqlAnimationStore(this.database);
        this.inventoryLogs = new SqlInventoryLogStore(this.database);

        context.logger().info("The database connection is valid ! (" + (type == DatabaseType.SQLITE ? "SQLITE" : configuration.host()) + ")");
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public @NotNull CompletableFuture<Void> close() {
        if (this.database == null) return CompletableFuture.completedFuture(null);
        try {
            if (!this.database.close(CLOSE_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                return CompletableFuture.failedFuture(new IllegalStateException("Some database writes did not finish within " + CLOSE_TIMEOUT_SECONDS + " seconds"));
            }
            return CompletableFuture.completedFuture(null);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(exception);
        }
    }

    @Override
    public @NotNull PlayerDataStore playerData() {
        return this.playerData;
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
}
