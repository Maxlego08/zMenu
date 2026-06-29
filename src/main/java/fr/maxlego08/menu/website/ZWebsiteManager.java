package fr.maxlego08.menu.website;

import com.google.gson.JsonObject;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.api.website.DisallowedHostException;
import fr.maxlego08.menu.api.website.DownloadResult;
import fr.maxlego08.menu.api.website.WebsiteManager;
import fr.maxlego08.menu.common.utils.ZUtils;
import fr.maxlego08.menu.website.request.HttpRequest;
import fr.maxlego08.menu.website.sync.LiveSyncManager;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.file.Paths;
import java.util.Locale;

public class ZWebsiteManager extends ZUtils implements WebsiteManager {

    private final String API_URL;

    private final ZMenuPlugin plugin;
    private final LiveSyncManager liveSyncManager;

    public ZWebsiteManager(ZMenuPlugin plugin) {
        super();
        this.plugin = plugin;
        this.API_URL = plugin.getConfig().getString("api-url", "https://minecraft-inventory-builder.com/api/v2/");
        this.liveSyncManager = new LiveSyncManager(plugin, this.API_URL);
    }

    /**
     * @return the live-sync manager (WebSocket link + push-to-reload pipeline).
     */
    public LiveSyncManager getLiveSyncManager() {
        return this.liveSyncManager;
    }

    @Override
    public void onEnable() {
        this.liveSyncManager.onEnable();
    }

    @Override
    public void onDisable() {
        this.liveSyncManager.onDisable();
    }

    @Override
    public boolean isLinked() {
        return this.liveSyncManager.isLinked();
    }

    @Override
    public void startDeviceFlow(CommandSender sender) {
        this.liveSyncManager.startDeviceFlow(sender);
    }

    @Override
    public void connect(CommandSender sender) {
        this.liveSyncManager.connect(sender);
    }

    @Override
    public void forceUnlink(CommandSender sender) {
        this.liveSyncManager.forceUnlink(sender);
    }

    @Override
    public String getApiUrl() {
        return this.API_URL;
    }

    private String getHostFromUrl(String urlString) {
        try {
            return new URI(urlString).toURL().getHost();
        } catch (MalformedURLException | URISyntaxException e) {
            return null;
        }
    }

    @Override
    public void downloadFromUrl(@NonNull CommandSender sender, @NonNull String baseUrl, boolean force) {

        message(this.plugin, sender, Message.WEBSITE_DOWNLOAD_START);
        this.plugin.getScheduler().runAsync(w -> {
            DownloadResult result = performDownload(baseUrl, force, sender);
            switch (result) {
                case SUCCESS -> {
                }
                case ERROR_HOST_NOT_ALLOWED -> {
                    String host = getHostFromUrl(baseUrl);
                    message(this.plugin, sender, Message.WEBSITE_DOWNLOAD_ERROR_HOST, "%host%", host != null ? host : "<invalid>", "%allowed%", String.join(", ", Configuration.allowedDownloadableWebsite));
                }
                case ERROR_IO -> message(this.plugin, sender, Message.WEBSITE_DOWNLOAD_ERROR_CONSOLE);
                case ERROR_INVALID_FILE_TYPE -> message(this.plugin, sender, Message.WEBSITE_DOWNLOAD_ERROR_TYPE);
                case ERROR_FILE_ALREADY_EXISTS -> message(this.plugin, sender, Message.WEBSITE_INVENTORY_EXIST);
            }
        });
    }

    private DownloadResult performDownload(String baseUrl, boolean force, CommandSender sender) {
        try {
            String finalUrl = followRedirection(baseUrl);

            String fileName;
            URL url = new URL(finalUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setInstanceFollowRedirects(false);
            try {
                if (!isYmlFile(httpURLConnection)) {
                    return DownloadResult.ERROR_INVALID_FILE_TYPE;
                }

                fileName = getFileNameFromContentDisposition(httpURLConnection);
            } finally {
                httpURLConnection.disconnect();
            }

            File folder = new File(this.plugin.getDataFolder(), "inventories/downloads");
            if (!folder.exists()) folder.mkdirs();
            File file = new File(folder, fileName);

            if (file.exists() && !force) {
                return DownloadResult.ERROR_FILE_ALREADY_EXISTS;
            }

            final String finalFileName = fileName;
            HttpRequest request = new HttpRequest(finalUrl, new JsonObject());
            request.setMethod("GET");
            request.submitForFileDownload(this.plugin, file, isSuccess -> message(this.plugin, sender, isSuccess ? Message.WEBSITE_INVENTORY_SUCCESS : Message.WEBSITE_INVENTORY_ERROR, "%name%", finalFileName));

            return DownloadResult.SUCCESS;
        } catch (DisallowedHostException exception) {
            exception.printStackTrace();
            return DownloadResult.ERROR_HOST_NOT_ALLOWED;
        } catch (IOException | URISyntaxException exception) {
            exception.printStackTrace();
            return DownloadResult.ERROR_IO;
        }
    }

    private boolean isValidHost(String urlString) throws IOException {
        String host = getHostFromUrl(urlString);
        if (host == null || host.isBlank()) {
            return false;
        }
        final String lowerCaseHost = host.toLowerCase(Locale.ROOT);
        return Configuration.allowedDownloadableWebsite.stream().map(String::toLowerCase).anyMatch(lowerCaseHost::equals);
    }

    private String followRedirection(String urlString) throws IOException, URISyntaxException {
        return resolveRedirectChain(urlString, 0);
    }

    private String resolveRedirectChain(String urlString, int hopCount) throws IOException, URISyntaxException {
        if (hopCount > 10) {
            throw new IOException("Too many redirects (>10 hops)");
        }

        if (!isValidHost(urlString)) {
            throw new DisallowedHostException("Disallowed host in redirect chain");
        }

        URL url = new URI(urlString).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setInstanceFollowRedirects(false);
        int status = conn.getResponseCode();

        if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER) {
            String location = conn.getHeaderField("Location");
            if (location == null || location.isBlank()) {
                throw new IOException("Redirect has no Location header");
            }
            return resolveRedirectChain(location, hopCount + 1);
        }

        return urlString;
    }

    private boolean isYmlFile(HttpURLConnection connection) throws IOException {
        String contentType = connection.getContentType();
        return contentType.contains("text/yaml") || contentType.contains("application/x-yaml");
    }

    private String getFileNameFromContentDisposition(HttpURLConnection conn) {
        String contentDisposition = conn.getHeaderField("Content-Disposition");
        String fileName = null;

        if (contentDisposition != null) {
            int index = contentDisposition.indexOf("filename=");
            if (index > 0) {
                fileName = contentDisposition.substring(index + 9).replaceAll("\"", "").trim();
            }
        }

        if (fileName != null && !fileName.isBlank()) {
            fileName = Paths.get(fileName).getFileName().toString();
            if (fileName.matches("[a-zA-Z0-9_\\-]{1,64}\\.yml")) {
                return fileName;
            }
        }

        return generateRandomString(16) + ".yml";
    }
}