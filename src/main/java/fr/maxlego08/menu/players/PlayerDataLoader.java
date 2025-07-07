package fr.maxlego08.menu.players;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.maxlego08.menu.api.players.Data;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PlayerDataLoader {

    public static Map<UUID, List<Data>> loadPlayerData(String path) throws IOException {
        Map<UUID, List<Data>> result = new HashMap<>();
        Gson gson = new Gson();

        JsonObject root = JsonParser.parseReader(new FileReader(path)).getAsJsonObject();
        JsonObject players = root.getAsJsonObject("players");

        for (Map.Entry<String, JsonElement> entry : players.entrySet()) {
            UUID uuid = UUID.fromString(entry.getKey());
            JsonObject playerObject = entry.getValue().getAsJsonObject();
            JsonObject datas = playerObject.getAsJsonObject("datas");

            List<Data> dataList = new ArrayList<>();

            for (Map.Entry<String, JsonElement> dataEntry : datas.entrySet()) {
                String jsonString = dataEntry.getValue().getAsString();
                JsonObject inner = JsonParser.parseString(jsonString).getAsJsonObject();

                String key = inner.get("key").getAsString();
                long expiredAt = inner.get("expiredAt").getAsLong();
                String value = inner.get("value").getAsString();

                dataList.add(new ZData(key, value, expiredAt));
            }

            result.put(uuid, dataList);
        }

        return result;
    }
}
