package fr.maxlego08.menu.storage;

import fr.maxlego08.menu.api.storage.spi.StorageCapability;
import fr.maxlego08.menu.api.storage.spi.StorageProvider;
import fr.maxlego08.menu.storage.sql.SqlStorageProvider;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SqliteStorageProviderTest extends StorageProviderContractTest {

    @Override
    StorageProvider createProvider() {
        return new SqlStorageProvider();
    }

    @Override
    YamlConfiguration configuration() {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("storage-type", "SQLITE");
        configuration.set("database-configuration.table-prefix", "zmenu_");
        return configuration;
    }

    @Test
    void sqliteIsPersistentButNotShared() {
        assertEquals(EnumSet.of(StorageCapability.PERSISTENT, StorageCapability.ATOMIC_INCREMENT), this.provider.capabilities());
    }
}
