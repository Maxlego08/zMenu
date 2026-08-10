package fr.maxlego08.menu.website.sync;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
import org.bukkit.entity.Player;
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
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class LiveSyncManager extends ZUtils {

    private static final String EVENT_SYNC = "inventory.sync";
    private static final String EVENT_PATTERN_SYNC = "pattern.sync";
    /**
     * Carries the player an inventory must be opened for, on the AUTHENTICATED download (never the relay).
     */
    private static final String OPEN_FOR_HEADER = "X-Zmenu-Open-For";
    /**
     * Same set the website validates: Java names, plus the '.' prefix / spaces a Bedrock name can carry.
     */
    private static final Pattern OPEN_TARGET_PATTERN = Pattern.compile("[A-Za-z0-9_ .\\-]{1,32}");
    private static final long MAX_YAML_BYTES = 512L * 1024L;
    private static final long DEFAULT_PAIR_TTL_SECONDS = 600L;
    private static final int CONNECTION_LOST_TIMEOUT_SECONDS = 30;
    private static final int MAX_RECONNECT_ATTEMPTS = 10;
    private static final long RECONNECT_BASE_SECONDS = 5L;
    private static final long MAX_RECONNECT_DELAY_SECONDS = 60L;
    /**
     * Delay before the link is reopened by itself at startup. Long enough for the server to finish
     * booting (and for {@link #validateStoredLink()} to have cleared a revoked token), short enough that
     * "restart the server, then sync from the website" just works.
     */
    private static final long AUTO_CONNECT_DELAY_SECONDS = 10L;

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

        // On startup, make sure a stored link is still valid server-side; a revoked/expired token
        // forces a local unlink so we never keep a dead link around.
        this.validateStoredLink();

        // ...then reopen the live link by itself. A linked server that has to be told /zmenu website
        // connect after every restart is just a broken feature: the website reports "server not
        // connected" until someone logs in and types it.
        this.scheduleAutoConnect();
    }

    /**
     * Reopen the live link shortly after startup when this server is already linked.
     * <p>
     * Scheduled independently of {@link #validateStoredLink()} rather than chained onto its callback, so
     * the timing never depends on how fast (or whether) the website answers. The delayed task re-reads
     * {@link #isLinked()}: if the validation meanwhile cleared a revoked token, nothing happens; and if
     * the validation is still in flight with a token that turns out to be dead, the relay answers
     * `unauthorized` and {@link #handleRelayError} unlinks — the same outcome, one round-trip later.
     * <p>
     * No {@code /connection} call here: the validation above already refreshed the relay url and
     * connection id, and the token is authenticated by the relay's own introspection anyway.
     */
    private void scheduleAutoConnect() {
        if (!this.isLinked()) {
            return;
        }

        if (!Configuration.enableWebsiteAutoConnect) {
            this.log("Live sync auto-connect is disabled (enable-website-auto-connect), run /zmenu website connect to open the link.");
            return;
        }

        this.log("Live sync link found, connecting in " + AUTO_CONNECT_DELAY_SECONDS + "s...");

        this.plugin.getScheduler().runLater(() -> {
            if (!this.isLinked() || this.connected || this.connecting) {
                return;
            }
            this.shouldStayConnected = true;
            this.reconnectAttempts = 0;
            this.connecting = true;
            // Off-thread like every other openSocket() call site: the socket handshake must not run on
            // the server thread.
            this.plugin.getScheduler().runAsync(w -> this.openSocket(Bukkit.getConsoleSender()));
        }, AUTO_CONNECT_DELAY_SECONDS, TimeUnit.SECONDS);
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
        File configFile = new File(this.plugin.getDataFolder().getParentFile().getParentFile(), "config/zmenu-uuid.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        String serverId = config.getString("server_uuid");

        if (serverId == null || serverId.isEmpty()) {
            serverId = UUID.randomUUID().toString();
            config.set("server_uuid", serverId);
            try {
                String header = "It is not recommended to delete this file, as it is used to identify the server on https://minecraft-inventory-builder.com/.";
                config.options().header(header);
                config.options().copyHeader(true);
                config.save(configFile);
                this.log("Generated new server UUID in " + configFile.getPath());
            } catch (Exception exception) {
                this.severe("Could not save server UUID to " + configFile.getPath() + ": " + exception.getMessage());
            }
        }

        if (this.config == null) this.config = new LiveSyncConfig();
        return serverId;
    }

    /**
     * This method is called when the player requests a pairing.
     *
     * @param sender The player who requested the pairing.
     */
    public void startDeviceFlow(CommandSender sender) {
        if (this.isLinked()) {
            this.warning("Pairing requested but this server is already linked (use /zmenu website connect).");
            message(this.plugin, sender, Message.WEBSITE_SYNC_ALREADY_LINKED);
            return;
        }

        if (this.pairing) {
            this.warning("Pairing requested but a pairing is already in progress.");
            message(this.plugin, sender, Message.WEBSITE_SYNC_PAIR_PENDING);
            return;
        }

        this.log("Starting device-flow pairing against " + this.apiUrl + "zmenu/pair/start ...");
        message(this.plugin, sender, Message.WEBSITE_SYNC_PAIR_START);

        JsonObject body = new JsonObject();
        body.addProperty("server_name", this.plugin.getServer().getName());
        body.addProperty("plugin_version", this.plugin.getDescription().getVersion());
        body.addProperty("server_id", this.ensureServerId());

        HttpRequest request = new HttpRequest(this.apiUrl + "zmenu/pair/start", body);
        request.setMethod("POST");
        request.submit(this.plugin, response -> {
            if (response.getCode() != 200) {
                this.severe("Pairing start failed: HTTP " + response.getCode() + " (is the API URL correct and reachable?).");
                message(this.plugin, sender, Message.WEBSITE_SYNC_PAIR_ERROR);
                return;
            }

            String userCode = (String) response.get("user_code");
            String url = (String) response.get("verification_url_complete");
            if (url == null) {
                url = (String) response.get("verification_url");
            }
            this.deviceCode = (String) response.get("device_code");
            this.pollSeconds = this.asInt(response.get("interval"), 5);
            long ttl = this.asInt(response.get("expires_in"), (int) DEFAULT_PAIR_TTL_SECONDS);

            if (this.deviceCode == null || userCode == null) {
                this.severe("Pairing start returned an incomplete response.");
                message(this.plugin, sender, Message.WEBSITE_SYNC_PAIR_ERROR);
                return;
            }

            this.pairing = true;
            this.pairDeadline = System.currentTimeMillis() + (ttl * 1000L);

            this.success("Pairing started; code " + userCode + ", verification url " + url + ".");
            message(this.plugin, sender, Message.WEBSITE_SYNC_PAIR_CODE, "%code%", userCode, "%url%", url == null ? "" : url);
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
            this.warning("Pairing expired: no approval within the time limit.");
            message(this.plugin, sender, Message.WEBSITE_SYNC_PAIR_EXPIRED);
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
            this.severe("Pairing failed: HTTP " + code + reason + ".");
            message(this.plugin, sender, code == 410 ? Message.WEBSITE_SYNC_PAIR_EXPIRED : Message.WEBSITE_SYNC_PAIR_ERROR);
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
            this.severe("Pairing was approved but the response was incomplete (missing token/ws_url).");
            message(this.plugin, sender, Message.WEBSITE_SYNC_PAIR_ERROR);
            return;
        }

        LiveSyncConfig cfg = this.config != null ? this.config : new LiveSyncConfig();
        cfg.token = token;
        cfg.wsUrl = wsUrl;
        cfg.connectionId = connectionId;
        this.config = cfg;
        this.config.save(this.plugin.getPersist());

        this.success("Server linked successfully. Relay " + wsUrl + ".");
        message(this.plugin, sender, Message.WEBSITE_SYNC_PAIR_SUCCESS);

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
            this.warning("Connect requested but the server is not linked yet. Run /zmenu website login first.");
            message(this.plugin, sender, Message.WEBSITE_SYNC_NOT_LINKED);
            return;
        }

        if (this.connecting || this.connected) {
            this.warning("Connect requested but the live connection is already open/opening.");
            message(this.plugin, sender, Message.WEBSITE_SYNC_ALREADY_CONNECTED);
            return;
        }

        this.shouldStayConnected = true;
        this.reconnectAttempts = 0;
        this.connecting = true;
        message(this.plugin, sender, Message.WEBSITE_SYNC_CONNECTING);

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
                this.warning("The website reports this link is no longer valid (revoked); clearing it.");
                this.unlink();
                message(this.plugin, sender, Message.WEBSITE_SYNC_AUTH_FAILED);
                return;
            }
            if (code == 200) {
                this.applyConnectionInfo(response);
            } else {
                this.warning("Could not refresh live sync info (HTTP " + code + "); using the stored relay url.");
            }
            this.log("Opening live connection to " + this.config.wsUrl + " ...");
            onReady.run();
        });
    }

    /**
     * On enable, confirm a stored link is still valid with the website. A revoked/expired token
     * (HTTP 401/403) forces a local unlink so a dead link is never kept; any other outcome (200, or a
     * transient error) leaves the stored credential untouched. Runs off the main thread via the HTTP
     * client, so it never blocks server startup.
     */
    private void validateStoredLink() {
        if (!this.isLinked()) {
            return;
        }

        this.log("Verifying the stored website link is still valid...");

        HttpRequest request = new HttpRequest(this.apiUrl + "zmenu/connection", new JsonObject());
        request.setBearer(this.config.token);
        request.setMethod("GET");
        request.submit(this.plugin, response -> {
            int code = response.getCode();
            // Only a genuine auth failure (401) clears the link unattended: the website returns 401 for a
            // revoked/expired/inactive token. A 403 is kept here because at startup it is ambiguous - a
            // reverse proxy / WAF / Cloudflare challenge in front of the API commonly answers a headless
            // request with 403, and wiping a valid link on every restart would be worse than a stale one.
            // The interactive /zmenu connect path (refreshConnectionInfo) still treats 403 as revocation.
            if (code == 401) {
                this.warning("The website reports this link is no longer valid (revoked/expired); clearing it. Run /zmenu website login to re-link.");
                this.unlink();
                return;
            }
            if (code == 200) {
                this.applyConnectionInfo(response);
                this.success("Website link verified.");
            } else {
                this.warning("Could not verify the website link on startup (HTTP " + code + "); keeping the stored link.");
            }
        });
    }

    /**
     * Apply the live-sync info from a successful /zmenu/connection response: refresh the stored relay
     * url / connection id when the website hands back new values, and persist only if something changed.
     */
    private void applyConnectionInfo(fr.maxlego08.menu.website.request.Response response) {
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
            this.log("Live sync info updated (relay " + this.config.wsUrl + ").");
        }
    }

    /**
     * Force-detach this server from the website: revoke the link server-side, then clear the local
     * credential REGARDLESS of the API result (so a failed/unreachable site never leaves a stuck link).
     */
    public void forceUnlink(CommandSender sender) {
        if (!this.isLinked()) {
            this.warning("Unlink requested but the server is not linked.");
            message(this.plugin, sender, Message.WEBSITE_SYNC_NOT_LINKED);
            return;
        }

        this.log("Unlinking this server from the website...");

        HttpRequest request = new HttpRequest(this.apiUrl + "zmenu/unlink", new JsonObject());
        request.setBearer(this.config.token);
        request.setMethod("POST");
        request.submit(this.plugin, response -> {
            if (response.getCode() == 200) {
                this.success("Website confirmed the unlink.");
            } else {
                this.warning("Website unlink call failed (HTTP " + response.getCode() + "); clearing the local link anyway.");
            }
            this.shouldStayConnected = false;
            this.unlink();
            message(this.plugin, sender, Message.WEBSITE_SYNC_UNLINKED);
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
                    LiveSyncManager.this.log("Socket open: authenticating...");
                    JsonObject hello = new JsonObject();
                    hello.addProperty("type", "hello");
                    hello.addProperty("token", LiveSyncManager.this.config.token);
                    try {
                        this.send(hello.toString());
                    } catch (Exception e) {
                        LiveSyncManager.this.severe("Failed to send hello: " + e.getMessage() + ".");
                    }
                }

                @Override
                public void onMessage(String message) {
                    LiveSyncManager.this.handleRelayMessage(sender, message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    LiveSyncManager.this.connected = false;
                    LiveSyncManager.this.connecting = false;
                    LiveSyncManager.this.warning("Live connection closed (code " + code + (reason != null && !reason.isEmpty() ? ", " + reason : "") + ").");
                    // Re-establish if the operator still wants to be connected (no-op after an explicit
                    // /zmenu disconnect or a revocation, which clear shouldStayConnected).
                    LiveSyncManager.this.scheduleReconnect();
                }

                @Override
                public void onError(Exception ex) {
                    LiveSyncManager.this.connecting = false;
                    LiveSyncManager.this.severe("Live connection error: " + ex.getMessage() + ".");
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
            this.severe("Failed to open the live connection: " + throwable.getMessage() + ".");
            message(this.plugin, sender, Message.WEBSITE_SYNC_CONNECT_ERROR);
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
                this.success("Live sync connected; ready to receive syncs.");
                message(this.plugin, sender, Message.WEBSITE_SYNC_CONNECTED);
                break;
            case "error":
                this.handleRelayError(sender, obj.has("error") ? obj.get("error").getAsString() : "error");
                break;
            case EVENT_SYNC:
                this.applySync(obj);
                break;
            case EVENT_PATTERN_SYNC:
                this.applyPatternSync(obj);
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
        this.severe("Relay rejected the connection: " + error + ".");
        message(this.plugin, sender, Message.WEBSITE_SYNC_AUTH_FAILED);

        if ("unauthorized".equals(error)) {
            // The token is invalid/revoked - forget the link so the operator can re-link cleanly.
            this.unlink();
            this.warning("The stored link was cleared (token no longer valid). Run /zmenu login to re-link.");
        }
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
            this.severe("Could not reconnect after " + MAX_RECONNECT_ATTEMPTS + " attempts, giving up. Run /zmenu website connect to retry.");
            return;
        }
        long delay = Math.min(MAX_RECONNECT_DELAY_SECONDS, RECONNECT_BASE_SECONDS * this.reconnectAttempts);
        this.warning("Reconnecting in " + delay + "s (attempt " + this.reconnectAttempts + "/" + MAX_RECONNECT_ATTEMPTS + ")...");
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
            this.warning("Ignored a sync notification: missing inventory_id/file_name/hash.");
            return;
        }

        int inventoryId = data.get("inventory_id").getAsInt();
        String fileName = data.get("file_name").getAsString();
        String hash = data.get("hash").getAsString();

        // "Open the menu in game once reloaded". The relay only ever carries the FLAG - the player it
        // opens for is read from the authenticated download response (X-Zmenu-Open-For), so the relay
        // never sees a player name and cannot choose the target itself.
        boolean open = this.asBoolean(data, "open");

        // Defence in depth: never let a remote file_name escape the inventories directory.
        if (fileName == null || !fileName.matches("[A-Za-z0-9_\\- ]{1,64}")) {
            this.warning("Ignored a sync notification: invalid file name '" + fileName + "'.");
            return;
        }

        // Optional subfolder under inventories/ (mirrors the website's folder tree). Reject traversal.
        String rawPath = data.has("path") && !data.get("path").isJsonNull() ? data.get("path").getAsString() : "";
        String subPath = this.sanitizeRelativePath(rawPath);
        if (subPath == null) {
            this.warning("Ignored a sync notification: invalid path '" + rawPath + "'.");
            return;
        }

        String displayName = subPath.isEmpty() ? fileName : subPath + "/" + fileName;
        this.log("Received sync notification for inventory '" + displayName + "' (id " + inventoryId + ").");

        // Idempotency key includes the path so the same name in two folders is tracked separately.
        // Skipped when the sync must also OPEN the menu: re-clicking "Sync" with nothing changed is
        // exactly how you ask for the menu to be (re)opened, and the target player only travels on the
        // download response - so that download has to happen.
        String hashKey = (subPath + "/" + fileName).toLowerCase(Locale.ROOT);
        if (!open && hash != null && hash.equalsIgnoreCase(this.lastAppliedHash.get(hashKey))) {
            this.log("Inventory '" + displayName + "' is already up to date, nothing to do.");
            return;
        }

        if (data.has("patterns") && data.get("patterns").isJsonArray() && !data.getAsJsonArray("patterns").isEmpty()) {
            this.applyPatternDependencies(data.getAsJsonArray("patterns"), 0, success -> {
                if (success) {
                    this.downloadAndApply(inventoryId, fileName, subPath, hash, open);
                } else {
                    this.severe("Inventory '" + displayName + "' was not synced because one of its button patterns could not be applied.");
                    message(this.plugin, Bukkit.getConsoleSender(), Message.WEBSITE_SYNC_APPLY_ERROR, "%name%", displayName);
                }
            });
            return;
        }

        this.downloadAndApply(inventoryId, fileName, subPath, hash, open);
    }

    /**
     * Apply a standalone button-pattern notification sent from the Pattern Studio.
     */
    private void applyPatternSync(JsonObject data) {
        this.downloadAndApplyPattern(data, success -> {
            if (!success) {
                this.warning("The button pattern received from the website could not be applied.");
            }
        });
    }

    /**
     * Install every dependency before reloading its inventory. Pattern loading is intentionally
     * sequential so an inventory never races the async downloads and sees a partially updated set.
     */
    private void applyPatternDependencies(JsonArray patterns, int index, Consumer<Boolean> completion) {
        if (index >= patterns.size()) {
            completion.accept(true);
            return;
        }
        if (!patterns.get(index).isJsonObject()) {
            completion.accept(false);
            return;
        }

        this.downloadAndApplyPattern(patterns.get(index).getAsJsonObject(), success -> {
            if (!success) {
                completion.accept(false);
                return;
            }
            this.applyPatternDependencies(patterns, index + 1, completion);
        });
    }

    /**
     * Download, verify, write and hot-load a managed button pattern.
     */
    private void downloadAndApplyPattern(JsonObject data, Consumer<Boolean> completion) {
        if (!data.has("id") || !data.has("file_name") || !data.has("hash")) {
            this.warning("Ignored a pattern sync notification: missing id/file_name/hash.");
            completion.accept(false);
            return;
        }

        int patternId;
        String rawFileName;
        String hash;
        String pluginName;
        try {
            patternId = data.get("id").getAsInt();
            rawFileName = data.get("file_name").getAsString();
            hash = data.get("hash").getAsString();
            pluginName = data.has("plugin_name") && !data.get("plugin_name").isJsonNull()
                    ? data.get("plugin_name").getAsString() : this.plugin.getName();
        } catch (Exception exception) {
            this.warning("Ignored a pattern sync notification: invalid field types.");
            completion.accept(false);
            return;
        }

        if (rawFileName == null || !rawFileName.matches("[A-Za-z0-9_\\-/]{1,180}") || pluginName == null
                || !pluginName.matches("[A-Za-z0-9_.-]{1,64}")) {
            this.warning("Ignored a pattern sync notification: invalid pattern or plugin path.");
            completion.accept(false);
            return;
        }

        int separator = rawFileName.lastIndexOf('/');
        String fileName = separator < 0 ? rawFileName : rawFileName.substring(separator + 1);
        String rawSubPath = separator < 0 ? "" : rawFileName.substring(0, separator);
        String subPath = this.sanitizeRelativePath(rawSubPath);
        if (fileName.isEmpty() || subPath == null) {
            this.warning("Ignored a pattern sync notification: invalid pattern path '" + rawFileName + "'.");
            completion.accept(false);
            return;
        }

        String hashKey = ("pattern:" + pluginName + ":" + rawFileName).toLowerCase(Locale.ROOT);
        if (hash != null && hash.equalsIgnoreCase(this.lastAppliedHash.get(hashKey))) {
            this.log("Button pattern '" + rawFileName + "' is already up to date.");
            completion.accept(true);
            return;
        }

        File tmpDir = new File(this.plugin.getDataFolder(), "live-sync");
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        File tempFile = new File(tmpDir, "pattern_" + patternId + "_" + fileName + ".tmp");
        this.log("Downloading button pattern '" + rawFileName + "' (id " + patternId + ")...");

        HttpRequest request = new HttpRequest(this.apiUrl + "zmenu/pattern/" + patternId + "/download", new JsonObject());
        request.setBearer(this.config.token);
        request.setMethod("GET");
        request.submitForFileDownloadDetailed(this.plugin, tempFile, result -> {
            if (!result.success()) {
                this.deleteQuietly(tempFile);
                this.severe("Download failed for button pattern '" + rawFileName + "': " + this.describeDownloadFailure(result.code()) + ".");
                completion.accept(false);
                return;
            }
            if (tempFile.length() <= 0 || tempFile.length() > MAX_YAML_BYTES) {
                this.severe("Rejected button pattern '" + rawFileName + "': downloaded size out of bounds.");
                this.deleteQuietly(tempFile);
                completion.accept(false);
                return;
            }

            String actual = this.sha256(tempFile);
            if (actual == null || hash == null || !actual.equalsIgnoreCase(hash)) {
                this.severe("Rejected button pattern '" + rawFileName + "': hash mismatch.");
                this.deleteQuietly(tempFile);
                completion.accept(false);
                return;
            }

            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(tempFile);
            if (yaml.getKeys(false).isEmpty() || !"BUTTON".equalsIgnoreCase(yaml.getString("type", ""))
                    || !yaml.isConfigurationSection("button")) {
                this.severe("Rejected button pattern '" + rawFileName + "': expected a BUTTON pattern with a button section.");
                this.deleteQuietly(tempFile);
                completion.accept(false);
                return;
            }

            File pluginsDirectory = this.plugin.getDataFolder().getParentFile();
            File pluginDirectory = pluginName.equalsIgnoreCase(this.plugin.getName())
                    ? this.plugin.getDataFolder() : new File(pluginsDirectory, pluginName);
            File patternDirectory = new File(pluginDirectory, "patterns");
            if (!subPath.isEmpty()) {
                patternDirectory = new File(patternDirectory, subPath);
            }
            if (!patternDirectory.exists()) {
                patternDirectory.mkdirs();
            }

            File target = new File(patternDirectory, fileName + ".yml");
            File backup = new File(patternDirectory, fileName + ".yml.bak");
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
                this.severe("Failed to write button pattern '" + rawFileName + "': " + exception.getMessage() + ".");
                this.deleteQuietly(tempFile);
                completion.accept(false);
                return;
            }

            File finalTarget = target;
            this.plugin.getScheduler().runNextTick(w -> {
                try {
                    YamlFileCache.invalidateCache(finalTarget.toPath());
                    this.plugin.getPatternManager().loadPattern(finalTarget);
                    this.lastAppliedHash.put(hashKey, hash);
                    this.success("Button pattern '" + rawFileName + "' synced and reloaded.");
                    completion.accept(true);
                } catch (InventoryException exception) {
                    this.severe("zMenu failed to load button pattern '" + rawFileName + "': " + exception.getMessage() + ".");
                    try {
                        if (backup.exists()) {
                            Files.copy(backup.toPath(), finalTarget.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            YamlFileCache.invalidateCache(finalTarget.toPath());
                            this.plugin.getPatternManager().loadPattern(finalTarget);
                        }
                    } catch (Exception rollbackFailure) {
                        this.severe("Rollback of button pattern '" + rawFileName + "' failed: " + rollbackFailure.getMessage() + ".");
                    }
                    completion.accept(false);
                }
            });
        });
    }

    /**
     * Read a boolean member defensively: a relay is untrusted input, and Gson throws on a member that is
     * not the expected primitive.
     */
    private boolean asBoolean(JsonObject data, String key) {
        try {
            return data.has(key) && !data.get(key).isJsonNull() && data.get(key).getAsBoolean();
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * Download the inventory file and apply it.
     *
     * @param inventoryId The ID of the inventory to download.
     * @param fileName    The name of the inventory file.
     * @param subPath     The sub-path of the inventory file.
     * @param hash        The hash of the inventory file.
     * @param open        Whether the website asked for the reloaded menu to be opened in game; the player
     *                    it opens for comes from the download's {@code X-Zmenu-Open-For} header.
     */
    private void downloadAndApply(int inventoryId, String fileName, String subPath, String hash, boolean open) {
        File tmpDir = new File(this.plugin.getDataFolder(), "live-sync");
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        // Unique per inventory so concurrent syncs of same-named inventories in different folders never
        // share a temp path.
        File tempFile = new File(tmpDir, inventoryId + "_" + fileName + ".tmp");

        this.log("Downloading inventory '" + fileName + "' (id " + inventoryId + ")...");

        HttpRequest request = new HttpRequest(this.apiUrl + "zmenu/inventory/" + inventoryId + "/download", new JsonObject());
        request.setBearer(this.config.token);
        request.setMethod("GET");
        request.submitForFileDownloadDetailed(this.plugin, tempFile, result -> {
            if (!result.success()) {
                this.deleteQuietly(tempFile);
                this.severe("Download failed for '" + fileName + "' (id " + inventoryId + "): " + this.describeDownloadFailure(result.code()) + ".");
                message(this.plugin, Bukkit.getConsoleSender(), Message.WEBSITE_SYNC_APPLY_ERROR, "%name%", fileName);
                return;
            }
            if (tempFile.length() <= 0 || tempFile.length() > MAX_YAML_BYTES) {
                this.severe("Rejected '" + fileName + "': downloaded size out of bounds (" + tempFile.length() + " bytes).");
                this.deleteQuietly(tempFile);
                return;
            }

            String actual = this.sha256(tempFile);
            if (actual == null || !actual.equalsIgnoreCase(hash)) {
                this.severe("Rejected '" + fileName + "': hash mismatch (expected " + hash + ", got " + actual + ").");
                this.deleteQuietly(tempFile);
                message(this.plugin, Bukkit.getConsoleSender(), Message.WEBSITE_SYNC_APPLY_ERROR, "%name%", fileName);
                return;
            }

            try {
                YamlConfiguration test = YamlConfiguration.loadConfiguration(tempFile);
                if (test.getKeys(false).isEmpty()) {
                    this.severe("Rejected '" + fileName + "': downloaded file is empty or not valid YAML.");
                    this.deleteQuietly(tempFile);
                    return;
                }
            } catch (Exception exception) {
                this.severe("Rejected '" + fileName + "': failed to parse the downloaded YAML.");
                this.deleteQuietly(tempFile);
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
                this.severe("Failed to write '" + fileName + ".yml': " + exception.getMessage() + ".");
                this.deleteQuietly(tempFile);
                message(this.plugin, Bukkit.getConsoleSender(), Message.WEBSITE_SYNC_APPLY_ERROR, "%name%", fileName);
                return;
            }

            // Read only when the notification asked for it: the header is present on every download of a
            // user who enabled the option (folder syncs included), and only the flag makes it actionable.
            String openFor = open ? this.readOpenTarget(result) : null;

            this.reloadOnMainThread(fileName, subPath, target, backup, hash, openFor);
        });
    }

    /**
     * The player a freshly synced inventory must be opened for, taken from the authenticated download
     * response. Re-validated here (defence in depth) with the same character set the website enforces, so
     * a rogue intermediary cannot smuggle anything odd into a player lookup.
     */
    private String readOpenTarget(HttpRequest.DownloadResult result) {
        String value = result.header(OPEN_FOR_HEADER);
        if (value == null) {
            this.warning("The sync asked to open the menu in game, but the website sent no player name (" + OPEN_FOR_HEADER + " missing).");
            return null;
        }

        String name = value.trim();
        if (!OPEN_TARGET_PATTERN.matcher(name).matches()) {
            this.warning("Ignored the open-after-sync request: invalid player name '" + name + "'.");
            return null;
        }
        return name;
    }

    // ------------------------------------------------------------------ //
    // Helpers
    // ------------------------------------------------------------------ //

    /**
     * Human-readable cause for a failed download, by final HTTP status (after the retry layer gave up),
     * so the operator isn't always told "token revoked" when the real cause was a rate limit or a timeout.
     *
     * @param code The last HTTP status code, or -1 for a transport error (timeout / connection reset).
     * @return A short explanation of the failure.
     */
    private String describeDownloadFailure(int code) {
        if (code == 429) {
            return "rate-limited by the website even after retries (HTTP 429). Too many inventories synced at once";
        }
        if (code == 401 || code == 403) {
            return "unauthorized (HTTP " + code + "). The link token was revoked/expired, or this inventory isn't owned by the linked account";
        }
        if (code == 404) {
            return "not found (HTTP 404). The inventory no longer exists on the website";
        }
        if (code == -1) {
            return "could not reach the website after retries (timeout / connection error)";
        }
        if (code >= 500) {
            return "website error (HTTP " + code + ") even after retries";
        }
        return "HTTP " + code;
    }

    /**
     * Reloads the inventory manager on the main thread.
     *
     * @param fileName The name of the inventory file.
     * @param subPath  The sub-path of the inventory file.
     * @param target   The target file.
     * @param backup   The backup file.
     * @param hash     The hash of the inventory file.
     * @param openFor  The player the reloaded inventory must be opened for, or null.
     */
    private void reloadOnMainThread(String fileName, String subPath, File target, File backup, String hash, String openFor) {
        String label = subPath.isEmpty() ? fileName : subPath + "/" + fileName;
        this.plugin.getScheduler().runNextTick(w -> {
            InventoryManager inventoryManager = this.plugin.getInventoryManager();
            YamlFileCache.invalidateCache(target.toPath());

            boolean applied;
            try {
                // zMenu's registry is keyed by bare file name (no path), so resolve by the actual TARGET
                // file - a bare-name lookup could return a same-named inventory from another folder and
                // reload the wrong file.
                Optional<Inventory> existing = this.findInventoryByFile(inventoryManager, target);
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
                this.severe("zMenu failed to load the synced inventory '" + label + "': " + exception.getMessage() + ".");
                if (Configuration.enableDebug) {
                    exception.printStackTrace();
                }
            }

            if (applied) {
                this.lastAppliedHash.put((subPath + "/" + fileName).toLowerCase(Locale.ROOT), hash);
                this.success("Inventory '" + label + "' synced and reloaded.");
                message(this.plugin, Bukkit.getConsoleSender(), Message.WEBSITE_SYNC_APPLIED, "%name%", label);

                // Only after a successful reload: opening the previous version would be misleading, and
                // opening a rolled-back one plainly wrong.
                if (openFor != null) {
                    this.openForPlayer(inventoryManager, target, label, openFor);
                }
            } else {
                this.rollback(inventoryManager, target, backup);
                this.warning("Rolled back '" + label + "' to the previous version.");
                message(this.plugin, Bukkit.getConsoleSender(), Message.WEBSITE_SYNC_APPLY_ERROR, "%name%", label);
            }
        });
    }

    /**
     * Force the freshly reloaded inventory open for a player, the way {@code /zmenu open <menu> <player>}
     * would. Nothing is reported back to the website (the relay is one-way), so an offline player is only
     * a console warning.
     *
     * @param inventoryManager The inventory manager.
     * @param target           The file that was just written - the inventory is resolved from it, not from
     *                         its bare name, which is ambiguous across subfolders.
     * @param label            The human-readable path/name, for logging.
     * @param playerName       The exact name of the player to open it for.
     */
    private void openForPlayer(InventoryManager inventoryManager, File target, String label, String playerName) {
        Player player = Bukkit.getPlayerExact(playerName);
        if (player == null || !player.isOnline()) {
            this.warning("Could not open '" + label + "': " + playerName + " is not online.");
            message(this.plugin, Bukkit.getConsoleSender(), Message.WEBSITE_SYNC_OPEN_OFFLINE, "%name%", label, "%player%", playerName);
            return;
        }

        Optional<Inventory> optional = this.findInventoryByFile(inventoryManager, target);
        if (optional.isEmpty()) {
            this.warning("Could not open '" + label + "' for " + player.getName() + ": the inventory is not loaded.");
            return;
        }

        Inventory inventory = optional.get();
        // Folia: anything touching a player must run on that player's region thread (the reload itself
        // runs on the global one).
        this.plugin.getScheduler().runAtEntity(player, w -> {
            try {
                inventoryManager.openInventory(player, inventory);
                this.success("Inventory '" + label + "' opened for " + player.getName() + ".");
                message(this.plugin, Bukkit.getConsoleSender(), Message.WEBSITE_SYNC_OPENED, "%name%", label, "%player%", player.getName());
            } catch (Exception exception) {
                this.severe("Failed to open '" + label + "' for " + player.getName() + ": " + exception.getMessage() + ".");
                if (Configuration.enableDebug) {
                    exception.printStackTrace();
                }
            }
        });
    }

    /**
     * Find the loaded inventory whose backing file is exactly {@code target} (canonical comparison),
     * regardless of its name - zMenu indexes by bare file name, which is ambiguous across subfolders.
     */
    private Optional<Inventory> findInventoryByFile(InventoryManager inventoryManager, File target) {
        File canonicalTarget = this.canonical(target);
        for (Inventory inventory : inventoryManager.getInventories()) {
            File file = inventory.getFile();
            if (file != null && this.canonical(file).equals(canonicalTarget)) {
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
            Optional<Inventory> old = this.findInventoryByFile(inventoryManager, target);
            if (old.isPresent()) {
                inventoryManager.reloadInventory(old.get());
            } else {
                inventoryManager.loadInventory(this.plugin, target);
            }
        } catch (Exception exception) {
            this.severe("Rollback of '" + target.getName() + "' failed: " + exception.getMessage() + ".");
        }
    }
}
