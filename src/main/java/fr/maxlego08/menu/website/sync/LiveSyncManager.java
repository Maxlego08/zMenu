package fr.maxlego08.menu.website.sync;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.common.utils.ZUtils;
import fr.maxlego08.menu.common.utils.cache.YamlFileCache;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.website.request.HttpRequest;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.logger.Logger.LogType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * zMenu Live Sync — opens an EPHEMERAL WebSocket to the website's custom relay on demand. When the user
 * clicks "Sync" on the site, the relay forwards a tiny notification; the plugin downloads the inventory
 * over the authenticated API, verifies its hash, atomically swaps the file and reloads that single
 * inventory on the main thread.
 * <p>
 * The socket is opened by /zmenu connect and is dropped by the relay after a short idle window, so at
 * scale only the servers actively syncing hold a connection. The relay never sees inventory data: it only
 * forwards {@code {inventory_id, file_name, hash}}; the YAML is pulled over HTTPS and hash-verified.
 * <p>
 * Every step logs to the console with a {@code [Live Sync]} prefix. The token is never logged.
 */
public class LiveSyncManager extends ZUtils {

    static final String LOG_PREFIX = "[Live Sync] ";
    private static final String EVENT_SYNC = "inventory.sync";
    private static final long MAX_YAML_BYTES = 512L * 1024L;
    private static final long DEFAULT_PAIR_TTL_SECONDS = 600L;
    private static final int CONNECTION_LOST_TIMEOUT_SECONDS = 30;

    private final ZMenuPlugin plugin;
    private final String apiUrl;
    private final Gson gson = new Gson();
    private final java.util.Map<String, String> lastAppliedHash = new ConcurrentHashMap<>();

    private LiveSyncConfig config;
    private WebSocketClient client;
    private volatile boolean connecting;
    private volatile boolean connected;

    // Device-flow pairing state.
    private volatile boolean pairing;
    private String deviceCode;
    private int pollSeconds = 5;
    private long pairDeadline;

    public LiveSyncManager(ZMenuPlugin plugin, String apiUrl) {
        super();
        this.plugin = plugin;
        this.apiUrl = apiUrl;
    }

    static void log(String message) {
        Logger.info(LOG_PREFIX + message, LogType.INFO);
    }

    static void log(String message, LogType type) {
        Logger.info(LOG_PREFIX + message, type);
    }

    // ------------------------------------------------------------------ //
    // Lifecycle
    // ------------------------------------------------------------------ //

    public void onEnable() {
        this.config = this.plugin.getPersist().loadOrSaveDefault(new LiveSyncConfig(), LiveSyncConfig.class, "live-sync");
        if (this.config == null) {
            this.config = new LiveSyncConfig();
        }
        // Ephemeral model: do NOT auto-connect on start. The operator runs /zmenu connect on demand.
    }

    public void onDisable() {
        this.pairing = false;
        this.closeSocket();
    }

    public boolean isLinked() {
        return this.config != null && this.config.isLinked();
    }

    // ------------------------------------------------------------------ //
    // Pairing — OAuth 2.0 Device Authorization Grant (RFC 8628)
    // ------------------------------------------------------------------ //

    public void startDeviceFlow(CommandSender sender) {
        if (this.isLinked()) {
            log("Pairing requested but this server is already linked (use /zmenu connect).", LogType.WARNING);
            this.message(this.plugin, sender, Message.WEBSITE_SYNC_ALREADY_LINKED);
            return;
        }
        if (this.pairing) {
            log("Pairing requested but a pairing is already in progress.", LogType.WARNING);
            this.message(this.plugin, sender, Message.WEBSITE_SYNC_PAIR_PENDING);
            return;
        }

        log("Starting device-flow pairing against " + this.apiUrl + "zmenu/pair/start ...");
        this.message(this.plugin, sender, Message.WEBSITE_SYNC_PAIR_START);

        JsonObject body = new JsonObject();
        body.addProperty("server_name", this.plugin.getServer().getName());
        body.addProperty("plugin_version", this.plugin.getDescription().getVersion());

        HttpRequest request = new HttpRequest(this.apiUrl + "zmenu/pair/start", body);
        request.setMethod("POST");
        request.submit(this.plugin, response -> {
            if (response.getCode() != 200) {
                log("Pairing start failed: HTTP " + response.getCode() + " (is the API URL correct and reachable?).", LogType.ERROR);
                this.message(this.plugin, sender, Message.WEBSITE_SYNC_PAIR_ERROR);
                return;
            }

            String userCode = (String) response.get("user_code");
            String url = (String) response.get("verification_url_complete");
            if (url == null) {
                url = (String) response.get("verification_url");
            }
            this.deviceCode = (String) response.get("device_code");
            this.pollSeconds = asInt(response.get("interval"), 5);
            long ttl = asInt(response.get("expires_in"), (int) DEFAULT_PAIR_TTL_SECONDS);

            if (this.deviceCode == null || userCode == null) {
                log("Pairing start returned an incomplete response.", LogType.ERROR);
                this.message(this.plugin, sender, Message.WEBSITE_SYNC_PAIR_ERROR);
                return;
            }

            this.pairing = true;
            this.pairDeadline = System.currentTimeMillis() + (ttl * 1000L);

            log("Pairing started — code " + userCode + ", verification url " + url + ".", LogType.SUCCESS);
            this.message(this.plugin, sender, Message.WEBSITE_SYNC_PAIR_CODE, "%code%", userCode, "%url%", url == null ? "" : url);
            this.scheduleNextPoll(sender);
        });
    }

    private void scheduleNextPoll(CommandSender sender) {
        if (!this.pairing) {
            return;
        }
        this.plugin.getScheduler().runLater(() -> this.pollOnce(sender), this.pollSeconds, TimeUnit.SECONDS);
    }

    private void pollOnce(CommandSender sender) {
        if (!this.pairing || this.deviceCode == null) {
            return;
        }
        if (System.currentTimeMillis() > this.pairDeadline) {
            this.pairing = false;
            this.deviceCode = null;
            log("Pairing expired — no approval within the time limit.", LogType.WARNING);
            this.message(this.plugin, sender, Message.WEBSITE_SYNC_PAIR_EXPIRED);
            return;
        }

        JsonObject body = new JsonObject();
        body.addProperty("device_code", this.deviceCode);

        HttpRequest request = new HttpRequest(this.apiUrl + "zmenu/pair/poll", body);
        request.setMethod("POST");
        request.submit(this.plugin, response -> {
            if (!this.pairing) {
                return;
            }
            int code = response.getCode();
            if (code == 202) { // authorization_pending
                this.scheduleNextPoll(sender);
                return;
            }
            if (code == 200) {
                this.handlePairSuccess(sender, response);
                return;
            }

            this.pairing = false;
            this.deviceCode = null;
            String reason = code == 410 ? " (code expired or already used)" : code == 404 ? " (unknown code)" : code == 403 ? " (denied)" : "";
            log("Pairing failed: HTTP " + code + reason + ".", LogType.ERROR);
            this.message(this.plugin, sender, code == 410 ? Message.WEBSITE_SYNC_PAIR_EXPIRED : Message.WEBSITE_SYNC_PAIR_ERROR);
        });
    }

    private void handlePairSuccess(CommandSender sender, fr.maxlego08.menu.website.request.Response response) {
        this.pairing = false;
        this.deviceCode = null;

        String token = (String) response.get("token");
        String wsUrl = (String) response.get("ws_url");
        String connectionId = (String) response.get("connection_id");

        if (token == null || wsUrl == null) {
            log("Pairing was approved but the response was incomplete (missing token/ws_url).", LogType.ERROR);
            this.message(this.plugin, sender, Message.WEBSITE_SYNC_PAIR_ERROR);
            return;
        }

        LiveSyncConfig cfg = this.config != null ? this.config : new LiveSyncConfig();
        cfg.token = token;
        cfg.wsUrl = wsUrl;
        cfg.connectionId = connectionId;
        this.config = cfg;
        this.config.save(this.plugin.getPersist());

        log("Server linked successfully. Relay " + wsUrl + ".", LogType.SUCCESS);
        this.message(this.plugin, sender, Message.WEBSITE_SYNC_PAIR_SUCCESS);

        // Open the first ephemeral window right away so the user can sync immediately.
        this.connect(sender);
    }

    // ------------------------------------------------------------------ //
    // Connection (ephemeral)
    // ------------------------------------------------------------------ //

    public void connect(CommandSender sender) {
        if (!this.isLinked()) {
            log("Connect requested but the server is not linked yet. Run /zmenu login first.", LogType.WARNING);
            this.message(this.plugin, sender, Message.WEBSITE_SYNC_NOT_LINKED);
            return;
        }
        if (this.connecting || this.connected) {
            log("Connect requested but the live connection is already open/opening.", LogType.WARNING);
            this.message(this.plugin, sender, Message.WEBSITE_SYNC_ALREADY_CONNECTED);
            return;
        }

        this.connecting = true;
        log("Opening live connection to " + this.config.wsUrl + " ...");
        this.message(this.plugin, sender, Message.WEBSITE_SYNC_CONNECTING);

        this.plugin.getScheduler().runAsync(w -> this.openSocket(sender));
    }

    private void openSocket(CommandSender sender) {
        try {
            URI uri = new URI(this.config.wsUrl);

            WebSocketClient socket = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    log("Socket open — authenticating...");
                    JsonObject hello = new JsonObject();
                    hello.addProperty("type", "hello");
                    hello.addProperty("token", config.token);
                    try {
                        this.send(hello.toString());
                    } catch (Exception e) {
                        log("Failed to send hello: " + e.getMessage() + ".", LogType.ERROR);
                    }
                }

                @Override
                public void onMessage(String message) {
                    handleRelayMessage(sender, message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    connected = false;
                    connecting = false;
                    log("Live connection closed (code " + code + (reason != null && !reason.isEmpty() ? ", " + reason : "") + ").", LogType.WARNING);
                }

                @Override
                public void onError(Exception ex) {
                    connecting = false;
                    log("Live connection error: " + ex.getMessage() + ".", LogType.ERROR);
                    if (Configuration.enableDebug) {
                        ex.printStackTrace();
                    }
                }
            };

            if ("wss".equalsIgnoreCase(uri.getScheme())) {
                socket.setSocketFactory((SSLSocketFactory) SSLSocketFactory.getDefault());
            }
            socket.setConnectionLostTimeout(CONNECTION_LOST_TIMEOUT_SECONDS);

            this.client = socket;
            socket.connect();

        } catch (Throwable throwable) {
            this.connecting = false;
            log("Failed to open the live connection: " + throwable.getMessage() + ".", LogType.ERROR);
            this.message(this.plugin, sender, Message.WEBSITE_SYNC_CONNECT_ERROR);
            if (Configuration.enableDebug) {
                throwable.printStackTrace();
            }
        }
    }

    private void handleRelayMessage(CommandSender sender, String message) {
        if (message == null) {
            return;
        }
        JsonObject obj;
        try {
            obj = this.gson.fromJson(message, JsonObject.class);
        } catch (Exception e) {
            return;
        }
        if (obj == null || !obj.has("type")) {
            return;
        }

        switch (obj.get("type").getAsString()) {
            case "welcome":
                this.connected = true;
                this.connecting = false;
                log("Live sync connected — ready to receive syncs.", LogType.SUCCESS);
                this.message(this.plugin, sender, Message.WEBSITE_SYNC_CONNECTED);
                break;
            case "error":
                this.handleRelayError(sender, obj.has("error") ? obj.get("error").getAsString() : "error");
                break;
            case EVENT_SYNC:
                this.applySync(obj);
                break;
            case "pong":
            default:
                break;
        }
    }

    private void handleRelayError(CommandSender sender, String error) {
        this.connecting = false;
        log("Relay rejected the connection: " + error + ".", LogType.ERROR);
        this.message(this.plugin, sender, Message.WEBSITE_SYNC_AUTH_FAILED);

        if ("unauthorized".equals(error)) {
            // The token is invalid/revoked — forget the link so the operator can re-link cleanly.
            this.unlink();
            log("The stored link was cleared (token no longer valid). Run /zmenu login to re-link.", LogType.WARNING);
        }
    }

    public void disconnect(CommandSender sender) {
        boolean wasActive = this.connected || this.connecting || this.pairing;

        this.pairing = false;
        this.deviceCode = null;
        this.closeSocket();

        log("Live sync " + (wasActive ? "disconnected." : "was not active."));
        this.message(this.plugin, sender, wasActive ? Message.WEBSITE_SYNC_DISCONNECTED : Message.WEBSITE_SYNC_NOT_CONNECTED);
    }

    private void unlink() {
        this.closeSocket();
        if (this.config != null) {
            this.config.token = null;
            this.config.wsUrl = null;
            this.config.connectionId = null;
            this.config.save(this.plugin.getPersist());
        }
    }

    private void closeSocket() {
        this.connected = false;
        this.connecting = false;
        if (this.client != null) {
            try {
                this.client.close();
            } catch (Exception ignored) {
            }
            this.client = null;
        }
    }

    // ------------------------------------------------------------------ //
    // Notification -> download -> verify -> reload
    // ------------------------------------------------------------------ //

    private void applySync(JsonObject data) {
        if (!data.has("inventory_id") || !data.has("file_name") || !data.has("hash")) {
            log("Ignored a sync notification: missing inventory_id/file_name/hash.", LogType.WARNING);
            return;
        }

        int inventoryId = data.get("inventory_id").getAsInt();
        String fileName = data.get("file_name").getAsString();
        String hash = data.get("hash").getAsString();

        // Defence in depth: never let a remote file_name escape the inventories directory.
        if (fileName == null || !fileName.matches("[A-Za-z0-9_\\- ]{1,64}")) {
            log("Ignored a sync notification: invalid file name '" + fileName + "'.", LogType.WARNING);
            return;
        }

        log("Received sync notification for inventory '" + fileName + "' (id " + inventoryId + ").");

        if (hash != null && hash.equalsIgnoreCase(this.lastAppliedHash.get(fileName.toLowerCase(Locale.ROOT)))) {
            log("Inventory '" + fileName + "' is already up to date, nothing to do.");
            return;
        }

        this.downloadAndApply(inventoryId, fileName, hash);
    }

    private void downloadAndApply(int inventoryId, String fileName, String hash) {
        File tmpDir = new File(this.plugin.getDataFolder(), "live-sync");
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        File tempFile = new File(tmpDir, fileName + ".tmp");

        log("Downloading inventory '" + fileName + "' (id " + inventoryId + ")...");

        HttpRequest request = new HttpRequest(this.apiUrl + "zmenu/inventory/" + inventoryId + "/download", new JsonObject());
        request.setBearer(this.config.token);
        request.setMethod("GET");
        request.submitForFileDownload(this.plugin, tempFile, success -> {
            if (!success) {
                deleteQuietly(tempFile);
                log("Download failed for '" + fileName + "' (id " + inventoryId + "). Token revoked/expired, or inventory not owned by this account.", LogType.ERROR);
                this.message(this.plugin, Bukkit.getConsoleSender(), Message.WEBSITE_SYNC_APPLY_ERROR, "%name%", fileName);
                return;
            }
            if (tempFile.length() <= 0 || tempFile.length() > MAX_YAML_BYTES) {
                log("Rejected '" + fileName + "': downloaded size out of bounds (" + tempFile.length() + " bytes).", LogType.ERROR);
                deleteQuietly(tempFile);
                return;
            }

            String actual = sha256(tempFile);
            if (actual == null || hash == null || !actual.equalsIgnoreCase(hash)) {
                log("Rejected '" + fileName + "': hash mismatch (expected " + hash + ", got " + actual + ").", LogType.ERROR);
                deleteQuietly(tempFile);
                this.message(this.plugin, Bukkit.getConsoleSender(), Message.WEBSITE_SYNC_APPLY_ERROR, "%name%", fileName);
                return;
            }

            try {
                YamlConfiguration test = YamlConfiguration.loadConfiguration(tempFile);
                if (test.getKeys(false).isEmpty()) {
                    log("Rejected '" + fileName + "': downloaded file is empty or not valid YAML.", LogType.ERROR);
                    deleteQuietly(tempFile);
                    return;
                }
            } catch (Exception exception) {
                log("Rejected '" + fileName + "': failed to parse the downloaded YAML.", LogType.ERROR);
                deleteQuietly(tempFile);
                return;
            }

            File invDir = new File(this.plugin.getDataFolder(), "inventories");
            if (!invDir.exists()) {
                invDir.mkdirs();
            }
            File target = new File(invDir, fileName + ".yml");
            File backup = new File(invDir, fileName + ".yml.bak");

            try {
                if (target.exists()) {
                    Files.copy(target.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                try {
                    Files.move(tempFile.toPath(), target.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception atomicFailure) {
                    Files.move(tempFile.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (Exception exception) {
                log("Failed to write '" + fileName + ".yml': " + exception.getMessage() + ".", LogType.ERROR);
                deleteQuietly(tempFile);
                this.message(this.plugin, Bukkit.getConsoleSender(), Message.WEBSITE_SYNC_APPLY_ERROR, "%name%", fileName);
                return;
            }

            this.reloadOnMainThread(fileName, target, backup, hash);
        });
    }

    private void reloadOnMainThread(String fileName, File target, File backup, String hash) {
        this.plugin.getScheduler().runNextTick(w -> {
            InventoryManager inventoryManager = this.plugin.getInventoryManager();
            YamlFileCache.invalidateCache(target.toPath());

            boolean applied;
            try {
                Optional<Inventory> existing = inventoryManager.getInventory(fileName);
                if (existing.isPresent()) {
                    Inventory inventory = existing.get();
                    this.plugin.getVInventoryManager().close(v -> {
                        InventoryDefault inventoryDefault = (InventoryDefault) v;
                        return !inventoryDefault.isClose() && inventoryDefault.getMenuInventory().equals(inventory);
                    });
                    inventoryManager.reloadInventory(inventory);
                } else {
                    inventoryManager.loadInventory(this.plugin, target);
                }
                applied = true;
            } catch (InventoryException exception) {
                applied = false;
                log("zMenu failed to load the synced inventory '" + fileName + "': " + exception.getMessage() + ".", LogType.ERROR);
                if (Configuration.enableDebug) {
                    exception.printStackTrace();
                }
            }

            if (applied) {
                this.lastAppliedHash.put(fileName.toLowerCase(Locale.ROOT), hash);
                log("Inventory '" + fileName + "' synced and reloaded.", LogType.SUCCESS);
                this.message(this.plugin, Bukkit.getConsoleSender(), Message.WEBSITE_SYNC_APPLIED, "%name%", fileName);
            } else {
                this.rollback(inventoryManager, fileName, target, backup);
                log("Rolled back '" + fileName + "' to the previous version.", LogType.WARNING);
                this.message(this.plugin, Bukkit.getConsoleSender(), Message.WEBSITE_SYNC_APPLY_ERROR, "%name%", fileName);
            }
        });
    }

    private void rollback(InventoryManager inventoryManager, String fileName, File target, File backup) {
        try {
            if (!backup.exists()) {
                return;
            }
            Files.copy(backup.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            YamlFileCache.invalidateCache(target.toPath());
            Optional<Inventory> old = inventoryManager.getInventory(fileName);
            if (old.isPresent()) {
                inventoryManager.reloadInventory(old.get());
            } else {
                inventoryManager.loadInventory(this.plugin, target);
            }
        } catch (Exception exception) {
            log("Rollback of '" + fileName + "' failed: " + exception.getMessage() + ".", LogType.ERROR);
        }
    }

    // ------------------------------------------------------------------ //
    // Helpers
    // ------------------------------------------------------------------ //

    private static int asInt(Object value, int fallback) {
        return value instanceof Number ? ((Number) value).intValue() : fallback;
    }

    private static void deleteQuietly(File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    private static String sha256(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(Files.readAllBytes(file.toPath()));
            StringBuilder builder = new StringBuilder(hashBytes.length * 2);
            for (byte b : hashBytes) {
                builder.append(Character.forDigit((b >> 4) & 0xF, 16));
                builder.append(Character.forDigit(b & 0xF, 16));
            }
            return builder.toString();
        } catch (Exception exception) {
            return null;
        }
    }
}
