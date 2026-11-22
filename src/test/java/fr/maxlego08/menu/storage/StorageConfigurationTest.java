package fr.maxlego08.menu.storage;

import fr.maxlego08.menu.api.ZMenuKeys;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

class StorageConfigurationTest {

    @Test
    void missingValuesUseTheDefaults() {
        StorageConfiguration configuration = StorageConfiguration.read(YamlSections.empty(), null);

        assertEquals("SQLITE", configuration.storageType());
        assertEquals("zmenu_", configuration.tablePrefix());
        assertEquals(3306, configuration.port());
        assertEquals("zmenu", configuration.database());
        assertNull(configuration.maxLifetimeMillis());
        assertEquals(10, configuration.batchSeconds());
    }

    @Test
    void theSharedFileWinsOverConfigYml() {
        YamlDocument plugin = YamlSections.empty();
        plugin.set("storage-type", "MYSQL");
        plugin.set("database-configuration.host", "plugin-host");
        plugin.set("database-configuration.user", "plugin-user");
        plugin.set("database-configuration.max-lifetime", 600);

        YamlDocument shared = YamlSections.empty();
        shared.set("database-configuration.host", "shared-host");

        StorageConfiguration configuration = StorageConfiguration.read(plugin, shared);

        assertEquals("MYSQL", configuration.storageType());
        assertEquals("shared-host", configuration.host());
        assertEquals("plugin-user", configuration.user());
        assertEquals(600_000L, configuration.maxLifetimeMillis());
    }

    @Test
    void theServerIdIsGeneratedOnceWhenNotSet(@TempDir File folder) {
        StorageConfiguration empty = StorageConfiguration.read(YamlSections.empty(), null);
        String generated = empty.resolveServerId(folder);

        assertTrue(generated.startsWith("server-"));
        assertEquals(generated, empty.resolveServerId(folder));

        YamlDocument configuration = YamlSections.empty();
        configuration.set("server-id", " lobby-1 ");
        assertEquals("lobby-1", StorageConfiguration.read(configuration, null).resolveServerId(folder));
    }

    @Test
    void storageTypesSelectTheirProvider() {
        assertEquals(ZMenuKeys.key("sql"), ZStorageManager.providerKey("SQLITE"));
        assertEquals(ZMenuKeys.key("sql"), ZStorageManager.providerKey("mariadb"));
        assertEquals(ZMenuKeys.key("memory"), ZStorageManager.providerKey("NONE"));
        assertEquals(ZMenuKeys.parse("myaddon:redis"), ZStorageManager.providerKey("MyAddon:Redis"));
    }
}
