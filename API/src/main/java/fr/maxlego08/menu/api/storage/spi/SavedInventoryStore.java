package fr.maxlego08.menu.api.storage.spi;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The player inventories zMenu takes away while a menu with {@code clear-inventory} is open, so
 * they can be given back after a crash.
 */
@ApiStatus.Experimental
public interface SavedInventoryStore {

    /**
     * @return every saved inventory, encoded as {@code InventoryPlayer#toInventoryString()} does
     */
    @NotNull
    CompletableFuture<Map<UUID, String>> loadAll();

    /**
     * @return the saved inventory of a player, or empty when there is none
     */
    @NotNull
    CompletableFuture<Optional<String>> load(@NotNull UUID playerId);

    /**
     * Saves the inventory of a player, replacing any saved before.
     *
     * @param inventory the encoded inventory
     */
    @NotNull
    CompletableFuture<Void> save(@NotNull UUID playerId, @NotNull String inventory);

    @NotNull
    CompletableFuture<Void> delete(@NotNull UUID playerId);
}
