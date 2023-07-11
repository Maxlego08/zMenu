package fr.maxlego08.menu.zcore.utils.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.players.ZData;
import fr.maxlego08.menu.zcore.ZPlugin;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class DataAdapter extends TypeAdapter<Data> {

    private static final String KEY = "key";
    private static final String VALUE = "value";
    private static final String EXPIRED_AT = "expiredAt";
    private static final Type seriType = new TypeToken<Map<String, Object>>() {
    }.getType();
    private final ZPlugin plugin;

    /**
     * @param plugin
     */
    public DataAdapter(ZPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public void write(JsonWriter jsonWriter, Data location) throws IOException {
        if (location == null) {
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.value(getRaw(location));
    }

    @Override
    public Data read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }
        return fromRaw(jsonReader.nextString());
    }

    private String getRaw(Data data) {
        Map<String, Object> serial = new HashMap<String, Object>();

        serial.put(KEY, data.getKey());
        serial.put(VALUE, data.getValue());
        serial.put(EXPIRED_AT, data.getExpiredAt());

        return plugin.getGson().toJson(serial);
    }

    private Data fromRaw(String raw) {
        Map<String, Object> keys = this.plugin.getGson().fromJson(raw, seriType);

        String key = (String) keys.get(KEY);
        Object object = keys.get(VALUE);
        Number expiredAt = (Number) keys.get(EXPIRED_AT);

        return new ZData(key, object, expiredAt.longValue());
    }

}
