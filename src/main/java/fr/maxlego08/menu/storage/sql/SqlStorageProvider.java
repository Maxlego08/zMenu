package fr.maxlego08.menu.storage.sql;

import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.storage.spi.AnimationStore;
import fr.maxlego08.menu.api.storage.spi.InventoryLogStore;
import fr.maxlego08.menu.api.storage.Tables;
import fr.maxlego08.menu.api.storage.dto.DataDTO;
import fr.maxlego08.menu.api.storage.spi.MessageBroker;
import fr.maxlego08.menu.api.storage.spi.SavedInventoryStore;
import fr.maxlego08.menu.api.storage.spi.SessionStore;
import fr.maxlego08.menu.api.storage.spi.StorageCapability;
import fr.maxlego08.menu.api.storage.spi.StorageContext;
import fr.maxlego08.menu.api.storage.spi.StorageProvider;
import fr.maxlego08.menu.api.storage.spi.VariableStore;
import fr.maxlego08.menu.api.storage.spi.WorldVisitStore;
import fr.maxlego08.menu.api.variable.Variable;
import fr.maxlego08.menu.api.variable.VariableRef;
import fr.maxlego08.menu.api.variable.VariableScope;
import fr.maxlego08.menu.storage.StorageConfiguration;
import fr.maxlego08.menu.storage.migrations.MultiServerMigration;
import fr.maxlego08.menu.storage.migrations.VariablesMigration;
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
import fr.maxlego08.sarah.SchemaBuilder;
import fr.maxlego08.sarah.SqliteConnection;
import fr.maxlego08.sarah.database.DatabaseType;
import fr.maxlego08.sarah.database.Schema;
import fr.maxlego08.sarah.logger.JULogger;
import fr.maxlego08.sarah.logger.Logger;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Keeps zMenu's data in SQLite, MySQL, MariaDB or PostgreSQL, chosen by {@code storage-type}, with
 * the connection settings of {@code database-configuration}.
 */
public final class SqlStorageProvider implements StorageProvider {

    private static final long CLOSE_TIMEOUT_SECONDS = 30;
    private static final int LEGACY_COPY_PAGE = 500;

    /**
     * Sarah keeps its migrations in a static list, so they are registered once even if a provider
     * is opened again.
     */
    private static final AtomicBoolean MIGRATIONS_REGISTERED = new AtomicBoolean(false);

    private SqlDatabase database;
    private DatabaseType databaseType;
    private VariableStore variables;
    private SavedInventoryStore savedInventories;
    private WorldVisitStore worldVisits;
    private AnimationStore animations;
    private InventoryLogStore inventoryLogs;
    private SqlSessionStore sessions;
    private SqlPollingBroker broker;

    /**
     * @return the database type a {@code storage-type} value selects, or null when the value does
     * not name a SQL database
     */
    public static DatabaseType databaseType(@NotNull String storageType) {
        return switch (storageType.toUpperCase(Locale.ROOT)) {
            case "SQLITE" -> DatabaseType.SQLITE;
            case "MYSQL" -> DatabaseType.MYSQL;
            case "MARIADB" -> DatabaseType.MARIADB;
            case "POSTGRESQL", "POSTGRES" -> DatabaseType.POSTGRESQL;
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
                ? EnumSet.of(StorageCapability.PERSISTENT, StorageCapability.ATOMIC_INCREMENT, StorageCapability.EXPORT)
                : EnumSet.of(StorageCapability.PERSISTENT, StorageCapability.SHARED, StorageCapability.ATOMIC_INCREMENT, StorageCapability.EXPORT);
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
            MigrationManager.registerMigration(new VariablesMigration());
            MigrationManager.registerMigration(new MultiServerMigration());
        }
        MigrationManager.setDatabaseConfiguration(connection.getDatabaseConfiguration());
        MigrationManager.execute(connection, logger);

        RequestHelper requestHelper = new RequestHelper(connection, logger);
        try {
            int copied = copyLegacyPlayerData(requestHelper);
            if (copied > 0) context.logger().info("Copied " + copied + " player data values to the variables table.");
            int inventories = SqlSavedInventoryStore.copyLegacyInventories(requestHelper, context.serverId());
            if (inventories > 0) context.logger().info("Copied " + inventories + " saved inventories to this server (" + context.serverId() + ").");
        } catch (RuntimeException exception) {
            connection.disconnect();
            return CompletableFuture.failedFuture(exception);
        }

        this.databaseType = type;
        this.database = new SqlDatabase(connection, requestHelper);
        this.variables = new SqlVariableStore(this.database);
        this.savedInventories = new SqlSavedInventoryStore(this.database, context.serverId());
        this.worldVisits = new SqlWorldVisitStore(this.database);
        this.animations = new SqlAnimationStore(this.database);
        this.inventoryLogs = new SqlInventoryLogStore(this.database);

        boolean forced = configuration.messagingType().equalsIgnoreCase("SQL");
        if (type != DatabaseType.SQLITE || forced) {
            this.sessions = new SqlSessionStore(this.database);
            if (configuration.messagingType().equalsIgnoreCase("AUTO") || forced) {
                this.broker = new SqlPollingBroker(this.database, context.serverId(), context.logger());
                this.broker.start(configuration.pollIntervalMillis());
            }
        }

        context.logger().info("The database connection is valid ! (" + (type == DatabaseType.SQLITE ? "SQLITE" : configuration.host()) + ")");
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public @NotNull CompletableFuture<Void> close() {
        if (this.database == null) return CompletableFuture.completedFuture(null);
        if (this.broker != null) this.broker.stop();
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

    /**
     * Copies the values of the old {@code player_datas} table into {@code variables}, once: only
     * when no player variable exists yet. The copy runs in one transaction, so a failure leaves
     * {@code variables} empty and the copy runs again on the next start. The old table is kept.
     *
     * @return how many values were copied
     */
    static int copyLegacyPlayerData(RequestHelper requestHelper) {
        if (requestHelper.count(Tables.VARIABLES, table -> table.where("scope", VariableScope.PLAYER.id())) > 0) return 0;
        if (requestHelper.count(Tables.PLAYER_DATAS, table -> {
        }) == 0) return 0;

        return requestHelper.inTransaction(helper -> {
            int copied = 0;
            int offset = 0;
            while (true) {
                int pageOffset = offset;
                List<DataDTO> rows = helper.select(Tables.PLAYER_DATAS, DataDTO.class, table -> {
                    table.orderBy("`player_id`, `key`");
                    table.limit(LEGACY_COPY_PAGE).offset(pageOffset);
                });
                if (rows.isEmpty()) break;

                List<Schema> schemas = new ArrayList<>(rows.size());
                for (DataDTO row : rows) {
                    if (row.player_id() == null || row.key() == null) continue;
                    Variable variable = Variable.of(VariableRef.player(row.player_id(), row.key()), row.data() == null ? "" : row.data(),
                            row.expired_at() == null ? 0 : row.expired_at().getTime());
                    schemas.add(SchemaBuilder.upsert(Tables.VARIABLES, table -> {
                        table.string("scope", VariableScope.PLAYER.id()).primary();
                        table.string("owner", variable.ref().owner()).primary();
                        table.string("name", variable.ref().name()).primary();
                        table.object("text_value", variable.number() == null ? variable.text() : null);
                        table.object("number_value", variable.number());
                        table.bigInt("expires_at", variable.expiresAt());
                        table.bigInt("version", 1);
                    }));
                }
                if (!schemas.isEmpty()) helper.upsertMultiple(schemas);
                copied += schemas.size();
                offset += rows.size();
            }
            return copied;
        });
    }

    @Override
    public @NotNull VariableStore variables() {
        return this.variables;
    }

    /**
     * @return the event table of MySQL and MariaDB with {@code messaging-type: AUTO}. SQLite is not
     * shared between servers, so it has none unless {@code messaging-type} is {@code SQL}. With
     * {@code REDIS} or {@code NONE} there is none either.
     */
    @Override
    public @NotNull Optional<MessageBroker> broker() {
        return Optional.ofNullable(this.broker);
    }

    @Override
    public @NotNull Optional<SessionStore> sessions() {
        return Optional.ofNullable(this.sessions);
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
