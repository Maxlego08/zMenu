package fr.maxlego08.menu.api.storage;

import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.inventory.InventoryPlayer;
import fr.maxlego08.menu.api.storage.dto.DataDTO;
import fr.maxlego08.menu.api.storage.dto.InventoryDTO;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface StorageManager extends Listener {

    void loadDatabase();

    /**
     * Writes every pending buffered record immediately, on the calling thread.
     *
     * <p>Records are normally buffered in memory and written by a periodic batch task. Anything
     * still buffered when the server stops is lost, which rolls player data back to the last batch.
     * This is called on shutdown, and optionally when a player disconnects, to close that window.</p>
     *
     * <p>Implementations without a write buffer can ignore this, which is why it is a default
     * no-op.</p>
     */
    default void flush() {
    }

    boolean isEnable();

    void upsertData(@NotNull UUID uuid,@NotNull Data data);

    void clearData();

    void clearData(@NotNull UUID uniqueId);

    void removeData(@NotNull UUID uuid,@NotNull String key);

    @NotNull
    List<DataDTO> loadPlayers();

    @NotNull
    List<InventoryDTO> loadInventories();

    void clearData(@NotNull String key);

    void storeInventory(@NotNull UUID uuid,@NotNull InventoryPlayer inventoryPlayer);

    void removeInventory(@NotNull UUID uuid);

    @NotNull
    Set<String> getVisitedWorlds(@NotNull UUID playerId);

    void markWorldVisited(@NotNull UUID playerId, @NotNull String worldName);
}
