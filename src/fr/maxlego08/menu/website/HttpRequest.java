package fr.maxlego08.menu.website;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class HttpRequest {

	private final String url;
	private final JsonObject data;

	/**
	 * @param url
	 * @param data
	 */
	public HttpRequest(String url, JsonObject data) {
		super();
		this.url = url;
		this.data = data;
	}

	public void submit(Plugin plugin, Consumer<Map<String, Object>> consumer) {

		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

			Map<String, Object> map;
			try {

				URL url = new URL(this.url);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();

				connection.setRequestMethod("POST");
				connection.addRequestProperty("Accept", "application/json");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setDoOutput(true);

				DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
				String jsonInputString = this.data.toString();
				byte[] input = jsonInputString.getBytes("utf-8");
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

				e.printStackTrace();
				consumer.accept(new HashMap<String, Object>());
				return;

			}

			consumer.accept(map == null ? new HashMap<>() : map);
		});

	}

}
