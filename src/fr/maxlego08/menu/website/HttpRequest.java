package fr.maxlego08.menu.website;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.zip.GZIPOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import sun.misc.Request;

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

			try {

				URL url = new URL(this.url);
				sun.net.www.protocol.https.HttpsURLConnectionImpl connection = (sun.net.www.protocol.https.HttpsURLConnectionImpl) url.openConnection();
				//URLConnection connection = (URLConnection) url.openConnection();

				System.out.println(this.data.toString());
				System.out.println(this.data);
				byte[] compressedData = this.compress(this.data.toString());
				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
				connection.setDoInput(true);
				connection.addRequestProperty("Accept", "application/json");
				connection.addRequestProperty("Connection", "close");
				connection.addRequestProperty("Content-Encoding", "gzip");
				connection.addRequestProperty("Content-Length", String.valueOf(compressedData.length));
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("User-Agent", "groupez/1");
				connection.setDoOutput(true);
				DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
				outputStream.write(compressedData);
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
				Map<String, Object> map = gson.fromJson(builder.toString(), Map.class);
				consumer.accept(map);

			} catch (Exception e) {

				e.printStackTrace();
				consumer.accept(new HashMap<String, Object>());

			}
		});
		
		
	}

	private byte[] compress(final String str) throws IOException {
		if (str == null) {
			return null;
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(outputStream);
		gzip.write(str.getBytes(StandardCharsets.UTF_8));
		gzip.close();
		return outputStream.toByteArray();
	}

}
