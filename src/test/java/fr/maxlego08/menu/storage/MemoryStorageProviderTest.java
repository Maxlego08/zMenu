package fr.maxlego08.menu.storage;

import fr.maxlego08.menu.api.storage.spi.StorageCapability;
import fr.maxlego08.menu.api.storage.spi.StorageProvider;
import fr.maxlego08.menu.storage.memory.MemoryStorageProvider;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MemoryStorageProviderTest extends StorageProviderContractTest {

    @Override
    StorageProvider createProvider() {
        return new MemoryStorageProvider();
    }

    @Test
    void memoryIsNeitherPersistentNorShared() {
        assertEquals(EnumSet.of(StorageCapability.ATOMIC_INCREMENT, StorageCapability.EXPORT), this.provider.capabilities());
    }
}
