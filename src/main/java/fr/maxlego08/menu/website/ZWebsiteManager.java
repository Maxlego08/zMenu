package fr.maxlego08.menu.website;

import com.google.gson.JsonObject;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.api.website.WebsiteManager;
import fr.maxlego08.menu.api.loader.NoneLoader;
import fr.maxlego08.menu.placeholder.LocalPlaceholder;
import fr.maxlego08.menu.website.buttons.ButtonBuilderRefresh;
import fr.maxlego08.menu.website.buttons.ButtonFolderBack;
import fr.maxlego08.menu.website.buttons.ButtonFolderNext;
import fr.maxlego08.menu.website.buttons.ButtonFolderPrevious;
import fr.maxlego08.menu.website.buttons.ButtonFolders;
import fr.maxlego08.menu.website.buttons.ButtonInventories;
import fr.maxlego08.menu.website.buttons.ButtonInventoryNext;
import fr.maxlego08.menu.website.buttons.ButtonInventoryPrevious;
import fr.maxlego08.menu.website.buttons.ButtonMarketplace;
import fr.maxlego08.menu.website.request.HttpRequest;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import fr.maxlego08.menu.zcore.utils.nms.NmsVersion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ZWebsiteManager extends ZUtils implements WebsiteManager {

    // private final String API_URL = "http://mib.test/api/v1/";
    private final String API_URL = "https://minecraft-inventory-builder.com/api/v1/";
    private final ZMenuPlugin plugin;
    private final List<Folder> folders = new ArrayList<>();
    private boolean isLogin = false;
    private boolean isDownloadResource = false;
    private long lastResourceUpdate = 0;
    private List<Resource> resources = new ArrayList<>();
    private int folderPage = 1;
    private int inventoryPage = 1;
    private int currentFolderId = -1;
    private int baseFolderId = 1;

    public ZWebsiteManager(ZMenuPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public void login(CommandSender sender, String token) {
        if (token == null) {
            message(this.plugin, sender, Message.WEBSITE_LOGIN_ERROR_TOKEN);
            return;
        }

        if (Token.token != null) {
            message(this.plugin, sender, Message.WEBSITE_LOGIN_ERROR_ALREADY);
            return;
        }

        String[] parts = token.split("\\|");
        if (parts.length != 2) {
            message(this.plugin, sender, Message.WEBSITE_LOGIN_ERROR_TOKEN);
            return;
        }

        String code = parts[1];
        if (code.length() < 40) {
            message(this.plugin, sender, Message.WEBSITE_LOGIN_ERROR_TOKEN);
            return;
        }

        if (this.isLogin) {
            message(this.plugin, sender, Message.WEBSITE_LOGIN_PROCESS);
            return;
        }

        this.isLogin = true;

        message(this.plugin, sender, Message.WEBSITE_LOGIN_PROCESS);

        JsonObject data = new JsonObject();
        HttpRequest request = new HttpRequest(this.API_URL + "auth/test", data);
        request.setBearer(token);
        request.submit(this.plugin, map -> {
            this.isLogin = false;
            boolean status = map.getOrDefault("status", false);
            if (status) {
                Token.token = token;
                Token.getInstance().save(this.plugin.getPersist());
                message(this.plugin, sender, Message.WEBSITE_LOGIN_SUCCESS);
            } else {
                message(this.plugin, sender, Message.WEBSITE_LOGIN_ERROR_INFO);
            }
        });

    }

    @Override
    public void disconnect(CommandSender sender) {

        if (Token.token == null) {
            message(this.plugin, sender, Message.WEBSITE_DISCONNECT_ERROR);
            return;
        }

        Token.token = null;
        Token.getInstance().save(this.plugin.getPersist());
        message(this.plugin, sender, Message.WEBSITE_DISCONNECT_SUCCESS);

    }

    @Override
    public void openMarketplace(Player player) {

        if (Token.token == null) {
            message(this.plugin, player, Message.WEBSITE_NOT_CONNECT);
            return;
        }


        // Pas besoin de télécharger les resources
        if (this.lastResourceUpdate > System.currentTimeMillis()) {

            openMarketplaceInventory(player);
        } else {

            message(this.plugin, player, Message.WEBSITE_MARKETPLACE_WAIT);

            if (this.isDownloadResource) return;
            this.isDownloadResource = true;

            JsonObject data = new JsonObject();
            HttpRequest request = new HttpRequest(this.API_URL + "resources", data);
            request.setBearer(Token.token);
            request.setMethod("GET");
            request.submit(this.plugin, map -> {

                this.isDownloadResource = false;
                this.lastResourceUpdate = System.currentTimeMillis() + 1000 * 60 * 15;

                List<Map<String, Object>> maps = (List<Map<String, Object>>) map.get("resources");

                List<Resource> loadedResources = new ArrayList<>(maps.size());
                for (Map<String, Object> resourceMap : maps) {
                    loadedResources.add(new Resource(resourceMap));
                }
                this.resources = loadedResources;

                this.plugin.getScheduler().runAtEntity(player, w -> openMarketplaceInventory(player));
            });
        }
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void registerPlaceholders() {
        LocalPlaceholder localPlaceholder = LocalPlaceholder.getInstance();
        localPlaceholder.register("marketplace_resources", (a, b) -> String.valueOf(this.resources.size()));
    }

    public void openMarketplaceInventory(Player player) {
        this.plugin.getInventoryManager().openInventory(player, this.plugin, "marketplace");
    }

    public void openInventoriesInventory(Player player, int inventorypage, int folderPage, int currentFolderId) {
        this.inventoryPage = inventorypage;
        this.folderPage = folderPage;
        this.currentFolderId = currentFolderId;
        this.plugin.getInventoryManager().openInventory(player, this.plugin, "inventories");
    }

    public void loadButtons(ButtonManager buttonManager) {
        buttonManager.register(new NoneLoader(this.plugin, ButtonMarketplace.class, "zmenu_marketplace_resources"));

        buttonManager.register(new NoneLoader(this.plugin, ButtonFolders.class, "zmenu_builder_folders"));
        buttonManager.register(new NoneLoader(this.plugin, ButtonFolderNext.class, "zmenu_builder_folder_next"));
        buttonManager.register(new NoneLoader(this.plugin, ButtonFolderPrevious.class, "zmenu_builder_folder_previous"));
        buttonManager.register(new NoneLoader(this.plugin, ButtonFolderBack.class, "zmenu_builder_folder_back"));

        buttonManager.register(new NoneLoader(this.plugin, ButtonBuilderRefresh.class, "zmenu_builder_refresh"));
        buttonManager.register(new NoneLoader(this.plugin, ButtonInventories.class, "zmenu_builder_inventories"));
        buttonManager.register(new NoneLoader(this.plugin, ButtonInventoryPrevious.class, "zmenu_builder_inventory_previous"));
        buttonManager.register(new NoneLoader(this.plugin, ButtonInventoryNext.class, "zmenu_builder_inventory_next"));
    }

    private void loadFiles() {

        List<String> files = new ArrayList<>();

        // files.add("website/marketplace.yml");
        files.add("website/inventories.yml");

        files.forEach(filePath -> {
            if (NmsVersion.nmsVersion.isNewMaterial()) {
                if (!new File(plugin.getDataFolder(), filePath).exists()) {
                    this.plugin.saveResource(filePath.replace("website/", "website/1_13/"), filePath, true);
                }
            } else {
                this.plugin.saveResource(filePath, !Config.enableDebug);
            }
        });
    }

    public void loadInventories(InventoryManager inventoryManager) {

        this.loadFiles();

        try {
            // inventoryManager.loadInventory(this.plugin, "website/marketplace.yml", InventoryMarketplace.class);
            inventoryManager.loadInventory(this.plugin, "website/inventories.yml");
        } catch (InventoryException exception) {
            exception.printStackTrace();
        }
    }

    public void fetchInventories(Player player) {

        if (Token.token == null) {
            message(this.plugin, player, Message.WEBSITE_NOT_CONNECT);
            return;
        }

        if (!this.folders.isEmpty()) {
            openInventoriesInventory(player, 1, 1, this.baseFolderId);
            return;
        }

        message(this.plugin, player, Message.WEBSITE_MARKETPLACE_WAIT);

        JsonObject data = new JsonObject();
        HttpRequest request = new HttpRequest(this.API_URL + "inventories", data);
        request.setBearer(Token.token);
        request.setMethod("GET");
        request.submit(this.plugin, map -> {

            boolean status = map.getOrDefault("status", false);
            if (status) {
                List<Map<String, Object>> folderMaps = (List<Map<String, Object>>) map.get("folders");

                this.folders.clear();
                for (Map<String, Object> folderMap : folderMaps) {
                    Folder folder = Folder.fromMap(folderMap);
                    this.folders.add(folder);
                }

                int baseId = -1;
                for (Folder folder : this.folders) {
                    if (folder.parentId() == -1) {
                        baseId = folder.id();
                        break;
                    }
                }
                this.baseFolderId = baseId;

                this.plugin.getScheduler().runAtEntity(player, w -> openInventoriesInventory(player, 1, 1, this.baseFolderId));
            } else {
                message(this.plugin, player, Message.WEBSITE_MARKETPLACE_ERROR);
            }
        });
    }

    public int getFolderPage() {
        return folderPage;
    }

    public void setFolderPage(int folderPage) {
        this.folderPage = folderPage;
    }

    public int getInventoryPage() {
        return inventoryPage;
    }

    public void setInventoryPage(int inventoryPage) {
        this.inventoryPage = inventoryPage;
    }

    public Optional<Folder> getCurrentFolder() {
        for (Folder folder : this.folders) {
            if (folder.id() == currentFolderId) {
                return Optional.of(folder);
            }
        }
        return Optional.empty();
    }

    public Optional<Folder> getFolder(int id) {
        for (Folder folder : this.folders) {
            if (folder.id() == id) {
                return Optional.of(folder);
            }
        }
        return Optional.empty();
    }

    public List<Folder> getFolders(Folder folder) {
        List<Folder> children = new ArrayList<>();
        for (Folder current : this.folders) {
            if (current.parentId() == folder.id()) {
                children.add(current);
            }
        }
        return children;
    }

    public void loadPlaceholders() {
        LocalPlaceholder placeholder = LocalPlaceholder.getInstance();
        placeholder.register("folder_name", (player, args) -> {
            Optional<Folder> optional = getCurrentFolder();
            return optional.isPresent() ? optional.get().name() : "Not found";
        });
    }

    private String getFolderPath(Folder folder, String path) {
        if (folder.parentId() == -1) {
            return path;
        }
        Optional<Folder> optional = getFolder(folder.parentId());
        if (optional.isPresent()) {
            Folder parrentFolder = optional.get();
            return getFolderPath(parrentFolder, folder.name() + "/" + path);
        }
        return folder.name() + "/" + path;
    }

    private File getFolderPath(Inventory inventory) {
        Optional<Folder> optional = getFolder(inventory.folderId());
        return optional.map(folder -> new File(this.plugin.getDataFolder(), "inventories/" + getFolderPath(folder, ""))).orElseGet(() -> new File(this.plugin.getDataFolder(), "inventories"));
    }

    public void downloadInventory(Player player, Inventory inventory, boolean forceDownload) {

        File folder = getFolderPath(inventory);
        File file = new File(folder, inventory.fileName() + ".yml");

        if (file.exists() && !forceDownload) {
            message(this.plugin, player, Message.WEBSITE_INVENTORY_EXIST);
            return;
        }

        player.closeInventory();
        message(this.plugin, player, Message.WEBSITE_INVENTORY_WAIT, "%name%", inventory.fileName());

        HttpRequest request = new HttpRequest(this.API_URL + String.format("inventory/%s/download", inventory.id()), new JsonObject());
        request.setBearer(Token.token);
        request.setMethod("GET");

        folder.mkdirs();

        request.submitForFileDownload(this.plugin, file, isSuccess -> message(this.plugin, player, isSuccess ? Message.WEBSITE_INVENTORY_SUCCESS : Message.WEBSITE_INVENTORY_ERROR, "%name%", inventory.fileName()));
    }

    public void refreshInventories(Player player) {
        this.folders.clear();
        this.fetchInventories(player);
    }

    private String getFileNameFromUrl(String url) {
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException exception) {
            exception.printStackTrace();
            return null;
        }
        String path = uri.getPath();
        String[] segments = path.split("/");
        if (segments.length > 0) {
            return segments[segments.length - 1];
        } else {
            return null;
        }
    }

    @Override
    public void downloadFromUrl(CommandSender sender, String baseUrl, boolean force) {

        message(this.plugin, sender, Message.WEBSITE_DOWNLOAD_START);
        plugin.getScheduler().runAsync(w -> {

            try {
                String finalUrl = followRedirection(baseUrl);

                // ToDo, add a configuration for "allowed urls"

                URL url = new URL(finalUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                String fileName = getFileNameFromContentDisposition(httpURLConnection);

                if (fileName == null) {
                    message(this.plugin, sender, Message.WEBSITE_DOWNLOAD_ERROR_NAME);
                    return;
                }

                if (!isYmlFile(httpURLConnection) && !fileName.endsWith(".yml")) {
                    message(this.plugin, sender, Message.WEBSITE_DOWNLOAD_ERROR_TYPE);
                    return;
                }

                File folder = new File(this.plugin.getDataFolder(), "inventories/downloads");
                if (!folder.exists()) folder.mkdirs();
                File file = new File(folder, fileName);

                if (file.exists() && !force) {
                    message(this.plugin, sender, Message.WEBSITE_INVENTORY_EXIST);
                    return;
                }

                HttpRequest request = new HttpRequest(finalUrl, new JsonObject());
                request.setMethod("GET");

                request.submitForFileDownload(this.plugin, file, isSuccess -> message(this.plugin, sender, isSuccess ? Message.WEBSITE_INVENTORY_SUCCESS : Message.WEBSITE_INVENTORY_ERROR, "%name%", fileName));
            } catch (IOException exception) {
                exception.printStackTrace();
                message(this.plugin, sender, Message.WEBSITE_DOWNLOAD_ERROR_CONSOLE);
            }
        });
    }

    private String followRedirection(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setInstanceFollowRedirects(true);
        int status = conn.getResponseCode();
        if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM
                || status == HttpURLConnection.HTTP_SEE_OTHER) {
            return conn.getHeaderField("Location");
        }
        return urlString;
    }

    private boolean isYmlFile(HttpURLConnection connection) throws IOException {
        String contentType = connection.getContentType();
        return contentType.contains("text/yaml") || contentType.contains("application/x-yaml");
    }

    private String getFileNameFromContentDisposition(HttpURLConnection conn) {
        String contentDisposition = conn.getHeaderField("Content-Disposition");
        if (contentDisposition != null) {
            int index = contentDisposition.indexOf("filename=");
            if (index > 0) {
                return contentDisposition.substring(index + 9).replaceAll("\"", "");
            }
        }
        return generateRandomString(16);
    }
}
