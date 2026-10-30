package fr.maxlego08.menu.api.storage.spi;

import org.jetbrains.annotations.ApiStatus;

/**
 * What a {@link StorageProvider} can do, so zMenu can adapt to it.
 */
@ApiStatus.Experimental
public enum StorageCapability {

    /**
     * The data survives a restart.
     */
    PERSISTENT,

    /**
     * Several servers can use the same store at the same time.
     */
    SHARED,

    /**
     * {@link VariableStore#incrementAndGet} is atomic even when several servers add to the same
     * value at once.
     */
    ATOMIC_INCREMENT
}
