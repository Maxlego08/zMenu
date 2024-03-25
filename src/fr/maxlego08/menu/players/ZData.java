package fr.maxlego08.menu.players;

import fr.maxlego08.menu.api.players.Data;

public class ZData implements Data {

    private final String key;
    private Object value;
    private final long expiredAt;

    /**
     * @param key
     * @param value
     * @param expiredAt
     */
    public ZData(String key, Object value, long expiredAt) {
        super();
        this.key = key;
        this.value = value;
        this.expiredAt = expiredAt;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public long getExpiredAt() {
        return this.expiredAt;
    }

    @Override
    public boolean isExpired() {
        return this.expiredAt != 0 && System.currentTimeMillis() > this.expiredAt;
    }

    @Override
    public void add(int amount) {
        int value = Integer.parseInt(this.value.toString());
        this.value = value + amount;
    }

    @Override
    public void remove(int amount) {
        int value = Integer.parseInt(this.value.toString());
        this.value = value - amount;
    }

}
