package fr.maxlego08.menu.players;

import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.storage.dto.DataDTO;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class ZData implements Data {

    private final String key;
    private final long expiredAt;
    private Object value;

    public ZData(@NotNull String key, Object value, long expiredAt) {
        super();
        this.key = key;
        this.value = value;
        this.expiredAt = expiredAt;
    }

    public ZData(@NotNull DataDTO dto) {
        assert dto.key() != null;
        this.key = dto.key();
        this.value = dto.data();
        this.expiredAt = dto.expired_at() == null ? 0 : dto.expired_at().getTime();
    }

    @Override
    public @NonNull String getKey() {
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
        int value = safeStringToInt(this.value.toString());
        this.value = value + amount;
    }

    @Override
    public void remove(int amount) {
        int value = safeStringToInt(this.value.toString());
        this.value = value - amount;
    }

    @Override
    public void negate() {
        this.value = -Integer.parseInt(this.value.toString());
    }

    private int safeStringToInt(String str) {
        if (str.contains(".")) {
            double doubleValue = Double.parseDouble(str);
            return (int) doubleValue;
        } else {
            return Integer.parseInt(str);
        }
    }

}
