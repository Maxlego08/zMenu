package fr.maxlego08.menu.storage;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.api.event.events.PlayerOpenInventoryEvent;
import fr.maxlego08.menu.api.storage.StorageManager;
import fr.maxlego08.menu.api.storage.Tables;
import fr.maxlego08.menu.storage.migrations.PlayerOpenInventoryMigration;
import fr.maxlego08.menu.zcore.utils.GlobalDatabaseConfiguration;
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
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ZStorageManager implements StorageManager {

    private final MenuPlugin plugin;
    private final List<PlayerOpenInventoryEvent> inventoryEvents = new ArrayList<>();
    private RequestHelper requestHelper;
    private boolean isEnable = true;

    public ZStorageManager(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void loadDatabase() {

        MigrationManager.setMigrationTableName("zmenu_migrations");
        MigrationManager.registerMigration(new PlayerOpenInventoryMigration());

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
            isEnable = false;
            return;
        }

        DatabaseConnection databaseConnection;
        if (storageType.equalsIgnoreCase("SQLITE")) {
            databaseConnection = new SqliteConnection(new DatabaseConfiguration(prefix, user, password, port, host, dataBase, enableDebug, DatabaseType.SQLITE), this.plugin.getDataFolder());
        } else {
            databaseConnection = new HikariDatabaseConnection(new DatabaseConfiguration(prefix, user, password, port, host, dataBase, enableDebug, DatabaseType.MYSQL));
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

        boolean enableTask = Config.enablePlayerOpenInventoryLogs;
        if (!enableTask) return;

        this.plugin.getScheduler().runTimer(() -> {
            List<Schema> schemas = new ArrayList<>();
            var iterator = this.inventoryEvents.iterator();
            while (iterator.hasNext()) {
                var event = iterator.next();
                schemas.add(SchemaBuilder.insert(Tables.PLAYER_OPEN_INVENTORIES, table -> {
                    table.uuid("player_id", event.getPlayer().getUniqueId());
                    table.string("plugin", event.getInventory().getPlugin().getName());
                    table.string("inventory", event.getInventory().getFileName());
                    table.bigInt("page", event.getPage());
                    table.string("old_inventories", event.getOldInventories().stream().map(Inventory::getFileName).collect(Collectors.joining(",")));
                }));
                iterator.remove();
            }
            this.requestHelper.insertMultiple(schemas);

        }, seconds, seconds, TimeUnit.SECONDS);
    }

    @Override
    public boolean isEnable() {
        return this.isEnable;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandQuit(PlayerOpenInventoryEvent event) {

        if (!isEnable() || !Config.enablePlayerOpenInventoryLogs) return;

        this.inventoryEvents.add(event);
    }
}
