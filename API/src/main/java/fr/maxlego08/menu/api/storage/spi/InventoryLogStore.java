package fr.maxlego08.menu.api.storage.spi;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The log of the inventories players open, kept when {@code enable-player-open-inventory-logs}
 * is on.
 */
@ApiStatus.Experimental
public interface InventoryLogStore {

    @NotNull
    CompletableFuture<Void> append(@NotNull List<InventoryOpenRecord> records);
}
