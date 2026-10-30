package fr.maxlego08.menu.storage;

import fr.maxlego08.menu.api.ZMenuKeys;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StorageConfigurationTest {

    @Test
    void missingValuesUseTheDefaults() {
        StorageConfiguration configuration = StorageConfiguration.read(new YamlConfiguration(), null);

        assertEquals("SQLITE", configuration.storageType());
        assertEquals("zmenu_", configuration.tablePrefix());
        assertEquals(3306, configuration.port());
        assertEquals("zmenu", configuration.database());
        assertNull(configuration.maxLifetimeMillis());
        assertEquals(10, configuration.batchSeconds());
    }

    @Test
    void theSharedFileWinsOverConfigYml() {
        YamlConfiguration plugin = new YamlConfiguration();
        plugin.set("storage-type", "MYSQL");
        plugin.set("database-configuration.host", "plugin-host");
        plugin.set("database-configuration.user", "plugin-user");
        plugin.set("database-configuration.max-lifetime", 600);

        YamlConfiguration shared = new YamlConfiguration();
        shared.set("database-configuration.host", "shared-host");

        StorageConfiguration configuration = StorageConfiguration.read(plugin, shared);

        assertEquals("MYSQL", configuration.storageType());
        assertEquals("shared-host", configuration.host());
        assertEquals("plugin-user", configuration.user());
        assertEquals(600_000L, configuration.maxLifetimeMillis());
    }

    @Test
    void storageTypesSelectTheirProvider() {
        assertEquals(ZMenuKeys.key("sql"), ZStorageManager.providerKey("SQLITE"));
        assertEquals(ZMenuKeys.key("sql"), ZStorageManager.providerKey("mariadb"));
        assertEquals(ZMenuKeys.key("memory"), ZStorageManager.providerKey("NONE"));
        assertEquals(ZMenuKeys.parse("myaddon:redis"), ZStorageManager.providerKey("MyAddon:Redis"));
    }
}
