package fr.maxlego08.menu.api.storage;

import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.inventory.InventoryPlayer;
import fr.maxlego08.menu.api.storage.dto.DataDTO;
import fr.maxlego08.menu.api.storage.dto.InventoryDTO;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public interface StorageManager extends Listener {

    void loadDatabase();

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
}
