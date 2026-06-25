# zMenu Live Sync — Plugin Implementation Prompt / Spec

> **Companion to the website spec** (in the Minecraft-Inventory-Builder repo at
> `docs/zmenu-live-sync-plan.md`). This document covers the **plugin (this repo) side**.
> Read both before coding. **Security is the top priority.**
>
> **Status:** Not started. The website already has an inert "Sync to Server" button.

## 1. Objective (plugin side)

Add a live link so that when the website pushes a notification, the plugin **downloads the inventory
and reloads it in zMenu — live, no restart**:

1. `/zmenu login` — link the server to the website account *(ALREADY EXISTS, see §2)*.
2. `/zmenu connect` — open a persistent **WebSocket** to the site *(NEW)*.
3. On a pushed `{inventory_id, file_name, hash}` message → download the YAML over the existing
   authenticated API → **verify the hash** → atomic write into `inventories/` → **reload that single
   inventory** → tell the player.

## 2. What ALREADY exists in this repo (grounded — verified)

**Good news: most of the plumbing is here.** Reuse it; don't reinvent.

| Capability | Where | Reuse for |
|---|---|---|
| Website account link `/zmenu login <token>` | `command/commands/.../CommandMenuLogin` → `ZWebsiteManager.login()` | The "login" step the user described. |
| Bearer token storage | `website/Token.java` (singleton) → `plugins/zMenu/token.json` via `Persist` (GSON) | Store the server token; add connection metadata next to it. |
| HTTP client (Bearer, async, file download) | `website/request/HttpRequest.java` (`setBearer`, `submit`, **`submitForFileDownload(plugin, file, cb)`**) | Download the inventory YAML. **No new HTTP lib needed.** |
| Website manager | `website/ZWebsiteManager.java` (`login`, fetch inventories, download) | Add WS lifecycle + connect here (or a new `WebSocketManager`). |
| API base URL | `https://minecraft-inventory-builder.com/api/v1/` | Same host; WS endpoint is separate (see §4). |
| Existing API calls | `/auth/test`, `/resources`, `/inventories`, **`/inventory/{id}/download`** | The download endpoint is the data path — reuse it. |
| **Single-inventory reload** | `ZInventoryManager.reloadInventory(Inventory)` (deletes from cache + `loadInventory(plugin, file)`) and `loadInventory(Plugin, File)` for a brand-new file | The "reload live" step. |
| Inventory lookup | `getInventory(String fileName)` (case-insensitive); files at `plugins/zMenu/inventories/<name>.yml`; name == file name | Resolve target inventory / file. |
| Close open viewers before reload | pattern in `CommandMenuReloadInventory` (`getVInventoryManager().close(...)`) | Avoid stale open GUIs on reload. |
| Commands | `VCommand` base + register in `CommandMenu` ctor; `Permission` enum (`ZMENU_LOGIN`, `ZMENU_DOWNLOAD`, `ZMENU_RELOAD`) | Add `CommandMenuConnect` (+ `ZMENU_CONNECT` perm). |
| Messaging | `Message` enum (API module) + `this.message(plugin, sender, Message.KEY, "%x%", v)` (MiniMessage + legacy) | In-game pairing/sync feedback. |
| Scheduler (Folia-safe) | FoliaLib `PlatformScheduler` via `plugin.getScheduler()` (`runAsync`, `runAtEntity`, `runTask`, `runTimer`) | Async net I/O; main-thread reload. |
| Persistence | `zcore/utils/storage/Persist` (`save`/`load`/`loadOrSaveDefault`, GSON, data folder) | Store connection config. |
| Lifecycle | `ZMenuPlugin` (`onEnable`/`onDisable`, managers as fields, `getWebsiteManager/getInventoryManager/getScheduler/getPersist`) | Start/stop the WS manager. |

**MUST be added:** a WebSocket client library (none present). See §3.

**Stack facts:** Java 25 toolchain (target Spigot `26.1-R0.1`/Paper `1.21.11`, NMS down to 1.20.3), Gradle Kotlin
DSL + Shadow JAR (`com.gradleup.shadow` v9), relocations under `fr.maxlego08.menu.hooks.*`. Gson available
(not shaded). Adventure available (not shaded). Package root `fr.maxlego08.menu`. Z-prefix for impls; public
interfaces go in the **API** module.

## 3. Build change — add a WebSocket client

Pick a transport consistent with the website spec (**Soketi/Pusher protocol recommended**, Laravel 10 can't use Reverb):

- **Pusher protocol:** add `com.pusher:pusher-java-client` (pulls `org.java-websocket`). Works with Soketi/Pusher; native private-channel auth.
- **Raw WS (custom-server alternative):** add `org.java-websocket:Java-WebSocket`.

Steps: declare in `gradle/libs.versions.toml` (`[versions]` + `[libraries]`), add to root `build.gradle.kts`
dependencies, and **relocate** in the shadowJar block, e.g.
`org.java_websocket → fr.maxlego08.menu.hooks.java_websocket` (and the pusher client package). Verify the
shaded jar still builds (`./gradlew clean build`, output `target/zMenu.jar`).

> ⚠️ Confirm the transport with the website side before adding the dep — it determines the connect/auth code.

## 4. New command: `/zmenu connect` (+ `/zmenu disconnect`)

Mirror `CommandMenuLogin`. New `CommandMenuConnect extends VCommand`:
- `addSubCommand("connect")`, `setPermission(Permission.ZMENU_CONNECT)`, `setDescription(Message.DESCRIPTION_CONNECT)`, console-allowed (it's a server-level action).
- `perform()`: if no token → tell the user to run `/zmenu login` first; else call `webSocketManager.connect(sender)`.
- Register both in `CommandMenu` ctor next to `CommandMenuLogin`. Add `ZMENU_CONNECT` to `Permission`, and the new `Message` keys (EN + FR).

Connect flow (in a new `WebSocketManager` under `website/`, or in `ZWebsiteManager`):
1. `runAsync`: `GET /api/v1/zmenu/connection-info` (Bearer) → `{ transport params (pusher key/host/port/cluster or ws_url), connection_id, channel }`. Server registers/refreshes the connection.
2. Open **WSS** to the endpoint; subscribe to the **private** channel `private-zmenu-conn.{opaqueId}`. Channel auth: the client calls the site's channel-auth endpoint (`/api/v1/zmenu/ws-auth`) with the **Bearer token**; the site returns the Pusher signature. Reject on mismatch.
3. On connect: mark connected, log, message the sender. Start heartbeat; reconnect with exponential backoff on drop. Persist "should be connected" so the WS auto-starts on `onEnable` if a token exists.
4. `/zmenu disconnect` and `onDisable` cleanly close the socket.

## 5. Notification → download → reload pipeline (the core)

On a `inventory.sync` message `{inventory_id, file_name, hash}` (all async until the reload):

1. **Download** via the existing client: `GET /api/v1/inventory/{inventory_id}/download` with the Bearer
   token (reuse `HttpRequest.submitForFileDownload` into a **temp file** in the data folder). The site token
   must carry the right ability (website spec adds `zmenu:download`).
2. **Verify integrity:** compute SHA-256 over the downloaded bytes; compare to `hash` from the message.
   On mismatch → **abort**, delete temp, log + alert. (Prevents stale/tampered application.)
3. **Validate:** enforce a max file size; optionally `YamlConfiguration.loadConfiguration` the temp file to
   confirm it parses before swapping. Never execute downloaded content.
4. **Atomic swap with backup:** target `plugins/zMenu/inventories/<file_name>.yml`. Back up the existing file
   to `<file_name>.yml.bak`, then move temp → target (atomic rename, same filesystem). On any failure,
   restore the backup.
5. **Reload on the MAIN thread:** `plugin.getScheduler().runTask(...)` (or `runAtEntity` if tied to a player):
   - close open viewers of that inventory (the `CommandMenuReloadInventory` close pattern),
   - if `getInventory(file_name)` is present → `reloadInventory(inventory)`; else → `loadInventory(this, file)` (new inventory),
   - wrap in try/catch; on `InventoryException` **roll back** to `<file_name>.yml.bak` and reload the old one.
6. **Feedback:** message the player/console (success: name + version; failure: reason). Optionally ACK back to the site over the WS (`{inventory_id, result}`) for the website's sync history.

## 6. Persistence (extend the existing pattern)

- Reuse `Token`/`token.json` for the bearer token (already done by `/zmenu login`).
- Add a small `ConnectionConfig` persisted via `Persist` (e.g. `connection.json`): `connectionId`, `channel`,
  `autoConnect` (bool), last-known transport params (refreshable). **Never log the token**; restrict file perms where the OS allows.

## 7. Security model (plugin side — ultra secure)

- **Transport:** **WSS only** (TLS); HTTPS for all API calls. Refuse plaintext.
- **Token:** keep it only in `token.json`; never print it (chat/console/logs); redact in errors. Treat as a
  password. Support `/zmenu disconnect` + a server-pushed **"revoked"** message → drop token + stop the WS.
- **Channel:** subscribe only to the opaque `connection_id` channel returned by the site; never derive it from
  the user id. Channel auth is server-validated.
- **Payload trust:** the WS message carries only ids + hash. The plugin **pulls** the YAML over the
  authenticated API and **verifies the hash** before applying — the WS is not trusted to deliver content.
- **Fail-safe:** hash mismatch, oversize, parse failure, or reload exception ⇒ **do not apply / roll back**;
  the previously-running inventory keeps working. Never crash the server on a bad payload.
- **Thread safety:** all net I/O off the main thread; inventory mutation/reload **only** on the main thread
  via the scheduler. Folia-safe (use `PlatformScheduler`).
- **Rate/abuse:** debounce duplicate `inventory.sync` for the same `{inventory_id, hash}` (no-op if the file
  already matches the hash); backoff on reconnect; cap reconnect attempts/log noise.
- **Optional hardening (matches site spec stretch goal):** generate an Ed25519 keypair on first connect,
  register the public key with the site, and **sign** API requests (proof-of-possession) so a stolen token
  alone can't be replayed.

## 8. Lifecycle wiring (`ZMenuPlugin`)

- Field: `private WebSocketManager webSocketManager;` (or fold into `ZWebsiteManager`).
- `onEnable()`: after the token load (`Token` from `token.json`), if a token + `autoConnect` exist →
  `webSocketManager.connect(console)` (async). Register the new commands in `CommandMenu`.
- `onDisable()`: `webSocketManager.disconnect()` (close socket, cancel timers) before saving the token.
- If a public surface is useful for other plugins, add a `ServerSyncManager` interface in the **API** module
  and register it via the ServiceManager (optional).

## 9. Phased delivery

1. **Build:** add + relocate the WS client; confirm `./gradlew clean build`.
2. **Connect:** `WebSocketManager` + `/zmenu connect`/`/zmenu disconnect` + `connection-info`/`ws-auth` calls; stable WSS connect with heartbeat + backoff; lifecycle wiring.
3. **Sync pipeline:** message handler → download (reuse `HttpRequest`) → SHA-256 verify → atomic write + backup → main-thread reload (with viewer-close + rollback) → feedback + optional ACK.
4. **Hardening & QA:** revocation kills the live link, hash-mismatch rejected, malformed-YAML rolled back, Folia tested, reconnect storms bounded. Optional request signing.
5. **Docs & changelog:** add an `# Unreleased` entry in `changelog.md`; update the Docusaurus docs (EN + FR) at `C:\Users\Admin\Desktop\groupez\documentation` describing `/zmenu connect` + live sync.

## 10. Open questions (confirm with the website side — shared contract)

- **Login flow:** keep the existing **`/zmenu login <token>`** (paste a scoped, revocable Sanctum token from
  the account page) — simpler, already built — **or** upgrade to the website spec's **RFC 8628 device flow**
  (no token paste, more secure UX)? `/zmenu connect` works either way. *(Recommendation: ship with the
  existing token login + scope/expiry/revocation; offer device flow as a follow-up.)*
- **Transport:** Soketi (self-host) vs hosted Pusher vs custom WS server — must match the site.
- **Channel-auth & connection-info endpoints:** confirm exact URLs/shapes (`/api/v1/zmenu/connection-info`,
  `/api/v1/zmenu/ws-auth`) and the message schema (`{type, inventory_id, file_name, hash}`).
- **Download ability:** confirm the server token's Sanctum abilities allow `/inventory/{id}/download`
  (website spec adds `zmenu:download`).
- **Multi-server & target:** does a sync target one server or all of the user's connected servers?
- **ACK channel:** does the site want a result ACK for its sync history?
