package fr.maxlego08.menu.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
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
 * @param serverId           {@code server-id}: the owner of this server's variables, empty to use a
 *                           generated one
 * @param messagingType      {@code messaging-type}: {@code AUTO} (the storage's own way, for a
 *                           database several servers share), {@code NONE}, or {@code SQL} (the
 *                           event table, even on SQLite)
 * @param pollIntervalMillis {@code messaging-poll-interval}: how often the event table is read
 * @param handoffTimeoutMillis {@code session-handoff-timeout}: how long a joining player waits for
 *                           the server they left to store their data
 */
public record StorageConfiguration(@NotNull String storageType, @NotNull String tablePrefix, @NotNull String host, int port,
                                   @NotNull String user, @NotNull String password, @NotNull String database, boolean debug,
                                   @Nullable Long maxLifetimeMillis, int batchSeconds, @NotNull String serverId,
                                   @NotNull String messagingType, long pollIntervalMillis, long handoffTimeoutMillis) {

    private static final String SERVER_ID_FILE = "server-id.txt";
    private static final int MAX_SERVER_ID_LENGTH = 64;

    /**
     * The id of this server: {@code server-id} when set, otherwise an id generated once and kept in
     * {@code server-id.txt}, so it survives restarts. Servers sharing a database need distinct ids.
     */
    public @NotNull String resolveServerId(@NotNull File dataFolder) {
        if (!this.serverId.isBlank()) return trim(this.serverId);

        Path file = dataFolder.toPath().resolve(SERVER_ID_FILE);
        try {
            if (Files.exists(file)) {
                String stored = Files.readString(file, StandardCharsets.UTF_8).trim();
                if (!stored.isEmpty()) return trim(stored);
            }
            String generated = "server-" + UUID.randomUUID().toString().substring(0, 8);
            Files.createDirectories(dataFolder.toPath());
            Files.writeString(file, generated, StandardCharsets.UTF_8);
            return generated;
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to read or write " + file, exception);
        }
    }

    private static String trim(String serverId) {
        String trimmed = serverId.trim();
        return trimmed.length() > MAX_SERVER_ID_LENGTH ? trimmed.substring(0, MAX_SERVER_ID_LENGTH) : trimmed;
    }

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
                configuration.getInt("batch-task", 10),
                configuration.getString("server-id", ""),
                configuration.getString("messaging-type", "AUTO"),
                Math.max(250L, (long) (configuration.getDouble("messaging-poll-interval", 2) * 1000)),
                Math.max(0L, configuration.getLong("session-handoff-timeout", 3000))
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
