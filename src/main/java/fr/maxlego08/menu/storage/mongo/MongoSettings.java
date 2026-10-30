package fr.maxlego08.menu.storage.mongo;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code mongodb} section of {@code config.yml}.
 *
 * @param uri              the connection string, such as {@code mongodb://user:password@localhost:27017}
 * @param database         the database zMenu writes into
 * @param collectionPrefix the start of every collection name
 */
public record MongoSettings(@NotNull String uri, @NotNull String database, @NotNull String collectionPrefix) {

    public static @NotNull MongoSettings read(@NotNull ConfigurationSection configuration) {
        return new MongoSettings(
                configuration.getString("mongodb.uri", "mongodb://localhost:27017"),
                configuration.getString("mongodb.database", "zmenu"),
                configuration.getString("mongodb.collection-prefix", "zmenu_")
        );
    }
}
