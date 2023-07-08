package fr.maxlego08.menu.website.request;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fr.maxlego08.menu.save.Config;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class HttpRequest {

    private final String url;
    private final JsonObject data;
    private String bearer;

    /**
     * @param url
     * @param data
     */
    public HttpRequest(String url, JsonObject data) {
        super();
        this.url = url;
        this.data = data;
    }

    public void setBearer(String bearer) {
        this.bearer = bearer;
    }

    public void submit(Plugin plugin, Consumer<Response> consumer) {

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            Map<String, Object> map = new HashMap<>();
            HttpURLConnection connection = null;
            int responseCode = -1;

            try {

                URL url = new URL(this.url);
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.addRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                if (this.bearer != null) {
                    connection.setRequestProperty("Authorization", "Bearer " + bearer);
                }

                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

                responseCode = connection.getResponseCode();

                String jsonInputString = this.data.toString();
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
                outputStream.flush();
                outputStream.close();

                InputStream inputStream = connection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line);
                }
                bufferedReader.close();

                Gson gson = new Gson();
                map = gson.fromJson(builder.toString(), Map.class);

            } catch (Exception e) {
                if (Config.enableDebug) {
                    e.printStackTrace();
                }

            }

            Response response = new Response(responseCode, map);
            consumer.accept(response);
        });

    }

}
