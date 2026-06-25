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
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class LiveSyncManager extends ZUtils {

    static final String LOG_PREFIX = "[Live Sync] ";
    private static final String EVENT_SYNC = "inventory.sync";
    private static final long MAX_YAML_BYTES = 512L * 1024L;
    private static final long DEFAULT_PAIR_TTL_SECONDS = 600L;
    private static final int CONNECTION_LOST_TIMEOUT_SECONDS = 30;
    private static final int MAX_RECONNECT_ATTEMPTS = 10;
    private static final long RECONNECT_BASE_SECONDS = 5L;
    private static final long MAX_RECONNECT_DELAY_SECONDS = 60L;

    private final ZMenuPlugin plugin;
    private final String apiUrl;
    private final Gson gson = new Gson();
    private final java.util.Map<String, String> lastAppliedHash = new ConcurrentHashMap<>();

    private LiveSyncConfig config;
    private WebSocketClient client;
    private volatile boolean connecting;
    private volatile boolean connected;
    private volatile boolean shouldStayConnected;
    private int reconnectAttempts;

    private volatile boolean pairing;
    private String deviceCode;
    private int pollSeconds = 5;
    private long pairDeadline;

    public LiveSyncManager(ZMenuPlugin plugin, String apiUrl) {
        super();
        this.plugin = plugin;
        this.apiUrl = apiUrl;
    }

    private void log(String message) {
        this.plugin.getLogger().info(message);
    }

    private void log(Level level, String message) {
        this.plugin.getLogger().log(level, message);
    }

    private void warning(String message) {
        this.log(Level.WARNING, message);
    }

    private void severe(String message) {
        this.log(Level.SEVERE, message);
    }

    private void success(String message) {
        this.log(Level.INFO, message);
    }

    /**
     * This method is called when the plugin is enabled.
     */
    public void onEnable() {
        this.config = this.plugin.getPersist().loadOrSaveDefault(new LiveSyncConfig(), LiveSyncConfig.class, "live-sync");
        if (this.config == null) {
            this.config = new LiveSyncConfig();
        }
    }

    /**
     * This method is called when the plugin is disabled.
     */
    public void onDisable() {
        this.pairing = false;
        this.shouldStayConnected = false;
        this.closeSocket();
    }

    public boolean isLinked() {
        return this.config != null && this.config.isLinked();
    }

    // ------------------------------------------------------------------ //
    // Connection (ephemeral)
    // ------------------------------------------------------------------ //

    /**
     * This server's stable id (server_id), generated and persisted on first use and kept across
     * unlink/relink so the website always recognises the same server and never duplicates its connection.
     */
    private String ensureServerId() {
        if (this.config == null) {
            this.config = new LiveSyncConfig();
        }
        if (this.config.serverId == null || this.config.serverId.isEmpty()) {
            this.config.serverId = UUID.randomUUID().toString();
            this.config.save(this.plugin.getPersist());
        }
        return this.config.serverId;
    }

    /**
     * This method is called when the player requests a pairing.
     *
     * @param sender The player who requested the pairing.
     */
    public void startDeviceFlow(CommandSender sender) {
        if (this.isLinked()) {
            warning("Pairing requested but this server is already linked (use /zmenu connect).");
            this.message(this.plugin, sender, Message.WEBSITE_SYNC_ALREADY_LINKED);
            return;
        }

        if (this.pairing) {
            warning("Pairing requested but a pairing is already in progress.");
            this.message(this.plugin, sender, Message.WEBSITE_SYNC_PAIR_PENDING);
            return;
        }

        log("Starting device-flow pairing against " + this.apiUrl + "zmenu/pair/start ...");
        this.message(this.plugin, sender, Message.WEBSITE_SYNC_PAIR_START);

        JsonObject body = new JsonObject();
        body.addProperty("server_name", this.plugin.getServer().getName());
        body.addProperty("plugin_version", this.plugin.getDescription().getVersion());
        body.addProperty("server_id", this.ensureServerId());

        HttpRequest request = new HttpRequest(this.apiUrl + "zmenu/pair/start", body);
        request.setMethod("POST");
        request.submit(this.plugin, response -> {
            if (response.getCode() != 200) {
                severe("Pairing start failed: HTTP " + response.getCode() + " (is the API URL correct and reachable?).");
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
                severe("Pairing start returned an incomplete response.");
                this.message(this.plugin, sender, Message.WEBSITE_SYNC_PAIR_ERROR);
                return;
            }

            this.pairing = true;
            this.pairDeadline = System.currentTimeMillis() + (ttl * 1000L);

            success("Pairing started - code " + userCode + ", verification url " + url + ".");
            this.message(this.plugin, sender, Message.WEBSITE_SYNC_PAIR_CODE, "%code%", userCode, "%url%", url == null ? "" : url);
            this.scheduleNextPoll(sender);
        });
    }

    /**
     * Schedules the next poll for the pairing process.
     *
     * @param sender The player who requested the pairing.
     */
    private void scheduleNextPoll(CommandSender sender) {
        if (!this.pairing) return;
        this.plugin.getScheduler().runLater(() -> this.pollOnce(sender), this.pollSeconds, TimeUnit.SECONDS);
    }

    /**
     * Polls the server for the pairing status.
     *
     * @param sender The player who requested the pairing.
     */
    private void pollOnce(CommandSender sender) {
        if (!this.pairing || this.deviceCode == null) return;

        if (System.currentTimeMillis() > this.pairDeadline) {
            this.pairing = false;
            this.deviceCode = null;
            warning("Pairing expired - no approval within the time limit.");
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
            severe("Pairing failed: HTTP " + code + reason + ".");
            this.message(this.plugin, sender, code == 410 ? Message.WEBSITE_SYNC_PAIR_EXPIRED : Message.WEBSITE_SYNC_PAIR_ERROR);
        });
    }

    /**
     * Handle the successful pairing response.
     *
     * @param sender   The player who requested the pairing.
     * @param response The response from the server containing the pairing details.
     */
    private void handlePairSuccess(CommandSender sender, fr.maxlego08.menu.website.request.Response response) {
        this.pairing = false;
        this.deviceCode = null;

        String token = (String) response.get("token");
        String wsUrl = (String) response.get("ws_url");
        String connectionId = (String) response.get("connection_id");

        if (token == null || wsUrl == null) {
            severe("Pairing was approved but the response was incomplete (missing token/ws_url).");
            this.message(this.plugin, sender, Message.WEBSITE_SYNC_PAIR_ERROR);
            return;
        }

        LiveSyncConfig cfg = this.config != null ? this.config : new LiveSyncConfig();
        cfg.token = token;
        cfg.wsUrl = wsUrl;
        cfg.connectionId = connectionId;
        this.config = cfg;
        this.config.save(this.plugin.getPersist());

        success("Server linked successfully. Relay " + wsUrl + ".");
        this.message(this.plugin, sender, Message.WEBSITE_SYNC_PAIR_SUCCESS);

        // Open the first ephemeral window right away so the user can sync immediately.
        this.connect(sender);
    }

    /**
     * Connect to the live sync server.
     *
     * @param sender The player who requested the connection.
     */
    public void connect(CommandSender sender) {

        if (!this.isLinked()) {
            warning("Connect requested but the server is not linked yet. Run /zmenu login first.");
            this.message(this.plugin, sender, Message.WEBSITE_SYNC_NOT_LINKED);
            return;
        }

        if (this.connecting || this.connected) {
            warning("Connect requested but the live connection is already open/opening.");
            this.message(this.plugin, sender, Message.WEBSITE_SYNC_ALREADY_CONNECTED);
            return;
        }

        this.shouldStayConnected = true;
        this.reconnectAttempts = 0;
        this.connecting = true;
        this.message(this.plugin, sender, Message.WEBSITE_SYNC_CONNECTING);

        // Verify the link is still valid + refresh the relay url/connection id before opening the socket.
        this.refreshConnectionInfo(sender, () -> this.openSocket(sender));
    }

    /**
     * Ask the website whether this link is still valid and refresh the stored relay url / connection id.
     * On 401/403 the link is forgotten (revoked server-side); otherwise {@code onReady} runs - even on a
     * transient API error, falling back to the stored url so a website hiccup doesn't block connecting.
     *
     * @param sender  The player who requested the connection.
     * @param onReady The callback to run when the connection info is ready.
     */
    private void refreshConnectionInfo(CommandSender sender, Runnable onReady) {
        HttpRequest request = new HttpRequest(this.apiUrl + "zmenu/connection", new JsonObject());
        request.setBearer(this.config.token);
        request.setMethod("GET");
        request.submit(this.plugin, response -> {
            int code = response.getCode();
            if (code == 401 || code == 403) {
                this.connecting = false;
                this.shouldStayConnected = false;
                warning("The website reports this link is no longer valid (revoked); clearing it.");
                this.unlink();
                this.message(this.plugin, sender, Message.WEBSITE_SYNC_AUTH_FAILED);
                return;
            }
            if (code == 200) {
                String wsUrl = (String) response.get("ws_url");
                String connectionId = (String) response.get("connection_id");
                boolean changed = false;
                if (wsUrl != null && !wsUrl.isEmpty() && !wsUrl.equals(this.config.wsUrl)) {
                    this.config.wsUrl = wsUrl;
                    changed = true;
                }
                if (connectionId != null && !connectionId.equals(this.config.connectionId)) {
                    this.config.connectionId = connectionId;
                    changed = true;
                }
                if (changed) {
                    this.config.save(this.plugin.getPersist());
                    log("Live sync info updated (relay " + this.config.wsUrl + ").");
                }
            } else {
                warning("Could not refresh live sync info (HTTP " + code + "); using the stored relay url.");
            }
            log("Opening live connection to " + this.config.wsUrl + " ...");
            onReady.run();
        });
    }

    /**
     * Force-detach this server from the website: revoke the link server-side, then clear the local
     * credential REGARDLESS of the API result (so a failed/unreachable site never leaves a stuck link).
     */
    public void forceUnlink(CommandSender sender) {
        if (!this.isLinked()) {
            warning("Unlink requested but the server is not linked.");
            this.message(this.plugin, sender, Message.WEBSITE_SYNC_NOT_LINKED);
            return;
        }

        log("Unlinking this server from the website...");

        HttpRequest request = new HttpRequest(this.apiUrl + "zmenu/unlink", new JsonObject());
        request.setBearer(this.config.token);
        request.setMethod("POST");
        request.submit(this.plugin, response -> {
            if (response.getCode() == 200) {
                success("Website confirmed the unlink.");
            } else {
                warning("Website unlink call failed (HTTP " + response.getCode() + "); clearing the local link anyway.");
            }
            this.shouldStayConnected = false;
            this.unlink();
            this.message(this.plugin, sender, Message.WEBSITE_SYNC_UNLINKED);
        });
    }

    /**
     * Open a WebSocket connection to the website.
     *
     * @param sender The player who requested the connection.
     */
    private void openSocket(CommandSender sender) {
        try {
            URI uri = new URI(this.config.wsUrl);

            WebSocketClient socket = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    log("Socket open - authenticating...");
                    JsonObject hello = new JsonObject();
                    hello.addProperty("type", "hello");
                    hello.addProperty("token", config.token);
                    try {
                        this.send(hello.toString());
                    } catch (Exception e) {
                        severe("Failed to send hello: " + e.getMessage() + ".");
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
                    warning("Live connection closed (code " + code + (reason != null && !reason.isEmpty() ? ", " + reason : "") + ").");
                    // Re-establish if the operator still wants to be connected (no-op after an explicit
                    // /zmenu disconnect or a revocation, which clear shouldStayConnected).
                    scheduleReconnect();
                }

                @Override
                public void onError(Exception ex) {
                    connecting = false;
                    severe("Live connection error: " + ex.getMessage() + ".");
                    if (Configuration.enableDebug) {
                        ex.printStackTrace();
                    }
                }
            };

            if ("wss".equalsIgnoreCase(uri.getScheme())) {
                socket.setSocketFactory(SSLSocketFactory.getDefault());
            }
            socket.setConnectionLostTimeout(CONNECTION_LOST_TIMEOUT_SECONDS);

            this.client = socket;
            socket.connect();

        } catch (Throwable throwable) {
            this.connecting = false;
            severe("Failed to open the live connection: " + throwable.getMessage() + ".");
            this.message(this.plugin, sender, Message.WEBSITE_SYNC_CONNECT_ERROR);
            if (Configuration.enableDebug) {
                throwable.printStackTrace();
            }
        }
    }

    /**
     * Handle a message received from the website.
     *
     * @param sender  The player who requested the pairing.
     * @param message The message received from the website.
     */
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
                this.reconnectAttempts = 0;
                success("Live sync connected - ready to receive syncs.");
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

    // ------------------------------------------------------------------ //
    // Notification -> download -> verify -> reload
    // ------------------------------------------------------------------ //

    /**
     * Handles a relay error by setting the connecting flag to false and sending a message to the sender.
     *
     * @param sender The player who requested the connection.
     * @param error  The error message received from the relay.
     */
    private void handleRelayError(CommandSender sender, String error) {
        this.connecting = false;
        severe("Relay rejected the connection: " + error + ".");
        this.message(this.plugin, sender, Message.WEBSITE_SYNC_AUTH_FAILED);

        if ("unauthorized".equals(error)) {
            // The token is invalid/revoked - forget the link so the operator can re-link cleanly.
            this.unlink();
            warning("The stored link was cleared (token no longer valid). Run /zmenu login to re-link.");
        }
    }

    /**
     * Disconnects the live sync.
     *
     * @param sender The player who requested the disconnection.
     */
    public void disconnect(CommandSender sender) {
        boolean wasActive = this.connected || this.connecting || this.pairing;

        this.pairing = false;
        this.deviceCode = null;
        this.shouldStayConnected = false;
        this.closeSocket();

        log("Live sync " + (wasActive ? "disconnected." : "was not active."));
        this.message(this.plugin, sender, wasActive ? Message.WEBSITE_SYNC_DISCONNECTED : Message.WEBSITE_SYNC_NOT_CONNECTED);
    }

    /**
     * Unlink the live sync.
     */
    private void unlink() {
        this.shouldStayConnected = false;
        this.closeSocket();
        if (this.config != null) {
            this.config.token = null;
            this.config.wsUrl = null;
            this.config.connectionId = null;
            this.config.save(this.plugin.getPersist());
        }
    }

    /**
     * Closes the socket connection.
     */
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

    /**
     * Schedule an automatic reconnect with linear backoff so /zmenu connect keeps the server connected
     * across idle-evictions, relay restarts and network blips. No-op once the operator runs
     * /zmenu disconnect or the token is revoked (both clear {@code shouldStayConnected}); gives up after
     * {@link #MAX_RECONNECT_ATTEMPTS} so a permanently unreachable relay doesn't retry forever.
     */
    private void scheduleReconnect() {
        if (!this.shouldStayConnected || !this.isLinked()) {
            return;
        }
        this.reconnectAttempts++;
        if (this.reconnectAttempts > MAX_RECONNECT_ATTEMPTS) {
            this.shouldStayConnected = false;
            severe("Could not reconnect after " + MAX_RECONNECT_ATTEMPTS + " attempts - giving up. Run /zmenu connect to retry.");
            return;
        }
        long delay = Math.min(MAX_RECONNECT_DELAY_SECONDS, RECONNECT_BASE_SECONDS * this.reconnectAttempts);
        warning("Reconnecting in " + delay + "s (attempt " + this.reconnectAttempts + "/" + MAX_RECONNECT_ATTEMPTS + ")...");
        this.plugin.getScheduler().runLater(() -> {
            if (this.shouldStayConnected && this.isLinked() && !this.connected && !this.connecting) {
                this.connecting = true;
                this.plugin.getScheduler().runAsync(w -> this.openSocket(Bukkit.getConsoleSender()));
            }
        }, delay, TimeUnit.SECONDS);
    }

    /**
     * Apply a sync notification.
     *
     * @param data The JSON object containing the sync notification.
     */
    private void applySync(JsonObject data) {
        if (!data.has("inventory_id") || !data.has("file_name") || !data.has("hash")) {
            warning("Ignored a sync notification: missing inventory_id/file_name/hash.");
            return;
        }

        int inventoryId = data.get("inventory_id").getAsInt();
        String fileName = data.get("file_name").getAsString();
        String hash = data.get("hash").getAsString();

        // Defence in depth: never let a remote file_name escape the inventories directory.
        if (fileName == null || !fileName.matches("[A-Za-z0-9_\\- ]{1,64}")) {
            warning("Ignored a sync notification: invalid file name '" + fileName + "'.");
            return;
        }

        // Optional subfolder under inventories/ (mirrors the website's folder tree). Reject traversal.
        String rawPath = data.has("path") && !data.get("path").isJsonNull() ? data.get("path").getAsString() : "";
        String subPath = sanitizeRelativePath(rawPath);
        if (subPath == null) {
            warning("Ignored a sync notification: invalid path '" + rawPath + "'.");
            return;
        }

        String displayName = subPath.isEmpty() ? fileName : subPath + "/" + fileName;
        log("Received sync notification for inventory '" + displayName + "' (id " + inventoryId + ").");

        // Idempotency key includes the path so the same name in two folders is tracked separately.
        String hashKey = (subPath + "/" + fileName).toLowerCase(Locale.ROOT);
        if (hash != null && hash.equalsIgnoreCase(this.lastAppliedHash.get(hashKey))) {
            log("Inventory '" + displayName + "' is already up to date, nothing to do.");
            return;
        }

        this.downloadAndApply(inventoryId, fileName, subPath, hash);
    }

    /**
     * Download the inventory file and apply it.
     *
     * @param inventoryId The ID of the inventory to download.
     * @param fileName    The name of the inventory file.
     * @param subPath     The sub-path of the inventory file.
     * @param hash        The hash of the inventory file.
     */
    private void downloadAndApply(int inventoryId, String fileName, String subPath, String hash) {
        File tmpDir = new File(this.plugin.getDataFolder(), "live-sync");
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        // Unique per inventory so concurrent syncs of same-named inventories in different folders never
        // share a temp path.
        File tempFile = new File(tmpDir, inventoryId + "_" + fileName + ".tmp");

        log("Downloading inventory '" + fileName + "' (id " + inventoryId + ")...");

        HttpRequest request = new HttpRequest(this.apiUrl + "zmenu/inventory/" + inventoryId + "/download", new JsonObject());
        request.setBearer(this.config.token);
        request.setMethod("GET");
        request.submitForFileDownload(this.plugin, tempFile, success -> {
            if (!success) {
                deleteQuietly(tempFile);
                severe("Download failed for '" + fileName + "' (id " + inventoryId + "). Token revoked/expired, or inventory not owned by this account.");
                this.message(this.plugin, Bukkit.getConsoleSender(), Message.WEBSITE_SYNC_APPLY_ERROR, "%name%", fileName);
                return;
            }
            if (tempFile.length() <= 0 || tempFile.length() > MAX_YAML_BYTES) {
                severe("Rejected '" + fileName + "': downloaded size out of bounds (" + tempFile.length() + " bytes).");
                deleteQuietly(tempFile);
                return;
            }

            String actual = sha256(tempFile);
            if (actual == null || !actual.equalsIgnoreCase(hash)) {
                severe("Rejected '" + fileName + "': hash mismatch (expected " + hash + ", got " + actual + ").");
                deleteQuietly(tempFile);
                this.message(this.plugin, Bukkit.getConsoleSender(), Message.WEBSITE_SYNC_APPLY_ERROR, "%name%", fileName);
                return;
            }

            try {
                YamlConfiguration test = YamlConfiguration.loadConfiguration(tempFile);
                if (test.getKeys(false).isEmpty()) {
                    severe("Rejected '" + fileName + "': downloaded file is empty or not valid YAML.");
                    deleteQuietly(tempFile);
                    return;
                }
            } catch (Exception exception) {
                severe("Rejected '" + fileName + "': failed to parse the downloaded YAML.");
                deleteQuietly(tempFile);
                return;
            }

            File invDir = new File(this.plugin.getDataFolder(), "inventories");
            if (!subPath.isEmpty()) {
                invDir = new File(invDir, subPath); // mirror the website folder tree (already sanitised)
            }
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
                severe("Failed to write '" + fileName + ".yml': " + exception.getMessage() + ".");
                deleteQuietly(tempFile);
                this.message(this.plugin, Bukkit.getConsoleSender(), Message.WEBSITE_SYNC_APPLY_ERROR, "%name%", fileName);
                return;
            }

            this.reloadOnMainThread(fileName, subPath, target, backup, hash);
        });
    }

    // ------------------------------------------------------------------ //
    // Helpers
    // ------------------------------------------------------------------ //


    /**
     * Reloads the inventory manager on the main thread.
     *
     * @param fileName The name of the inventory file.
     * @param subPath  The sub-path of the inventory file.
     * @param target   The target file.
     * @param backup   The backup file.
     * @param hash     The hash of the inventory file.
     */
    private void reloadOnMainThread(String fileName, String subPath, File target, File backup, String hash) {
        String label = subPath.isEmpty() ? fileName : subPath + "/" + fileName;
        this.plugin.getScheduler().runNextTick(w -> {
            InventoryManager inventoryManager = this.plugin.getInventoryManager();
            YamlFileCache.invalidateCache(target.toPath());

            boolean applied;
            try {
                // zMenu's registry is keyed by bare file name (no path), so resolve by the actual TARGET
                // file - a bare-name lookup could return a same-named inventory from another folder and
                // reload the wrong file.
                Optional<Inventory> existing = findInventoryByFile(inventoryManager, target);
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
                severe("zMenu failed to load the synced inventory '" + label + "': " + exception.getMessage() + ".");
                if (Configuration.enableDebug) {
                    exception.printStackTrace();
                }
            }

            if (applied) {
                this.lastAppliedHash.put((subPath + "/" + fileName).toLowerCase(Locale.ROOT), hash);
                success("Inventory '" + label + "' synced and reloaded.");
                this.message(this.plugin, Bukkit.getConsoleSender(), Message.WEBSITE_SYNC_APPLIED, "%name%", label);
            } else {
                this.rollback(inventoryManager, target, backup);
                warning("Rolled back '" + label + "' to the previous version.");
                this.message(this.plugin, Bukkit.getConsoleSender(), Message.WEBSITE_SYNC_APPLY_ERROR, "%name%", label);
            }
        });
    }

    /**
     * Find the loaded inventory whose backing file is exactly {@code target} (canonical comparison),
     * regardless of its name - zMenu indexes by bare file name, which is ambiguous across subfolders.
     */
    private Optional<Inventory> findInventoryByFile(InventoryManager inventoryManager, File target) {
        File canonicalTarget = canonical(target);
        for (Inventory inventory : inventoryManager.getInventories()) {
            File file = inventory.getFile();
            if (file != null && canonical(file).equals(canonicalTarget)) {
                return Optional.of(inventory);
            }
        }
        return Optional.empty();
    }

    /**
     * Rollback the inventory to the previous version.
     *
     * @param inventoryManager The inventory manager.
     * @param target           The target file.
     * @param backup           The backup file.
     */
    private void rollback(InventoryManager inventoryManager, File target, File backup) {
        try {
            if (!backup.exists()) {
                return;
            }
            Files.copy(backup.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            YamlFileCache.invalidateCache(target.toPath());
            Optional<Inventory> old = findInventoryByFile(inventoryManager, target);
            if (old.isPresent()) {
                inventoryManager.reloadInventory(old.get());
            } else {
                inventoryManager.loadInventory(this.plugin, target);
            }
        } catch (Exception exception) {
            severe("Rollback of '" + target.getName() + "' failed: " + exception.getMessage() + ".");
        }
    }
}
