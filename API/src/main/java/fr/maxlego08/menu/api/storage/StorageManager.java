package fr.maxlego08.menu.api.storage;

import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.storage.dto.DataDTO;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;

public interface StorageManager extends Listener {

    void loadDatabase();

    boolean isEnable();

    void upsertData(UUID uuid, Data data);

    void clearData();

    void clearData(UUID uniqueId);

    void removeData(UUID uuid, String key);

    List<DataDTO> loadPlayers();

    void clearData(String key);
}
