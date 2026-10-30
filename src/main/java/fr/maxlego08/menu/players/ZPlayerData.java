package fr.maxlego08.menu.players;

import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.PlayerData;
import fr.maxlego08.menu.api.storage.StorageManager;
import fr.maxlego08.menu.api.storage.dto.DataDTO;
import fr.maxlego08.menu.api.storage.spi.PlayerDataEntry;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ZPlayerData implements PlayerData {

    private final StorageManager storageManager;
    private final UUID uniqueId;
    private final Map<String, Data> datas = new ConcurrentHashMap<>();
    private volatile boolean loaded;
    private volatile boolean online;
    private volatile long lastAccess = System.currentTimeMillis();

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
        this.storageManager.upsertData(this.uniqueId, data);
    }

    @Override
    public void removeData(@NonNull Data data) {
        this.removeData(data.getKey());
    }

    @Override
    public void removeData(@NonNull String key) {
        this.datas.remove(key);
        this.storageManager.removeData(this.uniqueId, key);
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
                this.storageManager.removeData(this.uniqueId, key.getKey());
                return true;
            }
            return false;
        });
    }

    public void setData(DataDTO dto) {
        this.datas.put(dto.key(), new ZData(dto));
    }

    /**
     * Adds the values read from the database. A value already in memory is newer and kept.
     */
    void merge(@NotNull List<PlayerDataEntry> entries) {
        for (PlayerDataEntry entry : entries) {
            this.datas.putIfAbsent(entry.key(), new ZData(entry.key(), entry.value(), entry.expiresAt()));
        }
        this.loaded = true;
    }

    /**
     * Forgets every value, without touching the database.
     */
    void clearLocal() {
        this.datas.clear();
    }

    /**
     * @return whether the values of this player have been read from the database
     */
    boolean isLoaded() {
        return this.loaded;
    }

    boolean isOnline() {
        return this.online;
    }

    void setOnline(boolean online) {
        this.online = online;
        this.touch();
    }

    void touch() {
        this.lastAccess = System.currentTimeMillis();
    }

    long getLastAccess() {
        return this.lastAccess;
    }
}
