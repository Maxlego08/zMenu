package fr.maxlego08.menu.players;

import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.PlayerData;
import fr.maxlego08.menu.api.storage.StorageManager;
import fr.maxlego08.menu.api.storage.dto.DataDTO;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ZPlayerData implements PlayerData {

    private final StorageManager storageManager;
    private final UUID uniqueId;
    private final Map<String, Data> datas = new HashMap<>();

    public ZPlayerData(@NotNull StorageManager storageManager,@NotNull UUID uniqueId) {
        super();
        this.storageManager = storageManager;
        this.uniqueId = uniqueId;
    }

    @Override
    public @NonNull UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public @NonNull Collection<Data> getDatas() {
        return Collections.unmodifiableCollection(this.datas.values());
    }

    @Override
    public void addData(@NonNull Data data) {
        this.datas.put(data.getKey(), data);
        this.storageManager.upsertData(uniqueId, data);
    }

    @Override
    public void removeData(@NonNull Data data) {
        this.removeData(data.getKey());
    }

    @Override
    public void removeData(@NonNull String key) {
        this.datas.remove(key);
        this.storageManager.removeData(uniqueId, key);
    }

    @Override
    public boolean containsKey(@NonNull String key) {
        this.clearExpiredData();
        return this.datas.containsKey(key);
    }

    @Override
    public @NonNull Optional<Data> getData(@NonNull String key) {
        this.clearExpiredData();
        return Optional.ofNullable(this.datas.getOrDefault(key, null));
    }

    private void clearExpiredData() {
        this.datas.values().removeIf(key -> {
            if (key.isExpired()) {
                this.storageManager.removeData(uniqueId, key.getKey());
                return true;
            }
            return false;
        });
    }

    public void setData(DataDTO dto) {
        this.datas.put(dto.key(), new ZData(dto));
    }
}
