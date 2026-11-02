package fr.maxlego08.menu.storage;

import org.bukkit.configuration.file.YamlConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/**
 * Real database servers for the opt-in tests. Each backend is tested only when its system property
 * names a server, for example with the containers of {@code docker run}:
 *
 * <pre>
 * ./gradlew :test -Dzmenu.it.mysql=127.0.0.1:53306 -Dzmenu.it.mariadb=127.0.0.1:53307
 *     -Dzmenu.it.postgresql=127.0.0.1:55432 -Dzmenu.it.redis=127.0.0.1:56379 -Dzmenu.it.mongodb=127.0.0.1:57017
 * </pre>
 *
 * SQL servers need a database {@code sarah} with the user {@code sarah}, password {@code sarah}.
 * Their zMenu tables are dropped before each test; Redis and MongoDB tests use a key prefix or a
 * database of their own.
 */
public final class RealDatabases {

    public static final String MYSQL = "zmenu.it.mysql";
    public static final String MARIADB = "zmenu.it.mariadb";
    public static final String POSTGRESQL = "zmenu.it.postgresql";
    public static final String REDIS = "zmenu.it.redis";
    public static final String MONGODB = "zmenu.it.mongodb";

    private static final String[] TABLES = {"variables", "saved_inventories", "storage_events", "player_sessions", "servers", "player_datas",
            "player_inventories", "first_world_join", "player_animations", "player_open_inventories", "migrations"};

    private RealDatabases() {
    }

    private static String host(String property) {
        return System.getProperty(property).split(":")[0];
    }

    private static int port(String property) {
        return Integer.parseInt(System.getProperty(property).split(":")[1]);
    }

    /**
     * @param storageType {@code MYSQL}, {@code MARIADB} or {@code POSTGRESQL}
     */
    public static YamlConfiguration sql(String storageType, String property) {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("storage-type", storageType);
        configuration.set("database-configuration.host", host(property));
        configuration.set("database-configuration.port", port(property));
        configuration.set("database-configuration.user", "sarah");
        configuration.set("database-configuration.password", "sarah");
        configuration.set("database-configuration.database", "sarah");
        configuration.set("database-configuration.table-prefix", "zmenu_");
        configuration.set("messaging-poll-interval", 0.25);
        return configuration;
    }

    /**
     * Drops every zMenu table, so a test starts from an empty database.
     */
    public static void dropSqlTables(String storageType, String property) throws SQLException {
        String scheme = switch (storageType) {
            case "MYSQL" -> "mysql";
            case "MARIADB" -> "mariadb";
            default -> "postgresql";
        };
        String url = "jdbc:" + scheme + "://" + host(property) + ":" + port(property) + "/sarah";
        try (Connection connection = DriverManager.getConnection(url, "sarah", "sarah"); Statement statement = connection.createStatement()) {
            for (String table : TABLES) {
                statement.execute("DROP TABLE IF EXISTS zmenu_" + table);
            }
        }
    }

    public static YamlConfiguration redis() {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("storage-type", "REDIS");
        configuration.set("redis.uri", "redis://" + System.getProperty(REDIS) + "/0");
        configuration.set("redis.key-prefix", "test-" + UUID.randomUUID());
        return configuration;
    }

    public static YamlConfiguration mongodb() {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("storage-type", "MONGODB");
        configuration.set("mongodb.uri", "mongodb://" + System.getProperty(MONGODB));
        configuration.set("mongodb.database", "test_" + UUID.randomUUID().toString().replace("-", ""));
        configuration.set("messaging-poll-interval", 0.25);
        return configuration;
    }
}
