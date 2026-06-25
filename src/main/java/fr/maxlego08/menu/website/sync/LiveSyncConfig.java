package fr.maxlego08.menu.website.sync;

import fr.maxlego08.menu.zcore.utils.storage.Persist;

/**
 * Persisted state of the zMenu live-sync link (plugins/zMenu/live-sync.json).
 * <p>
 * Holds the scoped server token, the opaque connection id and the WebSocket relay url returned by the
 * website during pairing. The token is a credential — it must never be printed to chat/console/logs.
 */
public class LiveSyncConfig {

    /** Scoped Sanctum token (abilities zmenu:sync, zmenu:download). Treat as a password. */
    public String token;

    /** WebSocket relay url to connect to, e.g. wss://ws.example.com/ws (handed over at pairing time). */
    public String wsUrl;

    /** Opaque connection id (informational — the relay derives it from the token on connect). */
    public String connectionId;

    /**
     * Linked = pairing succeeded; we hold a token and the relay url needed to (re)connect on demand.
     */
    public boolean isLinked() {
        return this.token != null && this.wsUrl != null;
    }

    public void save(Persist persist) {
        persist.save(this, "live-sync");
    }
}
