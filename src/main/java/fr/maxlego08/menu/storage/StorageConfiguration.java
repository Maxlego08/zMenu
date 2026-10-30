package fr.maxlego08.menu.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * The storage settings of {@code config.yml}.
 *
 * <p>The {@code database-configuration} values can also come from a {@code database-configuration.yml}
 * file in the server folder, shared by every plugin of the server. A value set in that file wins over
 * {@code config.yml}, and a value set in neither falls back to the default below. The previous reader
 * returned {@code null} for a missing value when the shared file did not exist.</p>
 *
 * @param storageType        {@code storage-type}: {@code SQLITE}, {@code MYSQL}, {@code MARIADB},
 *                           {@code NONE}, or the key of a provider an addon registers
 * @param tablePrefix        prefix of every table
 * @param host               database host
 * @param port               database port
 * @param user               database user
 * @param password           database password
 * @param database           database name
 * @param debug              whether every SQL statement is logged
 * @param maxLifetimeMillis  pooled connection lifetime, or null to keep the pool default
 * @param batchSeconds       {@code batch-task}: seconds between two writes of the buffered data
 */
public record StorageConfiguration(@NotNull String storageType, @NotNull String tablePrefix, @NotNull String host, int port,
                                   @NotNull String user, @NotNull String password, @NotNull String database, boolean debug,
                                   @Nullable Long maxLifetimeMillis, int batchSeconds) {

    private static final String SECTION = "database-configuration.";
    private static final String SHARED_FILE = "database-configuration.yml";

    public static @NotNull StorageConfiguration read(@NotNull ConfigurationSection configuration) {
        File sharedFile = new File(SHARED_FILE);
        ConfigurationSection shared = sharedFile.exists() ? YamlConfiguration.loadConfiguration(sharedFile) : null;
        return read(configuration, shared);
    }

    static @NotNull StorageConfiguration read(@NotNull ConfigurationSection configuration, @Nullable ConfigurationSection shared) {
        long maxLifetimeSeconds = number(configuration, shared, "max-lifetime", -1L);
        return new StorageConfiguration(
                configuration.getString("storage-type", "SQLITE"),
                text(configuration, shared, "table-prefix", "zmenu_"),
                text(configuration, shared, "host", "192.168.10.10"),
                (int) number(configuration, shared, "port", 3306L),
                text(configuration, shared, "user", "homestead"),
                text(configuration, shared, "password", "secret"),
                text(configuration, shared, "database", "zmenu"),
                flag(configuration, shared, "debug"),
                maxLifetimeSeconds < 0 ? null : TimeUnit.SECONDS.toMillis(maxLifetimeSeconds),
                configuration.getInt("batch-task", 10)
        );
    }

    private static String text(ConfigurationSection configuration, ConfigurationSection shared, String key, String fallback) {
        String path = SECTION + key;
        if (shared != null && shared.isSet(path)) return shared.getString(path, fallback);
        return configuration.getString(path, fallback);
    }

    private static long number(ConfigurationSection configuration, ConfigurationSection shared, String key, long fallback) {
        String path = SECTION + key;
        if (shared != null && shared.isSet(path)) return shared.getLong(path, fallback);
        return configuration.getLong(path, fallback);
    }

    private static boolean flag(ConfigurationSection configuration, ConfigurationSection shared, String key) {
        String path = SECTION + key;
        if (shared != null && shared.isSet(path)) return shared.getBoolean(path);
        return configuration.getBoolean(path);
    }
}
