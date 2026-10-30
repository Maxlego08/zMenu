package fr.maxlego08.menu.variables;

import fr.maxlego08.menu.api.storage.spi.StorageProvider;
import fr.maxlego08.menu.storage.sql.SqlStorageProvider;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Two servers sharing one SQLite file with {@code messaging-type: SQL}, which turns on the event
 * table and the sessions that MySQL, MariaDB and PostgreSQL use.
 */
class MultiServerTest extends TwoServerContractTest {

    @Override
    StorageProvider newProvider() {
        return new SqlStorageProvider();
    }

    @Override
    YamlConfiguration configuration() {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("storage-type", "SQLITE");
        configuration.set("messaging-type", "SQL");
        configuration.set("messaging-poll-interval", 0.25);
        return configuration;
    }
}
