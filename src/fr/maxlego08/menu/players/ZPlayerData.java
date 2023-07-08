package fr.maxlego08.menu.players;

import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.PlayerData;

import java.util.*;

public class ZPlayerData implements PlayerData {

    private final UUID uniqueId;
    private final Map<String, Data> datas = new HashMap<>();

    /**
     * @param uniqueId
     */
    public ZPlayerData(UUID uniqueId) {
        super();
        this.uniqueId = uniqueId;
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public Collection<Data> getDatas() {
        return Collections.unmodifiableCollection(this.datas.values());
    }

    @Override
    public void addData(Data data) {
        this.datas.put(data.getKey(), data);
    }

    @Override
    public void removeData(Data data) {
        this.removeData(data.getKey());
    }

    @Override
    public void removeData(String key) {
        this.datas.remove(key);
    }

    @Override
    public boolean containsKey(String key) {
        this.clearExpiredData();
        return this.datas.containsKey(key);
    }

    @Override
    public Optional<Data> getData(String key) {
        this.clearExpiredData();
        return Optional.ofNullable(this.datas.getOrDefault(key, null));
    }

    private void clearExpiredData() {
        this.getDatas().stream().filter(Data::isExpired).forEach(this::removeData);
    }

}
