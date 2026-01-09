package fr.maxlego08.menu.storage;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.event.events.PlayerOpenInventoryEvent;
import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.inventory.InventoryPlayer;
import fr.maxlego08.menu.api.storage.StorageManager;
import fr.maxlego08.menu.api.storage.Tables;
import fr.maxlego08.menu.api.storage.dto.DataDTO;
import fr.maxlego08.menu.api.storage.dto.InventoryDTO;
import fr.maxlego08.menu.storage.migrations.PlayerDataMigration;
import fr.maxlego08.menu.storage.migrations.PlayerInventoriesMigration;
import fr.maxlego08.menu.storage.migrations.PlayerOpenInventoryMigration;
import fr.maxlego08.menu.zcore.utils.GlobalDatabaseConfiguration;
import fr.maxlego08.menu.zcore.utils.TypeSafeCache;
import fr.maxlego08.sarah.*;
import fr.maxlego08.sarah.database.DatabaseType;
import fr.maxlego08.sarah.database.Schema;
import fr.maxlego08.sarah.logger.JULogger;
import fr.maxlego08.sarah.logger.Logger;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ZStorageManager implements StorageManager {

    private final MenuPlugin plugin;
    private final TypeSafeCache cache = new TypeSafeCache();
    private RequestHelper requestHelper;
    private boolean isEnable = true;

    public ZStorageManager(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void loadDatabase() {

        MigrationManager.setMigrationTableName("zmenu_migrations");
        MigrationManager.registerMigration(new PlayerOpenInventoryMigration());
        MigrationManager.registerMigration(new PlayerDataMigration());
        MigrationManager.registerMigration(new PlayerInventoriesMigration());

        GlobalDatabaseConfiguration globalDatabaseConfiguration = new GlobalDatabaseConfiguration(this.plugin.getConfig());
        String user = globalDatabaseConfiguration.getUser();
        String password = globalDatabaseConfiguration.getPassword();
        String host = globalDatabaseConfiguration.getHost();
        String dataBase = globalDatabaseConfiguration.getDatabase();
        String prefix = globalDatabaseConfiguration.getTablePrefix();
        int port = globalDatabaseConfiguration.getPort();
        boolean enableDebug = globalDatabaseConfiguration.isDebug();

        String storageType = this.plugin.getConfig().getString("storage-type", "SQLITE");
        if (storageType.equalsIgnoreCase("NONE")) {
            this.plugin.getLogger().info("You are not using a database.");
            this.isEnable = false;
            return;
        }

        DatabaseConnection databaseConnection;
        if (storageType.equalsIgnoreCase("SQLITE")) {
            databaseConnection = new SqliteConnection(new DatabaseConfiguration(prefix, user, password, port, host, dataBase, enableDebug, DatabaseType.SQLITE), this.plugin.getDataFolder());
        } else {
            databaseConnection = new HikariDatabaseConnection(new DatabaseConfiguration(prefix, user, password, port, host, dataBase, enableDebug, storageType.equalsIgnoreCase("MYSQL") ? DatabaseType.MYSQL : DatabaseType.MARIADB));
        }

        Logger logger = JULogger.from(plugin.getLogger());
        this.requestHelper = new RequestHelper(databaseConnection, logger);

        if (!databaseConnection.isValid()) {
            this.plugin.getLogger().severe("Unable to connect to database !");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        } else {
            this.plugin.getLogger().info("The database connection is valid ! (" + (storageType.equalsIgnoreCase("SQLITE") ? "SQLITE" : databaseConnection.getDatabaseConfiguration().getHost()) + ")");
        }

        MigrationManager.setDatabaseConfiguration(databaseConnection.getDatabaseConfiguration());
        MigrationManager.execute(databaseConnection, logger);

        this.startBatchTask(this.plugin.getConfig().getInt("batch-task", 10));
    }

    private void startBatchTask(int seconds) {

        if (seconds <= 0) return;

        if (!isEnable()) return;

        this.plugin.getScheduler().runTimerAsync(() -> {
            this.storeOpenInventories();
            this.storePlayerData();
            this.cache.clearAll();
        }, seconds, seconds, TimeUnit.SECONDS);
    }

    private void storePlayerData() {

        if (!isEnable()) return;

        List<Schema> schemas = new ArrayList<>();

        var iterator = this.cache.get(DataDTO.class).iterator();
        while (iterator.hasNext()) {
            var dto = iterator.next();
            schemas.add(SchemaBuilder.upsert(Tables.PLAYER_DATAS, table -> {
                table.uuid("player_id", dto.player_id()).primary();
                table.string("key", dto.key()).primary();
                table.string("data", dto.data());
                table.object("expired_at", dto.expired_at() == null ? null : dto.expired_at());
            }));
            iterator.remove();
        }

        this.requestHelper.upsertMultiple(schemas);
    }

    private void storeOpenInventories() {

        if (!isEnable()) return;

        List<Schema> schemas = new ArrayList<>();
        var iterator = this.cache.get(PlayerOpenInventoryEvent.class).iterator();
        while (iterator.hasNext()) {
            var event = iterator.next();
            if (event != null) {
                schemas.add(SchemaBuilder.insert(Tables.PLAYER_OPEN_INVENTORIES, table -> {
                    table.uuid("player_id", event.getPlayer().getUniqueId());
                    table.string("plugin", event.getInventory().getPlugin().getName());
                    table.string("inventory", event.getInventory().getFileName());
                    table.bigInt("page", event.getPage());
                    StringBuilder inventoriesBuilder = new StringBuilder();
                    for (Inventory oldInventory : event.getOldInventories()) {
                        if (oldInventory != null) {
                            if (!inventoriesBuilder.isEmpty()) {
                                inventoriesBuilder.append(',');
                            }
                            inventoriesBuilder.append(oldInventory.getFileName());
                        }
                    }
                    table.string("old_inventories", inventoriesBuilder.toString());
                }));
            }
            iterator.remove();
        }
        this.requestHelper.insertMultiple(schemas);
    }

    @Override
    public boolean isEnable() {
        return this.isEnable;
    }

    @Override
    public void upsertData(@NonNull UUID uuid, @NonNull Data data) {
        if (!isEnable()) return;

        this.cache.get(DataDTO.class).removeIf(e -> e.player_id().equals(uuid) && e.key().equals(data.getKey()));
        this.cache.add(new DataDTO(uuid, data.getKey(), data.getValue().toString(), data.getExpiredAt() == 0 ? null : new Date(data.getExpiredAt())));
    }

    @Override
    public void clearData() {
        if (!isEnable()) return;

        this.cache.clear(DataDTO.class);
        this.plugin.getScheduler().runAsync(w -> this.requestHelper.delete(Tables.PLAYER_DATAS, table -> {
        }));
    }

    @Override
    public void clearData(@NonNull UUID uniqueId) {
        if (!isEnable()) return;

        this.cache.get(DataDTO.class).removeIf(e -> e.player_id().equals(uniqueId));
        this.plugin.getScheduler().runAsync(w -> this.requestHelper.delete(Tables.PLAYER_DATAS, table -> table.where("player_id", uniqueId)));
    }

    @Override
    public void clearData(@NonNull String key) {
        if (!isEnable()) return;

        this.cache.get(DataDTO.class).removeIf(e -> e.key().equals(key));
        this.plugin.getScheduler().runAsync(w -> this.requestHelper.delete(Tables.PLAYER_DATAS, table -> table.where("key", key)));
    }

    @Override
    public void removeData(@NonNull UUID uuid, @NonNull String key) {
        if (!isEnable()) return;

        this.cache.get(DataDTO.class).removeIf(e -> e.player_id().equals(uuid) && e.key().equals(key));
        this.plugin.getScheduler().runAsync(w -> this.requestHelper.delete(Tables.PLAYER_DATAS, table -> {
            table.where("player_id", uuid);
            table.where("key", key);
        }));
    }

    @Override
    public @NonNull List<DataDTO> loadPlayers() {
        return this.isEnable() ? this.requestHelper.selectAll(Tables.PLAYER_DATAS, DataDTO.class) : List.of();
    }

    @Override
    public @NonNull List<InventoryDTO> loadInventories() {
        return this.isEnable() ? this.requestHelper.selectAll(Tables.PLAYER_INVENTORIES, InventoryDTO.class) : List.of();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerOpenInventory(PlayerOpenInventoryEvent event) {

        if (!isEnable() || !Configuration.enablePlayerOpenInventoryLogs) return;

        this.cache.add(event);
    }

    @Override
    public void removeInventory(@NonNull UUID uuid) {

        if (!isEnable()) return;

        this.plugin.getScheduler().runAsync(w -> this.requestHelper.delete(Tables.PLAYER_INVENTORIES, table -> table.where("player_id", uuid)));
    }

    @Override
    public void storeInventory(@NonNull UUID uuid, @NonNull InventoryPlayer inventoryPlayer) {

        if (!isEnable()) return;

        this.plugin.getScheduler().runAsync(w -> this.requestHelper.insert(Tables.PLAYER_INVENTORIES, table -> {
            table.uuid("player_id", uuid);
            table.string("inventory", inventoryPlayer.toInventoryString());
        }));
    }
}
