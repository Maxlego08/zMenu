package fr.maxlego08.menu.zcore.utils.discord;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class DiscordWebhookComponentV2 {
    private final String url;
    private String avatarUrl;
    private String username;
    private List<?> json;


    public DiscordWebhookComponentV2(String url){this.url = url;}

    public void setJson(List<?> json) {
        this.json = json;
    }
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void execute() throws IOException {
        if (json == null){
            throw new IllegalArgumentException("Json cannot be null");
        }

        JsonObject json = new JsonObject();
        if (this.username != null) json.addProperty("username", this.username);
        if (this.avatarUrl != null) json.addProperty("avatar_url", this.avatarUrl);

        json.addProperty("flags", 32768);
        json.add("components", new Gson().toJsonTree(this.json));
        URL url = URI.create(this.url+"?with_components=true").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");

        int responseCode = SendDiscordHttpRequest(json, connection);;
        if (responseCode != 204) {
            throw new IOException("Failed to send message: " + responseCode);
        }

        connection.getInputStream().close();
        connection.disconnect();

    }

    static int SendDiscordHttpRequest(JsonObject json, HttpURLConnection connection) throws IOException {
        connection.addRequestProperty("User-Agent", "Java-DiscordWebhook-BY-Gelox_");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        OutputStream stream = connection.getOutputStream();
        String jsonString = new Gson().toJson(json);

        //System.out.println("json: " + jsonString);

        stream.write(jsonString.getBytes(StandardCharsets.UTF_8));
        stream.flush();
        stream.close();

        return connection.getResponseCode();
    }
}
