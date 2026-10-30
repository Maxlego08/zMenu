package fr.maxlego08.menu.storage;

import fr.maxlego08.menu.api.storage.spi.StorageProvider;
import fr.maxlego08.menu.storage.memory.MemoryStorageProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MemoryStorageProviderTest extends StorageProviderContractTest {

    @Override
    StorageProvider createProvider() {
        return new MemoryStorageProvider();
    }

    @Test
    void memoryIsNeitherPersistentNorShared() {
        assertTrue(this.provider.capabilities().isEmpty());
    }
}
