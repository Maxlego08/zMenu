package fr.maxlego08.menu.website.request;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.save.Config;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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

    private String method = "POST";

    public HttpRequest(String url, JsonObject data) {
        this.url = url;
        this.data = data;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setBearer(String bearer) {
        this.bearer = bearer;
    }

    public void submit(MenuPlugin plugin, Consumer<Response> consumer) {
        plugin.getScheduler().runTaskAsynchronously(() -> {
            Map<String, Object> map = new HashMap<>();
            HttpURLConnection connection;
            int responseCode = -1;

            try {
                URL url = new URL(this.url);
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod(this.method);
                connection.addRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/json");

                if (this.bearer != null) {
                    connection.setRequestProperty("Authorization", "Bearer " + this.bearer);
                }

                if ("POST".equals(this.method) || "PUT".equals(this.method)) {
                    connection.setDoOutput(true);
                    try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                        String jsonInputString = this.data.toString();
                        byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                        outputStream.write(input, 0, input.length);
                    }
                }

                responseCode = connection.getResponseCode();

                try (InputStream inputStream = connection.getInputStream(); BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        builder.append(line);
                    }
                    Gson gson = new Gson();
                    map = gson.fromJson(builder.toString(), Map.class);
                }

            } catch (Exception exception) {
                if (Config.enableDebug) {
                    exception.printStackTrace();
                }
            }

            Response response = new Response(responseCode, map);
            consumer.accept(response);
        });
    }

    public void submitForFileDownload(MenuPlugin plugin, File fileOut, Consumer<Boolean> consumer) {
        plugin.getScheduler().runTaskAsynchronously(() -> {
            HttpURLConnection connection;

            try {
                URL url = new URL(this.url);
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod(this.method);
                connection.addRequestProperty("Accept", "application/yaml");

                if (this.bearer != null) connection.setRequestProperty("Authorization", "Bearer " + this.bearer);

                if ("POST".equals(this.method) || "PUT".equals(this.method)) {
                    connection.setDoOutput(true);
                    try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                        String jsonInputString = this.data.toString();
                        byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                        outputStream.write(input, 0, input.length);
                    }
                }

                try (InputStream inputStream = connection.getInputStream(); FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }
                    consumer.accept(true);
                }

            } catch (Exception exception) {
                exception.printStackTrace();
                consumer.accept(false);
            }
        });
    }

}
